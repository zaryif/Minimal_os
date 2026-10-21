package com.zaryif.minimalos.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
        private val SYSTEM_WIDGETS = stringSetPreferencesKey("system_widgets")
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

    val systemWidgets: Flow<Set<Int>> = context.dataStore.data
        .map { prefs ->
            val set = prefs[SYSTEM_WIDGETS] ?: emptySet()
            set.mapNotNull { it.toIntOrNull() }.toSet()
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

    suspend fun addSystemWidget(widgetId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[SYSTEM_WIDGETS] ?: emptySet()
            prefs[SYSTEM_WIDGETS] = current + widgetId.toString()
        }
    }

    suspend fun removeSystemWidget(widgetId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[SYSTEM_WIDGETS] ?: emptySet()
            prefs[SYSTEM_WIDGETS] = current - widgetId.toString()
        }
    }
}

