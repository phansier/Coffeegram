package ru.beryukhov.coffeegram

import android.app.Application
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.repository.ThemeDataStorePrefStorage
import ru.beryukhov.coffeegram.repository.ThemeDataStoreProtoStorage
import ru.beryukhov.coffeegram.repository.ThemeSharedPrefStorage
import ru.beryukhov.coffeegram.store_lib.Storage
import ru.beryukhov.coffeegram.widget.DefaultWidgetDataBridge
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget
import ru.beryukhov.coffeegram.widget.WidgetDataBridge
import ru.beryukhov.coffeegram.widget.setWidgetPreview
import ru.beryukhov.repository.databaseModule

open class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(
                dataStoreModule,
                coffeeStorageModule,
                androidAppModule,
                databaseModule
            )
        }
        // causes java.lang.IllegalStateException: Reading a state that was created after the snapshot was taken
        // or in a snapshot that has not yet been applied
        MainScope().launch {
            withContext(Dispatchers.Default) {
                FirstGlanceWidget().updateAll(this@Application)
                // if (not robolectric test)
                setWidgetPreview()
            }
        }
    }

    open suspend fun setWidgetPreview() {
        setWidgetPreview(this@Application)
    }
}

internal val androidAppModule = module {
    // Theme storage and store
    // Widget data bridge
    single<WidgetDataBridge> { DefaultWidgetDataBridge(daysCoffeesStore = get()) }
    single<Storage<ThemeState>> {
        ThemeDataStoreProtoStorage(context = get())
        ThemeSharedPrefStorage(context = get())
        // the last used, other to demo their existence
        ThemeDataStorePrefStorage(dataStore = get())
    }
}
