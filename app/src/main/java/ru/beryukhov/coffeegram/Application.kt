package ru.beryukhov.coffeegram

import android.app.Application
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.pages.AppWidgetViewModelImpl
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.repository.ThemeDataStoreProtoStorage
import ru.beryukhov.coffeegram.store_lib.Storage
import ru.beryukhov.coffeegram.widget.DefaultWidgetDataBridge
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget
import ru.beryukhov.coffeegram.widget.WidgetDataBridge
import ru.beryukhov.coffeegram.widget.setWidgetPreview
import ru.beryukhov.repository.databaseModule

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(
                appModule,
                databaseModule
            )
        }
        // causes java.lang.IllegalStateException: Reading a state that was created after the snapshot was taken
        // or in a snapshot that has not yet been applied
        MainScope().launch {
            withContext(Dispatchers.Default) {
                FirstGlanceWidget().updateAll(this@Application)
                setWidgetPreview(this@Application)
                get<DaysCoffeesStore>().state.onEach {
                    FirstGlanceWidget().updateAll(this@Application)
                }.launchIn(this)
            }
        }
    }
}

internal val appModule = module {
    // Theme storage and store
    single<Storage<ThemeState>> {
        ThemeDataStoreProtoStorage(context = get())
    }
    single {
        ThemeStore(get())
    }

    // Coffee storage and store
    single<CoffeeStorage> { CoffeeStorage(get()) }
    single<DaysCoffeesStore> { DaysCoffeesStoreImpl(get()) }

    // Widget data bridge
    single<WidgetDataBridge> { DefaultWidgetDataBridge(daysCoffeesStore = get()) }

    // Widget ViewModel (still used by Glance widget)
    viewModel { AppWidgetViewModelImpl(daysCoffeesStore = get()) }
}
