package ru.beryukhov.coffeegram

/**
 * Glance composition rasterises coffee icons to a Bitmap, which Robolectric returns as null,
 * so the preview push stays out of unit tests.
 */
class TestApplication : Application() {
    override suspend fun setWidgetPreview() = Unit
}
