package ru.beryukhov.coffeegram.widget

import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.os.Build
import androidx.collection.intSetOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.setWidgetPreviews

class WidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = FirstGlanceWidget()
}

suspend fun setWidgetPreview(appContext: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        GlanceAppWidgetManager(appContext).setWidgetPreviews<WidgetReceiver>(
            intSetOf(AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN)
        )
    }
}
