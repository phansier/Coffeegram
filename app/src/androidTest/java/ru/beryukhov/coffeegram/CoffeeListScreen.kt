package ru.beryukhov.coffeegram

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import io.github.kakaocup.compose.node.element.lazylist.KLazyListItemNode
import io.github.kakaocup.compose.node.element.lazylist.KLazyListNode
import ru.beryukhov.coffeegram.pages.LazyListItemPosition
import ru.beryukhov.coffeegram.pages.LazyListLengthSemantics

class CoffeeListScreen(semanticsProvider: SemanticsNodeInteractionsProvider? = null) :
    ComposeScreen<CoffeeListScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = {
            hasTestTag("CoffeeListScreen")
        }
    ) {
    val coffeeList = KLazyListNode(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = {
            hasTestTag("CoffeeList")
        },
        itemTypeBuilder = {
            itemType(::CoffeeItemNode)
        },
        positionMatcher = { position -> SemanticsMatcher.expectValue(LazyListItemPosition, position) },
        lengthSemanticsPropertyKey = LazyListLengthSemantics,
    )
}

class CoffeeItemNode(
    semanticsNode: SemanticsNode,
    semanticsProvider: SemanticsNodeInteractionsProvider,
) : KLazyListItemNode<CoffeeItemNode>(semanticsNode, semanticsProvider) {
    val title: KNode = child {
            hasTestTag("CoffeeName")
        }
}
