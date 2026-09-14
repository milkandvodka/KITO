package com.kito.feature.auth.presentation.usersetup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kito.core.common.util.currentLocalDateTime
import com.kito.core.designsystem.UIColors
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import kito.composeapp.generated.resources.Res
import kito.composeapp.generated.resources.google
import kotlinx.datetime.number
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun UserSetupScreen(
    onSetupComplete: () -> Unit,
    userSetupViewModel: UserSetupViewModel = koinInject()
) {
    val setupState by userSetupViewModel.setupState.collectAsState()
    val loadingSource by userSetupViewModel.loadingSource.collectAsState()
    LaunchedEffect(setupState) {
        if (setupState is SetupState.Success) {
            onSetupComplete()
        }
    }

    val supabaseClient: SupabaseClient = koinInject()
    val googleSignIn = supabaseClient.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                is NativeSignInResult.Success -> Unit
                is NativeSignInResult.ClosedByUser -> userSetupViewModel.onSignInCancelled()
                is NativeSignInResult.Error -> userSetupViewModel.onSignInError(result.message)
                is NativeSignInResult.NetworkError -> userSetupViewModel.onSignInError(result.message)
            }
        }
    )

    UserSetupContent(
        setupState = setupState,
        loadingSource = loadingSource,
        onSubmit = { name, roll, year, term ->
            userSetupViewModel.completeSetup(
                name = name,
                roll = roll,
                year = year,
                term = term
            )
        },
        onGoogleSignIn = {
            userSetupViewModel.onSignInStarted()
            googleSignIn.startFlow()
        }
    )
}
