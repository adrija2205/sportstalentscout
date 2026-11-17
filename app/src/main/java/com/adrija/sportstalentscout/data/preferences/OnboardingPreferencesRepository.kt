package com.adrija.sportstalentscout.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.IOException

// Singleton DataStore instance tied to application context
@Volatile
private var dataStoreInstance: DataStore<Preferences>? = null

private fun getDataStore(context: Context): DataStore<Preferences> {
    return dataStoreInstance ?: synchronized(OnboardingPreferencesRepository::class.java) {
        dataStoreInstance ?: PreferenceDataStoreFactory.create {
            File(context.applicationContext.filesDir, "datastore/krida_user_preferences.preferences_pb")
        }.also { dataStoreInstance = it }
    }
}

/**
 * Repository responsible for managing persistent user preferences using Jetpack DataStore Preferences.
 *
 * Stores the onboarding completion status under the key "onboarding_completed".
 */
class OnboardingPreferencesRepository(context: Context) {

    private val dataStore = getDataStore(context)

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    /**
     * Flow observing whether the user has previously completed onboarding.
     * Emits `false` by default if no preference is currently recorded.
     * Handles IOExceptions gracefully by emitting empty preferences.
     */
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    /**
     * Persists the onboarding completion status to DataStore.
     *
     * @param completed `true` when the user has finished or skipped the onboarding flow.
     */
    suspend fun setOnboardingCompleted(completed: Boolean = true) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }
}
