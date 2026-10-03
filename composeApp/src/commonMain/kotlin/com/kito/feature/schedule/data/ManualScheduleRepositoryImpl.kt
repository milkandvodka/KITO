package com.kito.feature.schedule.data

import com.kito.core.database.AppDB
import com.kito.core.database.entity.ActiveSessionEntity
import com.kito.core.database.entity.StudentElectiveEntity
import com.kito.core.database.entity.StudentEntity
import com.kito.core.database.repository.SectionRepository
import com.kito.core.database.repository.StudentRepository
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.core.network.supabase.request.MissingRollReportRequest
import com.kito.core.sync.data.SyncRemoteDataSource
import com.kito.feature.schedule.domain.model.AvailableSectionsData
import com.kito.feature.schedule.domain.model.ElectiveSlotOption
import com.kito.feature.schedule.domain.model.ManualScheduleConfig
import com.kito.feature.schedule.domain.repository.ManualScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.annotation.Provided

class ManualScheduleRepositoryImpl(
    @Provided private val syncRemoteDataSource: SyncRemoteDataSource,
    @Provided private val prefs: PrefsRepository,
    @Provided private val studentRepository: StudentRepository,
    @Provided private val sectionRepository: SectionRepository,
    @Provided private val db: AppDB
) : ManualScheduleRepository {

    override fun observeIsManualSchedule(): Flow<Boolean> =
        prefs.isManualScheduleFlow

    override fun observeManualScheduleConfig(): Flow<ManualScheduleConfig?> =
        combine(
            prefs.isManualScheduleFlow,
            prefs.manualSectionFlow,
            prefs.manualBatchFlow,
            prefs.manualElective1Flow,
            prefs.manualElective2Flow
        ) { isManual, section, batch, el1, el2 ->
            if (isManual && section.isNotBlank()) {
                ManualScheduleConfig(
                    section = section,
                    batch = batch,
                    elective1 = el1,
                    elective2 = el2
                )
            } else null
        }

    override suspend fun checkRollExists(rollNo: String): Boolean {
        if (rollNo.isBlank()) return false
        val student = syncRemoteDataSource.getStudentByRoll(rollNo)
        return student != null
    }

    override suspend fun getAvailableSections(): AvailableSectionsData {
        val metadata = runCatching {
            syncRemoteDataSource.getTimetableMetadata()
        }.getOrDefault(emptyList())

        val allStudentElectives = syncRemoteDataSource.getAllStudentElectives()
        val batches = listOf("batch_1", "batch_2", "batch_3", "batch_4")

        val branchesByBatch = mutableMapOf<String, List<String>>()
        val coreSectionsByBatchAndBranch = mutableMapOf<String, Map<String, List<String>>>()
        val electiveSlotsByBatch = mutableMapOf<String, List<ElectiveSlotOption>>()

        batches.forEach { batch ->
            val batchRows = metadata.filter { it.batch.equals(batch, ignoreCase = true) }
            val batchElectives = allStudentElectives.filter { it.batch.equals(batch, ignoreCase = true) }
            val allElectivesForBatch = batchElectives.flatMap { listOf(it.elective_1, it.elective_2) }
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toSet()

            // Non-elective classes for this particular batch
            val nonElectiveSections = batchRows
                .map { it.section.trim() }
                .filter { sec ->
                    sec.isNotBlank() &&
                    sec !in allElectivesForBatch &&
                    !sec.startsWith("elective", ignoreCase = true)
                }
                .distinct()

            // Dynamically extract branch/group names from non-elective classes
            val branches = nonElectiveSections
                .map { extractBranchName(it) }
                .filter { it.isNotBlank() && it !in allElectivesForBatch }
                .distinct()
                .sortedNaturally()

            branchesByBatch[batch] = branches

            val branchMap = mutableMapOf<String, List<String>>()
            branches.forEach { branch ->
                val sections = nonElectiveSections
                    .filter { sec ->
                        val rowBranch = extractBranchName(sec)
                        rowBranch.equals(branch, ignoreCase = true)
                    }
                    .sortedNaturally()
                branchMap[branch] = sections
            }
            coreSectionsByBatchAndBranch[batch] = branchMap

            val el1Sections = (batchRows.filter { it.source == "elective_1" }.map { it.section.trim() } +
                    batchElectives.map { it.elective_1.trim() })
                .filter { it.isNotBlank() }
                .distinct()
                .sortedNaturally()

            val el2Sections = (batchRows.filter { it.source == "elective_2" }.map { it.section.trim() } +
                    batchElectives.map { it.elective_2.trim() })
                .filter { it.isNotBlank() }
                .distinct()
                .sortedNaturally()

            val slots = mutableListOf<ElectiveSlotOption>()
            if (el1Sections.isNotEmpty()) {
                slots.add(ElectiveSlotOption("elective_1", "Elective 1", el1Sections))
            }
            if (el2Sections.isNotEmpty()) {
                slots.add(ElectiveSlotOption("elective_2", "Elective 2", el2Sections))
            }
            electiveSlotsByBatch[batch] = slots
        }

        return AvailableSectionsData(
            availableBatches = batches,
            branchesByBatch = branchesByBatch,
            coreSectionsByBatchAndBranch = coreSectionsByBatchAndBranch,
            electiveSlotsByBatch = electiveSlotsByBatch
        )
    }

    override suspend fun saveManualSchedule(
        rollNo: String,
        config: ManualScheduleConfig
    ): Result<Unit> = runCatching {
        val activeSession = syncRemoteDataSource.getActiveSessionConfig()

        val coreTimetable = syncRemoteDataSource.getTimetableForStudent(
            section = config.section.trim(),
            batch = config.batch.trim()
        ).map {
            it.copy(
                day = com.kito.feature.schedule.presentation.components.normalizeDay(it.day),
                academic_year = activeSession.academic_year,
                term_code = activeSession.term_code,
                version = activeSession.version,
                source = "core"
            )
        }

        val elective1Timetable = if (config.elective1.isNotBlank()) {
            syncRemoteDataSource.getTimetableForStudent(
                section = config.elective1.trim(),
                batch = config.batch.trim()
            ).map {
                it.copy(
                    day = com.kito.feature.schedule.presentation.components.normalizeDay(it.day),
                    academic_year = activeSession.academic_year,
                    term_code = activeSession.term_code,
                    version = activeSession.version,
                    source = "elective_1"
                )
            }
        } else emptyList()

        val elective2Timetable = if (config.elective2.isNotBlank()) {
            syncRemoteDataSource.getTimetableForStudent(
                section = config.elective2.trim(),
                batch = config.batch.trim()
            ).map {
                it.copy(
                    day = com.kito.feature.schedule.presentation.components.normalizeDay(it.day),
                    academic_year = activeSession.academic_year,
                    term_code = activeSession.term_code,
                    version = activeSession.version,
                    source = "elective_2"
                )
            }
        } else emptyList()

        val allTimetable = (coreTimetable + elective1Timetable + elective2Timetable).mapIndexed { index, entity ->
            entity.copy(
                id = if (entity.id != 0) entity.id else (index + 1)
            )
        }

        // Save to DataStore
        prefs.saveManualSchedule(
            section = config.section,
            batch = config.batch,
            elective1 = config.elective1,
            elective2 = config.elective2
        )

        // Save synthetic student, sections, active session and electives to Room
        // First delete previous sections and electives for a clean slate
        sectionRepository.deleteAllSection()
        db.studentElectiveDao().deleteStudentElective(rollNo)

        studentRepository.insertStudent(
            listOf(
                StudentEntity(
                    roll_no = rollNo,
                    section = config.section,
                    batch = config.batch
                )
            )
        )
        sectionRepository.insertSection(allTimetable)
        db.activeSessionDao().insertActiveSession(
            ActiveSessionEntity(
                academic_year = activeSession.academic_year,
                term_code = activeSession.term_code,
                version = activeSession.version
            )
        )
        if (config.elective1.isNotBlank() || config.elective2.isNotBlank()) {
            db.studentElectiveDao().upsertStudentElective(
                StudentElectiveEntity(
                    roll_no = rollNo,
                    elective_1 = config.elective1,
                    elective_2 = config.elective2,
                    batch = config.batch
                )
            )
        }

        // Post to Supabase missing_roll table in background
        syncRemoteDataSource.reportMissingRoll(
            MissingRollReportRequest(
                roll_no = rollNo,
                section = config.section,
                batch = config.batch,
                elective_1 = config.elective1.ifBlank { null },
                elective_2 = config.elective2.ifBlank { null }
            )
        )
    }

    override suspend fun clearManualSchedule(): Result<Unit> = runCatching {
        prefs.clearManualSchedule()
        sectionRepository.deleteAllSection()
        db.studentElectiveDao().deleteAllStudentElectives()
    }
}

