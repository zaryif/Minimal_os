package com.zaryif.minimalos.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zaryif.minimalos.domain.model.RestrictionMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "minimal_prefs")

enum class AiProvider(val displayName: String, val webUrl: String, val packageName: String?) {
    GEMINI("Google Gemini", "https://gemini.google.com/app", "com.google.android.apps.bard"),
    ASSISTANT("Google Assistant", "https://www.google.com", "com.google.android.googlequicksearchbox"),
    CHATGPT("ChatGPT", "https://chatgpt.com", "com.openai.chatgpt"),
    CLAUDE("Claude AI", "https://claude.ai/new", "com.anthropic.claude"),
    GOOGLE("Google Search", "https://www.google.com/search?q=", null)
}

class UserPreferences(private val context: Context) {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val AI_PROVIDER = stringPreferencesKey("ai_provider")
        private val CUSTOM_WALLPAPER_URI = stringPreferencesKey("custom_wallpaper_uri")
        private val FAVORITE_APPS = stringSetPreferencesKey("favorite_apps")
        private val DECK_SYSTEM_WIDGETS = stringSetPreferencesKey("system_widgets")
        private val HOME_SYSTEM_WIDGETS = stringSetPreferencesKey("home_system_widgets")
        private val HOME_WIDGETS_LIST = stringPreferencesKey("home_widgets_list")
        private val DECK_WIDGETS_ORDER = stringPreferencesKey("deck_widgets_order")
        private val CUSTOM_COMMANDS_JSON = stringPreferencesKey("custom_commands_json")
        private val THEME_ID = stringPreferencesKey("theme_id")
        private val HOME_SHOW_ALL_TODOS = booleanPreferencesKey("home_show_all_todos")
        private val HOME_SHOW_ALL_NOTES = booleanPreferencesKey("home_show_all_notes")
        private val SHOW_COMMAND_FEEDBACK = booleanPreferencesKey("show_command_feedback")
        private val POMODORO_WORK_MINUTES = intPreferencesKey("pomodoro_work_minutes")
        private val POMODORO_BREAK_MINUTES = intPreferencesKey("pomodoro_break_minutes")
        private val ASSISTANT_PACKAGE = stringPreferencesKey("assistant_package")
        private val RESTRICTION_MODE = stringPreferencesKey("restriction_mode")

