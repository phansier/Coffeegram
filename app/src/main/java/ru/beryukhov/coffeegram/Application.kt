package ru.beryukhov.coffeegram

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen



@Suppress("unused")
class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}