private fun extractBranchName(section: String): String {
    val trimmed = section.trim()
    return when {
        trimmed.contains("-") -> trimmed.substringBefore("-").trim()
        trimmed.contains(" ") -> trimmed.substringBefore(" ").trim()
        trimmed.any { it.isDigit() } -> {
            val letters = trimmed.takeWhile { !it.isDigit() }.trim()
            if (letters.isNotBlank()) letters else trimmed
        }
        else -> trimmed
    }
}

private val naturalSortComparator = Comparator<String> { a, b ->
    val re = Regex("(\\d+)|(\\D+)")
    val aMatches = re.findAll(a).map { it.value }.iterator()
    val bMatches = re.findAll(b).map { it.value }.iterator()
    while (aMatches.hasNext() && bMatches.hasNext()) {
        val aToken = aMatches.next()
        val bToken = bMatches.next()
        val aNum = aToken.toLongOrNull()
        val bNum = bToken.toLongOrNull()
        val res = if (aNum != null && bNum != null) {
            aNum.compareTo(bNum)
        } else {
            aToken.compareTo(bToken, ignoreCase = true)
        }
        if (res != 0) return@Comparator res
    }
    a.length.compareTo(b.length)
}

private fun Iterable<String>.sortedNaturally(): List<String> = sortedWith(naturalSortComparator)

