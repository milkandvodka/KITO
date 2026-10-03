package com.kito.feature.friendview

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import com.kito.feature.friendview.domain.model.FriendSummary
import com.kito.feature.friendview.presentation.list.FriendListContent
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class FriendListUiTest {

    @Test
    fun friendList_rendersList_whenFriendsExist() = runComposeUiTest {
        val friends = listOf(
            FriendSummary(roll = "2205001", name = "Adrish", section = "CSE-48", elective1 = "AI", elective2 = "ML"),
            FriendSummary(roll = "2205002", section = "CSE-1")
        )
        setContent {
            FriendListContent(
                friends = friends,
                showAddDialog = false,
                onBack = {},
                onSelectFriend = {},
                onRemoveFriend = {},
                onAddFriendByRoll = { _, _ -> },
                onAddFriendBySection = { _, _, _, _, _, _ -> },
                onShowAddDialog = {}
            )
        }

        onNodeWithTag("friendview_content").assertIsDisplayed()
        onNodeWithText("Adrish").assertIsDisplayed()
        onNodeWithText("2205001").assertIsDisplayed()
        onNodeWithText("CSE-48 • AI, ML").assertIsDisplayed()
        onNodeWithText("2205002").assertIsDisplayed()
    }

    @Test
    fun friendList_rendersEmptyState_whenNoFriends() = runComposeUiTest {
        setContent {
            FriendListContent(
                friends = emptyList(),
                showAddDialog = false,
                onBack = {},
                onSelectFriend = {},
                onRemoveFriend = {},
                onAddFriendByRoll = { _, _ -> },
                onAddFriendBySection = { _, _, _, _, _, _ -> },
                onShowAddDialog = {}
            )
        }

        onNodeWithTag("friendview_content").assertIsDisplayed()
        onNodeWithTag("friendview_empty").assertIsDisplayed()
        onNodeWithText("No Friends Added").assertIsDisplayed()
    }
}
