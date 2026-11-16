package ru.beryukhov.coffeegram.wear

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationDataTimeline
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingTimelineComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.TimeInterval
import androidx.wear.watchface.complications.datasource.TimelineEntry
import kotlinx.coroutines.flow.first
import ru.beryukhov.coffeegram.data.durationUntilTomorrow
import java.time.Instant
import kotlin.time.toJavaDuration

private const val PREVIEW_TOTAL = 3

class TodayComplicationService : SuspendingTimelineComplicationDataSourceService() {

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationDataTimeline? {
        if (request.complicationType != ComplicationType.SHORT_TEXT) return null
        val repository = coffeeRepository
        repository.refreshFromDataLayer()
        val total = repository.todayCoffee.first().total
        val now = Instant.now()
        val tomorrow = now.plus(durationUntilTomorrow().toJavaDuration())
        return ComplicationDataTimeline(
            defaultComplicationData = totalData(0),
            timelineEntries = listOf(TimelineEntry(TimeInterval(now, tomorrow), totalData(total))),
        )
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? =
        if (type == ComplicationType.SHORT_TEXT) totalData(PREVIEW_TOTAL) else null

    private fun totalData(total: Int): ComplicationData = ShortTextComplicationData.Builder(
        text = PlainComplicationText.Builder(total.toString()).build(),
        contentDescription = PlainComplicationText.Builder(getString(R.string.complication_description, total)).build(),
    ).setTitle(PlainComplicationText.Builder(getString(R.string.complication_title)).build())
        .setTapAction(openAppIntent()).build()

    private fun openAppIntent(): PendingIntent = PendingIntent.getActivity(
        this,
        0,
        Intent(this, WearActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )
}
