package ru.beryukhov.coffeegram

import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import kotlin.test.Test

class CheckModulesTest : KoinTest {
    @Test
    fun verifyKoinApp() {

        koinApplication {
            modules(appModule())
            checkModules()
        }
    }
}
