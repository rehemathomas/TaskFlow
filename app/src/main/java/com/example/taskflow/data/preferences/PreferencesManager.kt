package com.example.taskflow.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore-based preferences manager for app settings
 * Provides type-safe access to user preferences with reactive Flow
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "task_flow_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val SORT_OPTION_KEY = stringPreferencesKey("sort_option")
        private val FILTER_PRIORITY_KEY = stringPreferencesKey("filter_priority")
        private val FILTER_CATEGORY_KEY = stringPreferencesKey("filter_category")
    }

    /**
     * Get dark mode preference as Flow
     * Default is false (light mode)
     */
    val darkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    /**
     * Get language preference as Flow
     * Default is "en" (English)
     */
    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en"
    }

    /**
     * Get notifications enabled preference as Flow
     * Default is true
     */
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
    }

    /**
     * Get current sort option as Flow
     * Default is "DATE_DESC"
     */
    val sortOption: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SORT_OPTION_KEY] ?: "DATE_DESC"
    }

    /**
     * Get filter priority as Flow
     * Default is "ALL"
     */
    val filterPriority: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[FILTER_PRIORITY_KEY] ?: "ALL"
    }

    /**
     * Get filter category as Flow
     * Default is "ALL"
     */
    val filterCategory: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[FILTER_CATEGORY_KEY] ?: "ALL"
    }

    /**
     * Toggle dark mode setting
     * @param enabled Whether dark mode is enabled
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    /**
     * Set language preference
     * @param languageCode Language code (e.g., "en", "sw")
     */
    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }

    /**
     * Set notifications enabled preference
     * @param enabled Whether notifications are enabled
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }

    /**
     * Save sort option
     * @param option Sort option string
     */
    suspend fun setSortOption(option: String) {
        context.dataStore.edit { preferences ->
            preferences[SORT_OPTION_KEY] = option
        }
    }

    /**
     * Save filter priority
     * @param priority Priority filter string
     */
    suspend fun setFilterPriority(priority: String) {
        context.dataStore.edit { preferences ->
            preferences[FILTER_PRIORITY_KEY] = priority
        }
    }

    /**
     * Save filter category
     * @param category Category filter string
     */
    suspend fun setFilterCategory(category: String) {
        context.dataStore.edit { preferences ->
            preferences[FILTER_CATEGORY_KEY] = category
        }
    }

    /**
     * Clear all preferences (reset to defaults)
     */
    suspend fun clearAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
