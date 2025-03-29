package ru.beryukhov.coffeegram

import android.app.Application
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.HeavyDaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.pages.AppWidgetViewModelImpl
import ru.beryukhov.coffeegram.pages.CoffeeListViewModelImpl
import ru.beryukhov.coffeegram.pages.MapPageViewModelImpl
import ru.beryukhov.coffeegram.pages.StatsPageViewModelImpl
import ru.beryukhov.coffeegram.pages.TablePageViewModelImpl
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.repository.ThemeDataStoreProtoStorage
import ru.beryukhov.coffeegram.store_lib.Storage
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget
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
        GlobalScope.launch {
            FirstGlanceWidget().updateAll(this@Application)
            setWidgetPreview(this@Application)
            get<DaysCoffeesStore>().state.onEach {
                FirstGlanceWidget().updateAll(this@Application) }.launchIn(this)
        }
    }
}

internal val appModule = module {
    single<Storage<ThemeState>> {
        // ThemeSharedPrefStorage(context = context)
        // ThemeDataStorePrefStorage(context = context)
        ThemeDataStoreProtoStorage(context = get())
    }
    single {
        ThemeStore(get())
    }
    single<CoffeeStorage> { CoffeeStorage(get()) }
    single<DaysCoffeesStore> { HeavyDaysCoffeesStore(get()) }
//        single<DaysCoffeesStore> { LightDaysCoffeesStore() }
    single { NavigationStore() }
    viewModel { CoffeeListViewModelImpl(daysCoffeesStore = get(), navigationStore = get()) }
    viewModel { TablePageViewModelImpl(daysCoffeesStore = get(), navigationStore = get()) }
    viewModel { StatsPageViewModelImpl(daysCoffeesStore = get()) }
    viewModel { MapPageViewModelImpl() }
    viewModel { AppWidgetViewModelImpl(daysCoffeesStore = get()) }
}
