package ru.beryukhov.coffeegram.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.store_lib.Storage

internal const val FILENAME = "theme_shared_pref"
internal const val THEME_STATE = "theme_state"

class ThemeSharedPrefStorage(private val context: Context): Storage<ThemeState> {

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
    }



    override suspend fun getState(): ThemeState? {
        return sharedPrefs.getString(THEME_STATE, null)
            ?.let { ThemeState.valueOf(it) }
    }

    override suspend fun saveState(state: ThemeState) {
        sharedPrefs.edit(commit = false) {
            putString(THEME_STATE, state.name)
        }
    }

}