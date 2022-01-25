package ru.beryukhov.coffeegram.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.GlanceAppWidget


class WidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = FirstGlanceWidget()
}


/* old way
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import ru.beryukhov.coffeegram.R

class WidgetReceiver : AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val intent = Intent(context, SplashActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        appWidgetIds?.forEach { appWidgetId ->
            val views = RemoteViews(
                context!!.packageName,
                R.layout.app_widget
            )
            views.setOnClickPendingIntent(R.id.count, pendingIntent)
            ...
            appWidgetManager?.updateAppWidget(appWidgetId, views);
        }
    }
}
*/
