package com.example.bukuku.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Inisialisasi Preferences DataStore sebagai ekstensi Context (otomatis bersifat singleton)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_pref")

/**
 * SettingsRepository bertanggung jawab mengelola preferensi aplikasi menggunakan DataStore.
 * Menggantikan SharedPreferences dengan API berbasis Coroutine dan Flow yang aman dan reaktif.
 */
class SettingsRepository(private val context: Context) {

    companion object {
        // Kunci preferensi bertipe Boolean untuk menyimpan pengaturan "mode_ringkas"
        val MODE_RINGKAS_KEY = booleanPreferencesKey("mode_ringkas")
    }

    // Membaca status mode ringkas secara reaktif sebagai Flow<Boolean>
    // Jika belum pernah diatur, nilai default-nya adalah false (mode tampilan normal)
    val modeRingkasFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[MODE_RINGKAS_KEY] ?: false
    }

    // Fungsi suspend untuk menyimpan perubahan preferensi mode ringkas (ON/OFF)
    suspend fun setModeRingkas(isRingkas: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MODE_RINGKAS_KEY] = isRingkas
        }
    }
}
