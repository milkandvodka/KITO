package com.kito.ui.newUi.screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kito.ui.components.FacultyCardContent
import com.kito.ui.components.UIColors
import com.kito.ui.components.state.SearchResultState
import com.kito.ui.navigation.Destinations
import com.kito.ui.newUi.viewmodel.FacultyScreenViewModel
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalHazeApi::class,
    ExperimentalHazeMaterialsApi::class, ExperimentalMaterial3ExpressiveApi::class,
    FlowPreview::class
)
@Composable
fun FacultyScreen(
    navController: NavHostController,
    viewModel: FacultyScreenViewModel = hiltViewModel()
) {
    val facultyList by viewModel.faculty.collectAsState()
    val searchResultState by viewModel.searchResultState.collectAsState()
    val facultySearchResult by viewModel.facultySearchResult.collectAsState()
    val uiColors = UIColors()
    val hazeState = rememberHazeState()
    val cardHaze = rememberHazeState()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val screenWidthDp = configuration.screenWidthDp.dp
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    if(
        searchBarState.currentValue == SearchBarValue.Expanded
    ){
        BackHandler {
            scope.launch {
                searchBarState.animateToCollapsed()
                viewModel.clearSearchResult()
                textFieldState.clearText()
            }
        }
    }
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                colors = SearchBarDefaults.inputFieldColors(
                    focusedContainerColor = uiColors.cardBackground,
                    unfocusedContainerColor = uiColors.cardBackground,
                ),
                modifier = Modifier
                    .width(
                        width = when (isPortrait) {
                            false -> screenWidthDp * 0.337f
                            true -> screenWidthDp
                        }
                    ),
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = {
                    keyboardController?.hide()
                },
                placeholder = {
                    Text(
                        text = "Search Faculty...",
                        style = MaterialTheme.typography.titleMediumEmphasized
                    )
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                searchBarState.animateToCollapsed()
                                viewModel.clearSearchResult()
                                textFieldState.clearText()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(uiColors.accentOrangeStart)
                            .clickable(onClick = {
                                keyboardController?.hide()
                            }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Search",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    var targetPadding by remember { mutableStateOf(0.dp) }
    val animatedPadding by animateDpAsState(
        targetValue = targetPadding,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "attendance"
    )
    LaunchedEffect(searchBarState.currentValue) {
        targetPadding = if (searchBarState.currentValue == SearchBarValue.Expanded){
            25.dp
        }else{
            0.dp
        }
    }
    LaunchedEffect(textFieldState) {
        launch {
            snapshotFlow { textFieldState.text.toString() }
                .debounce(300)
                .collect { query ->
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        viewModel.getSearchResult(query)
                    }
                }
        }
    }
    Box(
        modifier = Modifier.hazeSource(cardHaze)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 46.dp + animatedPadding,
                bottom = 86.dp + WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (searchBarState.currentValue != SearchBarValue.Expanded || searchResultState is SearchResultState.Idle) {
                itemsIndexed(facultyList) { index, faculty ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(
                            topStart = if (index == 0) 24.dp else 4.dp,
                            topEnd = if (index == 0) 24.dp else 4.dp,
                            bottomStart = if (index == facultyList.size - 1) 24.dp else 4.dp,
                            bottomEnd = if (index == facultyList.size - 1) 24.dp else 4.dp
                        ),
                        onClick = {
                            navController.navigate(Destinations.FacultyDetail(faculty.teacher_id)) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            uiColors.cardBackground,
                                            Color(0xFF2F222F),
                                            Color(0xFF2F222F),
                                            uiColors.cardBackgroundHigh
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    FacultyCardContent(
                                        facultyName = faculty.name,
                                        facultyOffice = faculty.office_room,
                                        facultyEmail = faculty.email
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        navController.navigate(Destinations.FacultyDetail(faculty.teacher_id)) {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Open",
                                        tint = uiColors.textSecondary,
                                        modifier = Modifier.size(35.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }else{
                itemsIndexed(facultySearchResult){index,faculty->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(
                            topStart = if (index == 0) 24.dp else 4.dp,
                            topEnd = if (index == 0) 24.dp else 4.dp,
                            bottomStart = if (index == facultySearchResult.size - 1) 24.dp else 4.dp,
                            bottomEnd = if (index == facultySearchResult.size - 1) 24.dp else 4.dp
                        ),
                        onClick = {
                            navController.navigate(Destinations.FacultyDetail(faculty.teacher_id)) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            uiColors.cardBackground,
                                            Color(0xFF2F222F),
                                            Color(0xFF2F222F),
                                            uiColors.cardBackgroundHigh
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    FacultyCardContent(
                                        facultyName = faculty.name,
                                        facultyOffice = faculty.office_room,
                                        facultyEmail = faculty.email
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        navController.navigate(Destinations.FacultyDetail(faculty.teacher_id)) {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Open",
                                        tint = uiColors.textSecondary,
                                        modifier = Modifier.size(35.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    alpha = 0.98f
                }
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(
                modifier = Modifier.height(
                    16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
            )
            Box(
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Faculty",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = uiColors.textPrimary,
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            scope.launch {
                                searchBarState.animateToExpanded()
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonSearch,
                            contentDescription = "Search",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                this@Column.AnimatedVisibility(
                    visible = searchBarState.currentValue == SearchBarValue.Expanded,
                    enter = fadeIn(
                        tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + expandVertically(
                        tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ),
                    exit = fadeOut(
                        tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + shrinkVertically(
                        tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ),
                ) {
                    SearchBar(
                        state = searchBarState,
                        inputField = inputField
                    )
                }
//                ExpandedFullScreenSearchBar(
//                    tonalElevation = 48.dp,
//                    state = searchBarState,
//                    inputField = inputField,
//                    colors = SearchBarDefaults.colors(
//                        containerColor = uiColors.cardBackground
//                    ),
//                ) {
//
//                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}