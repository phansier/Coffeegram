package ru.beryukhov.coffeegram

import android.annotation.SuppressLint
import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
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
        startKoin {
            //workaround for kotlin 1.6.0 see https://github.com/InsertKoinIO/koin/issues/1188
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@Application)
            modules(appModule)
        }
    }

    private val appModule = module {
        single<Storage<ThemeState>> {
            //ThemeSharedPrefStorage(context = context)
            //ThemeDataStorePrefStorage(context = context)
            ThemeDataStoreProtoStorage(context = get())
        }
        single<ThemeStore> {
            ThemeStore(get())
        }
    }
}