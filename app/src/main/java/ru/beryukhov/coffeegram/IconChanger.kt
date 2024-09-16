package ru.beryukhov.coffeegram

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

private const val MAIN_ACTIVITY = "ru.beryukhov.coffeegram.MainActivity"
private const val SUMMER_ACTIVITY = "ru.beryukhov.coffeegram.SummerActivity"

fun changeIcon(context: Context, isSummer: Boolean) {
    val aliasToEnable = if (isSummer) {
        SUMMER_ACTIVITY
    } else {
        MAIN_ACTIVITY
    }
    val aliasToDisable = when (aliasToEnable) {
        MAIN_ACTIVITY -> SUMMER_ACTIVITY
        else -> MAIN_ACTIVITY
    }

    val packageManager = context.packageManager
    enableComponent(context, packageManager, aliasToEnable)
    disableComponent(context, packageManager, aliasToDisable)
}

private fun enableComponent(
    context: Context,
    packageManager: PackageManager,
    componentNameString: String
) {
    val componentName = ComponentName(context, componentNameString)
    packageManager.setComponentEnabledSetting(
        componentName,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )
}

private fun disableComponent(
    context: Context,
    packageManager: PackageManager,
    componentNameString: String
) {
    val componentName = ComponentName(context, componentNameString)
    packageManager.setComponentEnabledSetting(
        componentName,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}
