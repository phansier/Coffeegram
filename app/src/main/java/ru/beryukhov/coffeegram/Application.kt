package ru.beryukhov.coffeegram

import android.app.Application
import androidx.glance.appwidget.updateAll
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.LightDaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.pages.CoffeeListViewModelImpl
import ru.beryukhov.coffeegram.pages.TablePageViewModelImpl
import ru.beryukhov.coffeegram.repository.ThemeDataStorePrefStorage
import ru.beryukhov.coffeegram.repository.ThemeDataStoreProtoStorage
import ru.beryukhov.coffeegram.repository.ThemeSharedPrefStorage
import ru.beryukhov.coffeegram.store_lib.Storage
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget


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
        //causes java.lang.IllegalStateException: Reading a state that was created after the snapshot was taken or in a snapshot that has not yet been applied
        GlobalScope.launch {
            FirstGlanceWidget().updateAll(this@Application)
        }
    }

    private val appModule = module {
        single<Storage<ThemeState>> {
            //ThemeSharedPrefStorage(context = context)
            //ThemeDataStorePrefStorage(context = context)
            ThemeDataStoreProtoStorage(context = get())
        }
        single {
            ThemeStore(get())
        }
        single<DaysCoffeesStore> { LightDaysCoffeesStore() }
        single { NavigationStore()}
        viewModel { CoffeeListViewModelImpl(daysCoffeesStore = get(), navigationStore = get()) }
        viewModel { TablePageViewModelImpl(daysCoffeesStore = get(), navigationStore = get()) }
    }
}