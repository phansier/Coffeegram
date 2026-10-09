package ru.beryukhov.coffeegram.view

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.tooling.preview.PreviewLightDark
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.copy_link
import coffeegram.cmp_common.generated.resources.share
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.PreviewTheme
import ru.beryukhov.coffeegram.repository.CoffeeShop
import ru.beryukhov.coffeegram.share.plainTextClipEntry
import ru.beryukhov.coffeegram.share.rememberNativeShare
import ru.beryukhov.coffeegram.share.shareLink
import ru.beryukhov.coffeegram.share.shareText

@Composable
internal fun CoffeeShopShareMenu(
    shop: CoffeeShop,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onLinkCopied: () -> Unit,
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val nativeShare = rememberNativeShare()

    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.copy_link)) },
            onClick = {
                onDismiss()
                scope.launch {
                    clipboard.setClipEntry(plainTextClipEntry(shop.shareLink()))
                    onLinkCopied()
                }
            },
        )
        if (nativeShare != null) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.share)) },
                onClick = {
                    onDismiss()
                    nativeShare(shop.shareText())
                },
            )
        }
    }
}

// visible in Run Preview
@PreviewLightDark
@Composable
private fun CoffeeShopShareMenuPreview() = PreviewTheme {
    CoffeeShopShareMenu(
        shop = CoffeeShop("name", "description", 0.0, 0.0,),
        expanded = true,
        onDismiss = {},
        onLinkCopied = {}
    )
}
