package com.goodwy.commons.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.goodwy.commons.R
import com.goodwy.commons.compose.alert_dialog.rememberAlertDialogState
import com.goodwy.commons.dialogs.ConfirmationAlertDialog

/** Kept as no-op for API compatibility; fake-app nag and Goodwy store redirect are disabled for Sorwi fork. */
@Composable
fun FakeVersionCheck() {
}

@Composable
fun CheckAppOnSdCard() {
    val context = LocalContext.current.getComponentActivity()
    val confirmationDialogAlertDialogState = rememberAlertDialogState().apply {
        DialogMember {
            ConfirmationAlertDialog(
                alertDialogState = this,
                messageId = R.string.app_on_sd_card,
                positive = R.string.ok,
                negative = null
            ) {}
        }
    }
    LaunchedEffect(Unit) {
        context.appOnSdCardCheckCompose(confirmationDialogAlertDialogState::show)
    }
}
