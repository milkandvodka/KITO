package com.kito.feature.friendview.presentation.list

sealed interface FriendListEvent {
    data class AddFriend(val roll: String) : FriendListEvent
    data class AddFriendByRoll(val name: String, val roll: String) : FriendListEvent
    data class AddFriendBySection(
        val name: String,
        val batch: String,
        val branch: String,
        val section: String,
        val elective1: String,
        val elective2: String
    ) : FriendListEvent
    data class RemoveFriend(val roll: String) : FriendListEvent
    data class ShowAddDialog(val show: Boolean) : FriendListEvent
    data object SyncOnOpen : FriendListEvent
}

