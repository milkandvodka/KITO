package com.kito.feature.gpa.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.feature.gpa.domain.model.StudentProfile
import com.kito.feature.gpa.domain.repository.GpaRepository
import com.kito.feature.gpa.presentation.components.calculateCGPA
import com.kito.feature.gpa.presentation.components.calculateSGPAOrNull
import com.kito.feature.gpa.presentation.components.gpaDatabase
import com.kito.feature.gpa.presentation.components.grades
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class GPASubjectState(
    val id: String,
    val name: String,
    val credits: Int,
    val creditText: String,
    val gradeIndex: Int,
    val nameError: String? = null,
    val creditError: String? = null,
)

data class DeletedSubjectState(
    val token: Long,
    val subject: GPASubjectState,
    val index: Int,
    val branch: String,
    val semester: Int,
    val generation: Long,
)

data class GPAUiState(
    val subjects: List<GPASubjectState> = emptyList(),
    val totalCredits: Int = 0,
    val subjectCount: Int = 0,
    val sgpa: Double? = null,
    val newSubjectName: String = "",
    val newSubjectCredits: String = "",
    val newSubjectGradeIndex: Int = 0,
    val newSubjectNameError: String? = null,
    val newSubjectCreditError: String? = null,
    val deletedSubject: DeletedSubjectState? = null,
    val previousCgpa: String = "",
    val completedSemesters: String = "",
    val manualCurrentSgpa: String = "",
    val useCalculatedSgpa: Boolean = false,
    val cgpa: Double? = 0.0,
)

private data class TemplateKey(
    val branch: String,
    val semester: Int,
)

