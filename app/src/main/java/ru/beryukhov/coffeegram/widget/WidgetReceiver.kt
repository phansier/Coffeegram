package ru.beryukhov.coffeegram.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.compose

class WidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = FirstGlanceWidget()
}

suspend fun setWidgetPreview(appContext: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        AppWidgetManager.getInstance(appContext).setWidgetPreview(
            ComponentName(
                appContext,
                WidgetReceiver::class.java
            ),
            AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
            FirstGlanceWidget().compose(
                context = appContext
            ),
        )
    }
}
