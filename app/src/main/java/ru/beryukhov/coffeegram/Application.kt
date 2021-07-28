package ru.beryukhov.coffeegram

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.ThemeDataStorePrefStorage
import ru.beryukhov.coffeegram.repository.ThemeDataStoreProtoStorage
import ru.beryukhov.coffeegram.repository.ThemeSharedPrefStorage
import ru.beryukhov.coffeegram.store_lib.Storage


@Suppress("unused")
class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        context = this
    }

    // prototype of DI
    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        private val themeStorage: Storage<ThemeState> by lazy {
            //ThemeSharedPrefStorage(context = context)
            //ThemeDataStorePrefStorage(context = context)
            ThemeDataStoreProtoStorage(context = context)
        }

        val themeStore: ThemeStore by lazy { ThemeStore(themeStorage) }
    }
}