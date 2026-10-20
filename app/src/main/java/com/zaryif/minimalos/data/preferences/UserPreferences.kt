package com.zaryif.minimalos.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zaryif.minimalos.domain.model.RestrictionMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "minimal_prefs")

enum class AiProvider(val displayName: String, val webUrl: String, val packageName: String?) {
    GEMINI("Google Gemini", "https://gemini.google.com/app", "com.google.android.apps.bard"),
    CHATGPT("ChatGPT", "https://chatgpt.com", "com.openai.chatgpt"),
    CLAUDE("Claude AI", "https://claude.ai/new", "com.anthropic.claude"),
    GOOGLE("Google Web Search", "https://www.google.com/search?q=", null)
}

class UserPreferences(private val context: Context) {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val RESTRICTION_MODE = stringPreferencesKey("restriction_mode")
        private val HIDDEN_APPS = stringSetPreferencesKey("hidden_apps")
        private val DISTRACTING_APPS = stringSetPreferencesKey("distracting_apps")
        private val AI_PROVIDER = stringPreferencesKey("ai_provider")
        private val CUSTOM_WALLPAPER_URI = stringPreferencesKey("custom_wallpaper_uri")
        private val FAVORITE_APPS = stringSetPreferencesKey("favorite_apps")
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[ONBOARDING_COMPLETED] ?: false }

    val restrictionMode: Flow<RestrictionMode> = context.dataStore.data
        .map { prefs ->
            val name = prefs[RESTRICTION_MODE] ?: RestrictionMode.UNRESTRICTED.name
            try {
                RestrictionMode.valueOf(name)
            } catch (_: Exception) {
                RestrictionMode.UNRESTRICTED
            }
        }

    val hiddenApps: Flow<Set<String>> = context.dataStore.data
        .map { prefs -> prefs[HIDDEN_APPS] ?: emptySet() }

    val distractingApps: Flow<Set<String>> = context.dataStore.data
        .map { prefs -> prefs[DISTRACTING_APPS] ?: emptySet() }

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
        .map { prefs ->
            prefs[FAVORITE_APPS] ?: setOf(
                "com.google.android.dialer",
                "com.google.android.apps.messaging",
                "com.android.chrome",
                "com.google.android.GoogleCamera"
            )
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setRestrictionMode(mode: RestrictionMode) {
        context.dataStore.edit { prefs ->
            prefs[RESTRICTION_MODE] = mode.name
        }
    }

    suspend fun setHiddenApps(apps: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[HIDDEN_APPS] = apps
        }
    }

    suspend fun setDistractingApps(apps: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[DISTRACTING_APPS] = apps
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
}
