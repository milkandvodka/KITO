package com.kito.feature.friendview.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.core.presentation.components.animation.PageNotFoundAnimation
import com.kito.feature.friendview.domain.model.FriendSummary
import com.kito.feature.friendview.presentation.components.AddFriendDialog
import com.kito.feature.friendview.presentation.components.FriendCard
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState

import com.kito.feature.schedule.domain.model.AvailableSectionsData

@OptIn(
    ExperimentalHazeApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun FriendListContent(
    friends: List<FriendSummary>,
    showAddDialog: Boolean,
    onBack: () -> Unit,
    onSelectFriend: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onAddFriendByRoll: (name: String, roll: String) -> Unit = { _, _ -> },
    onAddFriendBySection: (
        name: String,
        batch: String,
        branch: String,
        section: String,
        elective1: String,
        elective2: String
    ) -> Unit = { _, _, _, _, _, _ -> },
    onShowAddDialog: (Boolean) -> Unit,
    availableData: AvailableSectionsData = AvailableSectionsData(),
    isAddingFriend: Boolean = false,
    addFriendError: String? = null,
    modifier: Modifier = Modifier
) {
    val hazeState = rememberHazeState()
    val uiColors = UIColors()
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121116))
            .semantics { testTag = "friendview_content" }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121116))
                .hazeSource(hazeState)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 80.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (friends.isNotEmpty()) {
                    items(friends, key = { it.roll }) { friend ->
                        FriendCard(
                            friend = friend,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onSelectFriend(friend.roll)
                            },
                            onDelete = {
                                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onRemoveFriend(friend.roll)
                            },
                            uiColors = uiColors
                        )
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(480.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                uiColors.cardBackground,
                                                Color(0xFF2F222F),
                                                uiColors.cardBackgroundHigh
                                            )
                                        )
                                    )
                                    .padding(24.dp)
                                    .semantics { testTag = "friendview_empty" }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(modifier = Modifier.size(200.dp)) {
                                        PageNotFoundAnimation()
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No Friends Added",
                                        color = uiColors.textPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLargeEmphasized,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Add your friends' roll numbers to quickly view their schedules and electives.",
                                        color = uiColors.textSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(
                                        onClick = {
                                            haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                                            onShowAddDialog(true)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = uiColors.progressAccent,
                                            contentColor = uiColors.textPrimary
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Friend",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Add Friend",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.labelMediumEmphasized
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Top Bar
        Box(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                }
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(
                    modifier = Modifier.height(
                        16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = uiColors.progressAccent
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Friends",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = uiColors.textPrimary,
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis
                    )

                    if (friends.isNotEmpty()) {
                        Button(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onShowAddDialog(true)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = uiColors.progressAccent,
                                contentColor = uiColors.textPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Friend",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelSmallEmphasized
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (showAddDialog) {
            AddFriendDialog(
                availableData = availableData,
                isSubmitting = isAddingFriend,
                errorMessage = addFriendError,
                onDismiss = {
                    haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onShowAddDialog(false)
                },
                onConfirmRoll = { name, roll ->
                    haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onAddFriendByRoll(name, roll)
                },
                onConfirmSection = { name, batch, branch, section, el1, el2 ->
                    haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onAddFriendBySection(name, batch, branch, section, el1, el2)
                },
                hazeState = hazeState
            )
        }
    }
}