        val DEFAULT_HOME_WIDGETS = listOf("TODOS", "CALENDAR", "SHORTCUTS")
        val DEFAULT_DECK_WIDGETS = listOf("NOTES", "TODOS", "CALENDAR", "POMODORO", "TIMERS", "SYSTEM_WIDGETS")
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[ONBOARDING_COMPLETED] ?: false }

    val aiProvider: Flow<AiProvider> = context.dataStore.data
        .map { prefs ->
            val name = prefs[AI_PROVIDER] ?: AiProvider.GEMINI.name
            try {
                AiProvider.valueOf(name)
            } catch (_: Exception) {
                AiProvider.GEMINI
            }
        }

    val customWallpaperUri: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[CUSTOM_WALLPAPER_URI] }

    val favoriteApps: Flow<Set<String>> = context.dataStore.data
        .map { prefs -> prefs[FAVORITE_APPS] ?: emptySet() }

    // System widgets in the Widgets Deck
    val deckSystemWidgets: Flow<Set<Int>> = context.dataStore.data
        .map { prefs ->
            val set = prefs[DECK_SYSTEM_WIDGETS] ?: emptySet()
            set.mapNotNull { it.toIntOrNull() }.toSet()
        }

    // System widgets placed on the Home Screen
    val homeSystemWidgets: Flow<Set<Int>> = context.dataStore.data
        .map { prefs ->
            val set = prefs[HOME_SYSTEM_WIDGETS] ?: emptySet()
            set.mapNotNull { it.toIntOrNull() }.toSet()
        }

    // Configured active widgets on Home Screen
    val homeWidgets: Flow<List<String>> = context.dataStore.data
        .map { prefs ->
            val str = prefs[HOME_WIDGETS_LIST]
            if (str.isNullOrBlank()) {
                DEFAULT_HOME_WIDGETS
            } else {
                str.split(",").filter { it.isNotBlank() }
            }
        }

    // Configured order of cards on the Widgets Deck
    val deckWidgetsOrder: Flow<List<String>> = context.dataStore.data
        .map { prefs ->
            val str = prefs[DECK_WIDGETS_ORDER]
            if (str.isNullOrBlank()) {
                DEFAULT_DECK_WIDGETS
            } else {
                str.split(",").filter { it.isNotBlank() }
            }
        }

    // User-defined custom commands/aliases (e.g. "yt" -> "https://youtube.com")
    val customCommands: Flow<Map<String, String>> = context.dataStore.data
        .map { prefs ->
            val jsonStr = prefs[CUSTOM_COMMANDS_JSON] ?: "{}"
            val map = mutableMapOf<String, String>()
            try {
                val obj = JSONObject(jsonStr)
                obj.keys().forEach { k ->
                    map[k] = obj.getString(k)
                }
            } catch (_: Exception) {}
            map
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setAiProvider(provider: AiProvider) {
        context.dataStore.edit { prefs ->
            prefs[AI_PROVIDER] = provider.name
        }
    }

    suspend fun setCustomWallpaperUri(uri: String?) {
        context.dataStore.edit { prefs ->
            if (uri != null) {
                prefs[CUSTOM_WALLPAPER_URI] = uri
            } else {
                prefs.remove(CUSTOM_WALLPAPER_URI)
            }
        }
    }

    suspend fun setFavoriteApps(apps: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[FAVORITE_APPS] = apps
        }
    }

    suspend fun addDeckSystemWidget(widgetId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[DECK_SYSTEM_WIDGETS] ?: emptySet()
            prefs[DECK_SYSTEM_WIDGETS] = current + widgetId.toString()
        }
    }

    suspend fun removeDeckSystemWidget(widgetId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[DECK_SYSTEM_WIDGETS] ?: emptySet()
            prefs[DECK_SYSTEM_WIDGETS] = current - widgetId.toString()
        }
    }

    suspend fun addHomeSystemWidget(widgetId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[HOME_SYSTEM_WIDGETS] ?: emptySet()
            prefs[HOME_SYSTEM_WIDGETS] = current + widgetId.toString()
        }
    }

    suspend fun removeHomeSystemWidget(widgetId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[HOME_SYSTEM_WIDGETS] ?: emptySet()
            prefs[HOME_SYSTEM_WIDGETS] = current - widgetId.toString()
        }
    }

    suspend fun setHomeWidgets(widgets: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[HOME_WIDGETS_LIST] = widgets.joinToString(",")
        }
    }

    suspend fun setDeckWidgetsOrder(order: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[DECK_WIDGETS_ORDER] = order.joinToString(",")
        }
    }

    suspend fun setCustomCommand(keyword: String, action: String) {
        context.dataStore.edit { prefs ->
            val jsonStr = prefs[CUSTOM_COMMANDS_JSON] ?: "{}"
            val obj = try { JSONObject(jsonStr) } catch (_: Exception) { JSONObject() }
            obj.put(keyword.lowercase().trim(), action.trim())
            prefs[CUSTOM_COMMANDS_JSON] = obj.toString()
        }
    }

    suspend fun removeCustomCommand(keyword: String) {
        context.dataStore.edit { prefs ->
            val jsonStr = prefs[CUSTOM_COMMANDS_JSON] ?: "{}"
            val obj = try { JSONObject(jsonStr) } catch (_: Exception) { JSONObject() }
            obj.remove(keyword.lowercase().trim())
            prefs[CUSTOM_COMMANDS_JSON] = obj.toString()
        }
    }

    val themeId: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[THEME_ID] ?: "RABBIT_ORANGE" }

    val homeShowAllTodos: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[HOME_SHOW_ALL_TODOS] ?: false }

    val homeShowAllNotes: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[HOME_SHOW_ALL_NOTES] ?: false }

    suspend fun setThemeId(theme: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_ID] = theme
        }
    }

    suspend fun setHomeShowAllTodos(showAll: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[HOME_SHOW_ALL_TODOS] = showAll
        }
    }

    suspend fun setHomeShowAllNotes(showAll: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[HOME_SHOW_ALL_NOTES] = showAll
        }
    }

    val showCommandFeedback: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SHOW_COMMAND_FEEDBACK] ?: false }

    val pomodoroWorkMinutes: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[POMODORO_WORK_MINUTES] ?: 25 }

    val pomodoroBreakMinutes: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[POMODORO_BREAK_MINUTES] ?: 5 }

    suspend fun setShowCommandFeedback(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SHOW_COMMAND_FEEDBACK] = show
        }
    }

    suspend fun setPomodoroWorkMinutes(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[POMODORO_WORK_MINUTES] = minutes
        }
    }

    suspend fun setPomodoroBreakMinutes(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[POMODORO_BREAK_MINUTES] = minutes
        }
    }

    val assistantPackage: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[ASSISTANT_PACKAGE] }

    suspend fun setAssistantPackage(pkg: String?) {
        context.dataStore.edit { prefs ->
            if (pkg != null) {
                prefs[ASSISTANT_PACKAGE] = pkg
            } else {
                prefs.remove(ASSISTANT_PACKAGE)
            }
        }
    }

    val restrictionMode: Flow<RestrictionMode> = context.dataStore.data
        .map { prefs ->
            val name = prefs[RESTRICTION_MODE] ?: RestrictionMode.UNRESTRICTED.name
            try {
                RestrictionMode.valueOf(name)
            } catch (_: Exception) {
                RestrictionMode.UNRESTRICTED
            }
        }

    suspend fun setRestrictionMode(mode: RestrictionMode) {
        context.dataStore.edit { prefs ->
            prefs[RESTRICTION_MODE] = mode.name
        }
    }
}