class GPAViewmodel(
    prefs: PrefsRepository,
    private val gpaRepository: GpaRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    val roll = prefs.userRollFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    private val _student = MutableStateFlow<StudentProfile?>(null)
    val student = _student.asStateFlow()

    private val _branch = MutableStateFlow("CSE")
    val branch = _branch.asStateFlow()

    private val _semester = MutableStateFlow(1)
    val semester = _semester.asStateFlow()

    private val _uiState = MutableStateFlow(GPAUiState())
    val uiState = _uiState.asStateFlow()

    private val editableSubjects = mutableMapOf<TemplateKey, List<GPASubjectState>>()
    private var nextCustomId = 1L
    private var nextUndoToken = 1L
    private var generation = 0L
    private var profileDefaultsConfigured = false
    private var userInteracted = false

    init {
        loadSubjectsFor(_branch.value, _semester.value, invalidateUndo = true)
        viewModelScope.launch(dispatcher) {
            roll.collect { rollNumber ->
                if (rollNumber.isEmpty()) return@collect
                val profile = gpaRepository.getStudentProfile(rollNumber)
                _student.value = profile
                if (!profileDefaultsConfigured && !userInteracted) {
                    profileDefaultsConfigured = true
                    configureGpaDefaults(rollNumber, profile)
                }
            }
        }
    }

    private fun configureGpaDefaults(roll: String, profile: StudentProfile?) {
        val defaultBranch = profile?.section?.substringBefore("-") ?: "CSE"
        val defaultSemester = deriveSemesterFromRoll(roll)
        _branch.value = defaultBranch
        _semester.value = defaultSemester
        loadSubjectsFor(defaultBranch, defaultSemester, invalidateUndo = true)
    }

    private fun deriveSemesterFromRoll(roll: String): Int {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentYear = now.year
        val month = now.month.number
        val joinYear = ("20" + roll.take(2)).toInt()
        val yearDiff = currentYear - joinYear
        val term = if (month in 7..11) "010" else "020"
        return when (yearDiff) {
            1 -> if (term == "010") 1 else 2
            2 -> if (term == "010") 3 else 4
            3 -> if (term == "010") 5 else 6
            4 -> if (term == "010") 7 else 8
            else -> 1
        }
    }

    fun onEvent(event: GPAEvent) {
        userInteracted = true
        when (event) {
            is GPAEvent.UpdateSemester -> updateSemester(event.semester)
            is GPAEvent.UpdateBranch -> updateBranch(event.branch)
            is GPAEvent.UpdateSubjectName -> updateSubjectName(event.id, event.name)
            is GPAEvent.UpdateSubjectCredits -> updateSubjectCredits(event.id, event.credits)
            is GPAEvent.UpdateSubjectGrade -> updateSubjectGrade(event.id, event.gradeIndex)
            is GPAEvent.DeleteSubject -> deleteSubject(event.id)
            is GPAEvent.UndoDelete -> undoDelete(event.token)
            GPAEvent.ResetSubjects -> resetSubjects()
            is GPAEvent.UpdateNewSubjectName -> updateNewSubjectName(event.name)
            is GPAEvent.UpdateNewSubjectCredits -> updateNewSubjectCredits(event.credits)
            is GPAEvent.UpdateNewSubjectGrade -> updateNewSubjectGrade(event.gradeIndex)
            GPAEvent.AddSubject -> addSubject()
            is GPAEvent.UpdatePreviousCgpa -> updatePreviousCgpa(event.value)
            is GPAEvent.UpdateCompletedSemesters -> updateCompletedSemesters(event.value)
            is GPAEvent.UpdateManualCurrentSgpa -> updateManualCurrentSgpa(event.value)
            is GPAEvent.UseCalculatedSgpa -> updateUseCalculatedSgpa(event.enabled)
        }
    }

    private fun updateSemester(semester: Int) {
        _semester.value = semester
        loadSubjectsFor(_branch.value, semester, invalidateUndo = true)
    }

    private fun updateBranch(branch: String) {
        _branch.value = branch
        loadSubjectsFor(branch, _semester.value, invalidateUndo = true)
    }

    private fun updateSubjectName(id: String, name: String) {
        val updated = _uiState.value.subjects.map { subject ->
            if (subject.id == id) subject.copy(
                name = name,
                nameError = validateSubjectName(name)
            ) else subject
        }
        commitSubjects(updated)
    }

    private fun updateSubjectCredits(id: String, credits: String) {
        val creditResult = validateCredits(credits)
        val updated = _uiState.value.subjects.map { subject ->
            if (subject.id == id) {
                subject.copy(
                    creditText = credits,
                    credits = creditResult.value ?: subject.credits,
                    creditError = creditResult.error
                )
            } else {
                subject
            }
        }
        commitSubjects(updated)
    }

    private fun updateSubjectGrade(id: String, gradeIndex: Int) {
        val supportedGrade = gradeIndex.coerceIn(grades.indices)
        val updated = _uiState.value.subjects.map { subject ->
            if (subject.id == id) subject.copy(gradeIndex = supportedGrade) else subject
        }
        commitSubjects(updated)
    }

    private fun deleteSubject(id: String) {
        val current = _uiState.value.subjects
        val index = current.indexOfFirst { it.id == id }
        if (index == -1) return

        val key = TemplateKey(_branch.value, _semester.value)
        val deleted = DeletedSubjectState(
            token = nextUndoToken++,
            subject = current[index],
            index = index,
            branch = key.branch,
            semester = key.semester,
            generation = generation,
        )
        val updated = current.toMutableList().apply { removeAt(index) }
        commitSubjects(updated, deletedSubject = deleted)
    }

    private fun undoDelete(token: Long) {
        val deleted = _uiState.value.deletedSubject ?: return
        if (
            deleted.token != token ||
            deleted.branch != _branch.value ||
            deleted.semester != _semester.value ||
            deleted.generation != generation
        ) return

        val restored = _uiState.value.subjects.toMutableList()
        val index = deleted.index.coerceIn(0, restored.size)
        if (restored.none { it.id == deleted.subject.id }) {
            restored.add(index, deleted.subject)
        }
        commitSubjects(restored, deletedSubject = null)
    }

    private fun resetSubjects() {
        val key = TemplateKey(_branch.value, _semester.value)
        editableSubjects.remove(key)
        loadSubjectsFor(key.branch, key.semester, invalidateUndo = true)
        _uiState.update {
            it.copy(
                newSubjectName = "",
                newSubjectCredits = "",
                newSubjectGradeIndex = 0,
                newSubjectNameError = null,
                newSubjectCreditError = null,
            )
        }
    }

    private fun updateNewSubjectName(name: String) {
        _uiState.update {
            it.copy(
                newSubjectName = name,
                newSubjectNameError = validateSubjectName(name)
            )
        }
    }

    private fun updateNewSubjectCredits(credits: String) {
        _uiState.update {
            it.copy(
                newSubjectCredits = credits,
                newSubjectCreditError = validateCredits(credits).error
            )
        }
    }

    private fun updateNewSubjectGrade(gradeIndex: Int) {
        _uiState.update { it.copy(newSubjectGradeIndex = gradeIndex.coerceIn(grades.indices)) }
    }

    private fun addSubject() {
        val state = _uiState.value
        val nameError = validateSubjectName(state.newSubjectName)
        val creditResult = validateCredits(state.newSubjectCredits)
        if (nameError != null || creditResult.value == null) {
            _uiState.update {
                it.copy(
                    newSubjectNameError = nameError,
                    newSubjectCreditError = creditResult.error ?: "Credits are required"
                )
            }
            return
        }

        val subject = GPASubjectState(
            id = "${_branch.value}-${_semester.value}-custom-${nextCustomId++}",
            name = state.newSubjectName.trim(),
            credits = creditResult.value,
            creditText = creditResult.value.toString(),
            gradeIndex = state.newSubjectGradeIndex.coerceIn(grades.indices),
        )
        commitSubjects(
            subjects = state.subjects + subject,
            deletedSubject = null,
            transform = {
                it.copy(
                    newSubjectName = "",
                    newSubjectCredits = "",
                    newSubjectGradeIndex = 0,
                    newSubjectNameError = null,
                    newSubjectCreditError = null,
                )
            }
        )
    }

    private fun updatePreviousCgpa(value: String) {
        _uiState.update { recalculate(it.copy(previousCgpa = value)) }
    }

    private fun updateCompletedSemesters(value: String) {
        _uiState.update { recalculate(it.copy(completedSemesters = value)) }
    }

    private fun updateManualCurrentSgpa(value: String) {
        _uiState.update { recalculate(it.copy(manualCurrentSgpa = value)) }
    }

    private fun updateUseCalculatedSgpa(enabled: Boolean) {
        _uiState.update { recalculate(it.copy(useCalculatedSgpa = enabled)) }
    }

    private fun loadSubjectsFor(
        branch: String,
        semester: Int,
        invalidateUndo: Boolean,
    ) {
        if (invalidateUndo) generation++
        val key = TemplateKey(branch, semester)
        val subjects = editableSubjects.getOrPut(key) { templateSubjects(branch, semester) }
        _uiState.update {
            recalculate(
                it.copy(
                    subjects = subjects,
                    deletedSubject = if (invalidateUndo) null else it.deletedSubject
                )
            )
        }
    }

    private fun commitSubjects(
        subjects: List<GPASubjectState>,
        deletedSubject: DeletedSubjectState? = _uiState.value.deletedSubject,
        transform: (GPAUiState) -> GPAUiState = { it },
    ) {
        val key = TemplateKey(_branch.value, _semester.value)
        editableSubjects[key] = subjects
        _uiState.update {
            transform(
                recalculate(
                    it.copy(
                        subjects = subjects,
                        deletedSubject = deletedSubject,
                    )
                )
            )
        }
    }

    private fun recalculate(state: GPAUiState): GPAUiState {
        val totalCredits = state.subjects.sumOf { it.credits }
        val sgpa = calculateSGPAOrNull(
            state.subjects.map { it.credits to gradePointForIndex(it.gradeIndex) }
        )
        val currentSgpa = if (state.useCalculatedSgpa) {
            sgpa ?: return state.copy(
                totalCredits = totalCredits,
                subjectCount = state.subjects.size,
                sgpa = sgpa,
                cgpa = null,
            )
        } else {
            state.manualCurrentSgpa.toDoubleOrNull() ?: 0.0
        }
        return state.copy(
            totalCredits = totalCredits,
            subjectCount = state.subjects.size,
            sgpa = sgpa,
            cgpa = calculateCGPA(
                oldCgpa = state.previousCgpa.toDoubleOrNull() ?: 0.0,
                completedSem = state.completedSemesters.toIntOrNull() ?: 0,
                currentSgpa = currentSgpa,
            )
        )
    }

    private fun templateSubjects(branch: String, semester: Int): List<GPASubjectState> {
        return gpaDatabase
            .firstOrNull { it.branch == branch && it.semester == semester }
            ?.subjects
            ?.mapIndexed { index, subject ->
                GPASubjectState(
                    id = "$branch-$semester-template-$index",
                    name = subject.name,
                    credits = subject.credit,
                    creditText = subject.credit.toString(),
                    gradeIndex = 0,
                )
            }
            .orEmpty()
    }

    private fun gradePointForIndex(index: Int): Int {
        return when (grades[index.coerceIn(grades.indices)]) {
            "O" -> 10
            "E" -> 9
            "A" -> 8
            "B" -> 7
            "C" -> 6
            "D" -> 5
            else -> 2
        }
    }

    private fun validateSubjectName(name: String): String? {
        return if (name.isBlank()) "Subject name is required" else null
    }

    private fun validateCredits(value: String): CreditValidation {
        if (value.isBlank()) return CreditValidation(error = "Credits are required")
        if (value.any { !it.isDigit() }) return CreditValidation(error = "Enter a whole number")
        val parsed = value.toLongOrNull() ?: return CreditValidation(error = "Credits are too large")
        if (parsed > Int.MAX_VALUE) return CreditValidation(error = "Credits are too large")
        if (parsed <= 0) return CreditValidation(error = "Credits must be greater than zero")
        return CreditValidation(value = parsed.toInt())
    }

    private data class CreditValidation(
        val value: Int? = null,
        val error: String? = null,
    )
}
