package com.econect.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.econect.app.domain.model.UserType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Nota: se usa Preferences DataStore en lugar de Proto DataStore para evitar conflicto
// entre protobuf-kotlin-lite y Firebase protolite-well-known-types.
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_session"
)

@Singleton
class UserDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.userDataStore

    val uidFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_UID]
    }

    val userTypeFlow: Flow<UserType?> = dataStore.data.map { prefs ->
        prefs[KEY_USER_TYPE]?.let { runCatching { UserType.valueOf(it) }.getOrNull() }
    }

    suspend fun saveActiveUser(uid: String, userType: UserType) {
        dataStore.edit { prefs ->
            prefs[KEY_UID] = uid
            prefs[KEY_USER_TYPE] = userType.name
        }
    }

    suspend fun clearActiveUser() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_UID)
            prefs.remove(KEY_USER_TYPE)
        }
    }

    companion object {
        private val KEY_UID = stringPreferencesKey("uid")
        private val KEY_USER_TYPE = stringPreferencesKey("user_type")
    }
}
