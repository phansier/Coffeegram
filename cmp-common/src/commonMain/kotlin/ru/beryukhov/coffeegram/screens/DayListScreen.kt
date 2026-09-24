package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.add_drink
import coffeegram.cmp_common.generated.resources.pick_day_placeholder
import com.slapps.cupertino.adaptive.AdaptiveIconButton
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.LocalPhoneFrameInsets
import ru.beryukhov.coffeegram.components.DayListComponent
import ru.beryukhov.coffeegram.components.getShortMonthName
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.view.CoffeeTypeItem

@Composable
fun DayListScreen(
    component: DayListComponent,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val screenState by component.models.collectAsState()
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .consumeWindowInsets(contentPadding),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(
            items = screenState.dayItems,
            itemContent = { _, (coffee, count): CoffeeTypeWithCount ->
                CoffeeTypeItem(
                    coffeeType = coffee,
                    count = count,
                    onIncrement = { component.onPlusCoffee(coffee) },
                    onDecrement = { component.onMinusCoffee(coffee) },
                )
            }
        )
    }
}

@Composable
fun DayListPlaceholder(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CoffeeTypes.Cappuccino.icon(modifier = Modifier.size(width = 80.dp, height = 48.dp))
        Text(
            text = stringResource(Res.string.pick_day_placeholder),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun DayListAppBar(
    component: DayListComponent,
    modifier: Modifier = Modifier
) {
    val screenState by component.models.collectAsState()
    val localDate = screenState.date
    AdaptiveTopAppBar(
        title = {
            TopBarTitle(
                title = "${localDate.day} ${getShortMonthName(localDate.month)} "
                    + stringResource(Res.string.add_drink)
            )
        },
        navigationIcon = {
            AdaptiveIconButton(onClick = component::onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = ""
                )
            }
        },
        adaptation = { material { isCenterAligned = true } },
        windowInsets = TopAppBarDefaults.windowInsets.union(LocalPhoneFrameInsets.current),
    )
}
