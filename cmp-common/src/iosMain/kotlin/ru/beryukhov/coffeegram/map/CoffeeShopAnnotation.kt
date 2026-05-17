package ru.beryukhov.coffeegram.map

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.darwin.NSObject
import ru.beryukhov.coffeegram.repository.CoffeeShop

/**
 * MKAnnotation wrapping a [CoffeeShop] and a Compose-rasterised marker image.
 *
 * The annotation creates the [MKAnnotationView] on demand and lets us re-bind the cached UIImage
 * whenever the rendered marker changes (e.g. on highlight or expand state changes).
 */
@OptIn(ExperimentalForeignApi::class)
internal class CoffeeShopAnnotation(
    val coffeeShop: CoffeeShop,
    val identifier: String,
    private val location: CValue<CLLocationCoordinate2D>,
    private val viewCreator: (MKAnnotationProtocol) -> MKAnnotationView,
    private val viewBinder: (MKAnnotationView) -> Unit,
) : NSObject(), MKAnnotationProtocol {

    override fun coordinate(): CValue<CLLocationCoordinate2D> = location

    fun createView(): MKAnnotationView = viewCreator(this)

    fun bindView(view: MKAnnotationView) = viewBinder(view)
}
