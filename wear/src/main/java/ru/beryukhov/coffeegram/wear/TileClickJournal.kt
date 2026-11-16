package ru.beryukhov.coffeegram.wear

import android.content.Context

class TileClickJournal(context: Context) {
    private val preferences = context.getSharedPreferences("tile_clicks", Context.MODE_PRIVATE)

    fun isNew(clickId: String): Boolean = clickId.isNotEmpty() && clickId != preferences.getString(LAST_HANDLED, null)

    fun markHandled(clickId: String) {
        preferences.edit().putString(LAST_HANDLED, clickId).apply()
    }

    private companion object {
        const val LAST_HANDLED = "last_handled_click"
    }
}
