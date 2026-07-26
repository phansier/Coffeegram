package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.beryukhov.coffeegram.repository.CoffeeShop
import ru.beryukhov.coffeegram.repository.coffeeShops

/**
 * State for the map feature.
 */
data class CoffeeShopsState(
    val list: List<ExtendedCoffeeShop> = emptyList(),
    val expanded: Boolean = false,
    val isLoading: Boolean = true,
)

data class ExtendedCoffeeShop(
    val coffeeShop: CoffeeShop,
    val highlighted: Boolean = false,
)

/**
 * Component for the map feature showing coffee shop locations.
 */
interface MapComponent {
    val coffeeShops: StateFlow<CoffeeShopsState>
    val hasUserLocation: StateFlow<Boolean>

    fun onZoomChanged(zoom: Float)
    fun onMarkerClicked(coffeeShop: CoffeeShop)
    fun onUserLocationObtained()
}

class DefaultMapComponent(
    context: ComponentContext,
) : MapComponent, ComponentContext by context {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _coffeeShops = MutableStateFlow(CoffeeShopsState())
    override val coffeeShops: StateFlow<CoffeeShopsState> = _coffeeShops.asStateFlow()

    private val _hasUserLocation = MutableStateFlow(false)
    override val hasUserLocation: StateFlow<Boolean> = _hasUserLocation.asStateFlow()

    init {
        loadCoffeeShops()
    }

    private fun loadCoffeeShops() {
        scope.launch {
            try {
                val shops = coffeeShops()
                _coffeeShops.value = CoffeeShopsState(
                    list = shops.map { ExtendedCoffeeShop(it, false) },
                    expanded = false,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _coffeeShops.value = CoffeeShopsState(
                    list = emptyList(),
                    expanded = false,
                    isLoading = false,
                )
            }
        }
    }

    override fun onZoomChanged(zoom: Float) {
        val newExpanded = zoom >= 11
        val current = _coffeeShops.value
        if (current.expanded != newExpanded) {
            _coffeeShops.value = current.copy(expanded = newExpanded)
        }
    }

    override fun onMarkerClicked(coffeeShop: CoffeeShop) {
        val current = _coffeeShops.value
        _coffeeShops.value = current.copy(
            list = current.list.map {
                if (it.coffeeShop == coffeeShop) {
                    it.copy(highlighted = true)
                } else {
                    it.copy(highlighted = false)
                }
            }
        )
    }

    override fun onUserLocationObtained() {
        _hasUserLocation.value = true
    }
}
