package com.zaryif.minimalos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.zaryif.minimalos.data.preferences.UserPreferences
import com.zaryif.minimalos.data.repository.AppRepository
import com.zaryif.minimalos.data.repository.StorageRepository
import com.zaryif.minimalos.domain.model.RestrictionMode
import com.zaryif.minimalos.domain.usecase.ExecuteCommandUseCase
import com.zaryif.minimalos.service.MinimalNotificationService
import com.zaryif.minimalos.ui.apps.AppDrawerScreen
import com.zaryif.minimalos.ui.components.MinimalAskSheet
import com.zaryif.minimalos.ui.components.MinimalClockSheet
import com.zaryif.minimalos.ui.components.MinimalMemoSheet
import com.zaryif.minimalos.ui.home.HomeScreen
import com.zaryif.minimalos.ui.hub.HubScreen
import com.zaryif.minimalos.ui.onboarding.OnboardingScreen
import com.zaryif.minimalos.ui.settings.SettingsScreen
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalOSTheme
import kotlinx.coroutines.launch

enum class ScreenState {
    ONBOARDING,
    HOME,
    APPS,
    HUB,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private lateinit var userPreferences: UserPreferences
    private lateinit var appRepository: AppRepository
    private lateinit var storageRepository: StorageRepository
    private lateinit var executeCommandUseCase: ExecuteCommandUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        userPreferences = UserPreferences(this)
        appRepository = AppRepository(this)
        storageRepository = StorageRepository(this)
        executeCommandUseCase = ExecuteCommandUseCase(this, appRepository, storageRepository)

        MinimalNotificationService.setMockDataIfEmpty()

        setContent {
            MinimalOSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MinimalBlack
                ) {
                    MinimalAppContent(
                        userPreferences = userPreferences,
                        appRepository = appRepository,
                        storageRepository = storageRepository,
                        executeCommandUseCase = executeCommandUseCase
                    )
                }
            }
        }
    }
}

@Composable
fun MinimalAppContent(
    userPreferences: UserPreferences,
    appRepository: AppRepository,
    storageRepository: StorageRepository,
    executeCommandUseCase: ExecuteCommandUseCase
) {
    val scope = rememberCoroutineScope()
    val onboardingCompleted by userPreferences.onboardingCompleted.collectAsState(initial = false)
    val restrictionMode by userPreferences.restrictionMode.collectAsState(initial = RestrictionMode.UNRESTRICTED)

    val installedApps = remember { appRepository.getInstalledApps() }

    var currentScreen by remember(onboardingCompleted) {
        mutableStateOf(if (onboardingCompleted) ScreenState.HOME else ScreenState.ONBOARDING)
    }

    // Mini Apps bottom sheet states
    var showMemoSheet by remember { mutableStateOf(false) }
    var showClockSheet by remember { mutableStateOf(false) }
    var showAskSheet by remember { mutableStateOf(false) }

    // Android back button handling
    BackHandler(enabled = currentScreen != ScreenState.HOME && currentScreen != ScreenState.ONBOARDING) {
        currentScreen = ScreenState.HOME
    }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            ScreenState.ONBOARDING -> {
                OnboardingScreen(
                    onFinish = { selectedMode ->
                        scope.launch {
                            userPreferences.setRestrictionMode(selectedMode)
                            userPreferences.setOnboardingCompleted(true)
                            currentScreen = ScreenState.HOME
                        }
                    }
                )
            }

            ScreenState.HOME -> {
                HomeScreen(
                    restrictionMode = restrictionMode,
                    installedApps = installedApps,
                    executeCommandUseCase = executeCommandUseCase,
                    onNavigateToApps = { currentScreen = ScreenState.APPS },
                    onNavigateToHub = { currentScreen = ScreenState.HUB },
                    onNavigateToSettings = { currentScreen = ScreenState.SETTINGS },
                    onOpenMemo = { showMemoSheet = true },
                    onOpenClock = { showClockSheet = true },
                    onOpenAsk = { showAskSheet = true }
                )
            }

            ScreenState.APPS -> {
                AppDrawerScreen(
                    apps = installedApps,
                    restrictionMode = restrictionMode,
                    onBack = { currentScreen = ScreenState.HOME }
                )
            }

            ScreenState.HUB -> {
                HubScreen(
                    onBackToHome = { currentScreen = ScreenState.HOME }
                )
            }

            ScreenState.SETTINGS -> {
                SettingsScreen(
                    currentMode = restrictionMode,
                    onSelectMode = { mode ->
                        scope.launch { userPreferences.setRestrictionMode(mode) }
                    },
                    onResetOnboarding = {
                        scope.launch {
                            userPreferences.setOnboardingCompleted(false)
                            currentScreen = ScreenState.ONBOARDING
                        }
                    },
                    onBack = { currentScreen = ScreenState.HOME }
                )
            }
        }
    }

    // Modal Mini Apps Bottom Sheets
    if (showMemoSheet) {
        MinimalMemoSheet(
            storageRepository = storageRepository,
            onDismiss = { showMemoSheet = false }
        )
    }

    if (showClockSheet) {
        MinimalClockSheet(
            onDismiss = { showClockSheet = false }
        )
    }

    if (showAskSheet) {
        MinimalAskSheet(
            onDismiss = { showAskSheet = false }
        )
    }
}
