package applicationId.ru.netology.nmedia.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth")

@Singleton
class AuthLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        val KEY_ID = longPreferencesKey("id")
        val KEY_TOKEN = stringPreferencesKey("token")
    }

    val authState: Flow<AuthState> = context.dataStore.data.map { prefs ->
        AuthState(
            id = prefs[KEY_ID] ?: 0L,
            token = prefs[KEY_TOKEN]
        )
    }

    suspend fun getTokenOrNull(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_TOKEN]
    }

    suspend fun setAuth(id: Long, token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ID] = id
            prefs[KEY_TOKEN] = token
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ID)
            prefs.remove(KEY_TOKEN)
        }
    }
}