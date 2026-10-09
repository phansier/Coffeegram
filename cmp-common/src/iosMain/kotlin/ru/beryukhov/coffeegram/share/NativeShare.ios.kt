package ru.beryukhov.coffeegram.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController

@Composable
internal actual fun rememberNativeShare(): ((text: String) -> Unit)? {
    val viewController = LocalUIViewController.current
    return remember(viewController) {
        { text -> viewController.topmostPresented().presentShareSheet(text) }
    }
}

private fun UIViewController.topmostPresented(): UIViewController =
    presentedViewController?.topmostPresented() ?: this

@OptIn(ExperimentalForeignApi::class)
private fun UIViewController.presentShareSheet(text: String) {
    val shareSheet = UIActivityViewController(activityItems = listOf(text), applicationActivities = null)
    shareSheet.popoverPresentationController?.let { popover ->
        popover.sourceView = view
        popover.sourceRect = view.bounds
    }
    presentViewController(shareSheet, animated = true, completion = null)
}
