package ru.beryukhov.coffeegram

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
class Application : Application() {
    private val androidModule = module {}

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            androidLogger()
            modules(appModule() + androidModule)
        }
        initDbContext(this)
    }
}
