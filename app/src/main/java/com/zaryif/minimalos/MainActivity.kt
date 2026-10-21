package com.zaryif.minimalos

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.zaryif.minimalos.data.preferences.UserPreferences
import com.zaryif.minimalos.data.repository.AppRepository
import com.zaryif.minimalos.data.repository.ContactRepository
import com.zaryif.minimalos.data.repository.StorageRepository
import com.zaryif.minimalos.domain.usecase.ExecuteCommandUseCase
import com.zaryif.minimalos.service.MinimalNotificationService
import com.zaryif.minimalos.ui.apps.AppDrawerScreen
import com.zaryif.minimalos.ui.components.MinimalClockSheet
import com.zaryif.minimalos.ui.components.MinimalMemoSheet
import com.zaryif.minimalos.ui.home.HomeScreen
import com.zaryif.minimalos.ui.hub.HubScreen
import com.zaryif.minimalos.ui.settings.SettingsScreen
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalOSTheme
import com.zaryif.minimalos.ui.widgets.WidgetsDeckScreen
import kotlinx.coroutines.launch

enum class ScreenState {
    HOME,
    APPS,
    HUB,
    WIDGETS,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    companion object {
        private const val APPWIDGET_HOST_ID = 2048
    }

    private lateinit var userPreferences: UserPreferences
    private lateinit var appRepository: AppRepository
    private lateinit var storageRepository: StorageRepository
    private lateinit var contactRepository: ContactRepository
    private lateinit var executeCommandUseCase: ExecuteCommandUseCase

    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var appWidgetHost: AppWidgetHost

    private val pickWidgetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val appWidgetId = result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
            if (appWidgetId != -1) {
                val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
                if (appWidgetInfo?.configure != null) {
                    val configIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                        component = appWidgetInfo.configure
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    }
                    configWidgetLauncher.launch(configIntent)
                } else {
                    lifecycleScope.launch {
                        userPreferences.addSystemWidget(appWidgetId)
                    }
                }
            }
        }
    }

    private val configWidgetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val appWidgetId = result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
            if (appWidgetId != -1) {
                lifecycleScope.launch {
                    userPreferences.addSystemWidget(appWidgetId)
                }
            }
        }
    }

    fun launchWidgetPicker() {
        val newId = appWidgetHost.allocateAppWidgetId()
        val pickIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newId)
        }
        pickWidgetLauncher.launch(pickIntent)
    }

    fun deleteWidget(id: Int) {
        appWidgetHost.deleteAppWidgetId(id)
        lifecycleScope.launch {
            userPreferences.removeSystemWidget(id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APPWIDGET_HOST_ID)

        userPreferences = UserPreferences(this)
        appRepository = AppRepository(this)
        storageRepository = StorageRepository(this)
        contactRepository = ContactRepository(this)
        executeCommandUseCase = ExecuteCommandUseCase(
            this,
            appRepository,
            storageRepository,
            contactRepository,
            userPreferences
        )

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
                        contactRepository = contactRepository,
                        executeCommandUseCase = executeCommandUseCase,
                        appWidgetManager = appWidgetManager,
                        appWidgetHost = appWidgetHost,
                        onAddWidget = { launchWidgetPicker() },
                        onDeleteWidget = { deleteWidget(it) }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        appWidgetHost.startListening()
    }

    override fun onStop() {
        super.onStop()
        appWidgetHost.stopListening()
    }
}

@Composable
fun MinimalAppContent(
    userPreferences: UserPreferences,
    appRepository: AppRepository,
    storageRepository: StorageRepository,
    contactRepository: ContactRepository,
    executeCommandUseCase: ExecuteCommandUseCase,
    appWidgetManager: AppWidgetManager,
    appWidgetHost: AppWidgetHost,
    onAddWidget: () -> Unit,
    onDeleteWidget: (Int) -> Unit
) {
    val installedApps = remember { appRepository.getInstalledApps() }
    val systemWidgetIds by userPreferences.systemWidgets.collectAsState(initial = emptySet())

    var currentScreen by remember { mutableStateOf(ScreenState.HOME) }

    // Mini Apps bottom sheet states
    var showMemoSheet by remember { mutableStateOf(false) }
    var showClockSheet by remember { mutableStateOf(false) }

    // Android back button handling returns to Home screen
    BackHandler(enabled = currentScreen != ScreenState.HOME) {
        currentScreen = ScreenState.HOME
    }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            ScreenState.HOME -> {
                HomeScreen(
                    installedApps = installedApps,
                    storageRepository = storageRepository,
                    contactRepository = contactRepository,
                    userPreferences = userPreferences,
                    executeCommandUseCase = executeCommandUseCase,
                    onNavigateToApps = { currentScreen = ScreenState.APPS },
                    onNavigateToHub = { currentScreen = ScreenState.HUB },
                    onNavigateToWidgets = { currentScreen = ScreenState.WIDGETS },
                    onNavigateToSettings = { currentScreen = ScreenState.SETTINGS },
                    onOpenMemo = { showMemoSheet = true },
                    onOpenClock = { showClockSheet = true }
                )
            }

            ScreenState.APPS -> {
                AppDrawerScreen(
                    apps = installedApps,
                    onBack = { currentScreen = ScreenState.HOME }
                )
            }

            ScreenState.HUB -> {
                HubScreen(
                    onBackToHome = { currentScreen = ScreenState.HOME }
                )
            }

            ScreenState.WIDGETS -> {
                WidgetsDeckScreen(
                    storageRepository = storageRepository,
                    executeCommandUseCase = executeCommandUseCase,
                    systemWidgetIds = systemWidgetIds,
                    appWidgetManager = appWidgetManager,
                    appWidgetHost = appWidgetHost,
                    onAddSystemWidget = onAddWidget,
                    onDeleteSystemWidget = onDeleteWidget,
                    onBackToHome = { currentScreen = ScreenState.HOME }
                )
            }

            ScreenState.SETTINGS -> {
                SettingsScreen(
                    installedApps = installedApps,
                    userPreferences = userPreferences,
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
}
