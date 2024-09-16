import androidx.compose.runtime.Composable
import repository.InMemoryCoffeeRepository
import ru.beryukhov.coffeegram.DefaultPreview
import ru.beryukhov.coffeegram.Dependencies
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.store_lib.Storage

// koin causes RuntimeError: dereferencing a null pointer
// private val koinApp = initKoin().koin
//
// private val appModule = module {
//
//    single<Storage<ThemeState>> {
//        LocalThemePrefStorage()
//    }
//    single {
//        ThemeStore(get())
//    }
//    single<DaysCoffeesStore> { DaysCoffeesStoreImpl(coffeeStorage = get()) }
//    single { CoffeeStorage(repository = InMemoryCoffeeRepository()) }
//
//    single { NavigationStore() }
//    single { Dependencies(get(), get(), get()) }
// }
//
// private fun initKoin() =
//    startKoin {
//        modules(appModule)
//    }

private val deps = Dependencies(
    navigationStore = NavigationStore(),
    daysCoffeesStore = DaysCoffeesStoreImpl(CoffeeStorage(InMemoryCoffeeRepository())),
    themeStore = ThemeStore(LocalThemePrefStorage()),
)

@Composable
fun App() {
//    DefaultPreview(koinApp.get())
    DefaultPreview(deps)
}

class LocalThemePrefStorage : Storage<ThemeState> {
    private var themeState: ThemeState? = null

    override suspend fun getState(): ThemeState? = themeState
    override suspend fun saveState(state: ThemeState) {
        themeState = state
    }
}
