package com.kito.feature.gpa.presentation

sealed interface GPAEvent {
    data class UpdateSemester(val semester: Int) : GPAEvent
    data class UpdateBranch(val branch: String) : GPAEvent
    data class UpdateSubjectName(val id: String, val name: String) : GPAEvent
    data class UpdateSubjectCredits(val id: String, val credits: String) : GPAEvent
    data class UpdateSubjectGrade(val id: String, val gradeIndex: Int) : GPAEvent
    data class DeleteSubject(val id: String) : GPAEvent
    data class UndoDelete(val token: Long) : GPAEvent
    data object ResetSubjects : GPAEvent
    data class UpdateNewSubjectName(val name: String) : GPAEvent
    data class UpdateNewSubjectCredits(val credits: String) : GPAEvent
    data class UpdateNewSubjectGrade(val gradeIndex: Int) : GPAEvent
    data object AddSubject : GPAEvent
    data class UpdatePreviousCgpa(val value: String) : GPAEvent
    data class UpdateCompletedSemesters(val value: String) : GPAEvent
    data class UpdateManualCurrentSgpa(val value: String) : GPAEvent
    data class UseCalculatedSgpa(val enabled: Boolean) : GPAEvent
}
