package com.goodwy.commons.extensions

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.loader.content.CursorLoader
import com.google.android.material.color.MaterialColors
import com.goodwy.commons.R
import com.goodwy.commons.helpers.*
import com.goodwy.commons.helpers.MyContentProvider.GLOBAL_THEME_SYSTEM
import com.goodwy.commons.models.GlobalConfig
import com.goodwy.commons.models.isGlobalThemingEnabled
import com.goodwy.commons.views.*
import android.content.ComponentName
import android.content.pm.PackageManager

// -------------------------------------------------------------
// FORCE ALWAYS DARK MODE
// -------------------------------------------------------------

fun Context.isDynamicTheme() = false
fun Context.isSystemInDarkMode() = true
fun Context.isAutoTheme() = false
fun Context.isLightTheme() = false
fun Context.isGrayTheme() = false
fun Context.isDarkTheme() = true
fun Context.isBlackTheme() = true

// -------------------------------------------------------------
// Material3 dark color tokens
// -------------------------------------------------------------

fun Context.getProperTextColor(): Int =
    MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface, Color.WHITE)

fun Context.getProperBackgroundColor(): Int =
    MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface, Color.BLACK)

fun Context.getProperPrimaryColor(): Int =
    MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, Color.WHITE)

fun Context.getProperAccentColor(): Int = getProperPrimaryColor()

fun Context.getProperStatusBarColor(): Int =
    MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurfaceVariant, Color.BLACK)

fun Context.getBottomNavigationBackgroundColor(): Int =
    MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurfaceVariant, Color.DKGRAY)

fun Context.getDialogBackgroundColor(): Int =
    MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface, Color.BLACK)

fun Context.getSurfaceColor(): Int =
    MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurfaceContainerLow, Color.BLACK)

fun Context.getProperTextCursorColor(): Int = getProperPrimaryColor()

fun Context.getColoredMaterialStatusBarColor(): Int =
    getBottomNavigationBackgroundColor().lightenColor(2)

// -------------------------------------------------------------
// Text + widget coloring
// -------------------------------------------------------------

fun Context.updateTextColors(viewGroup: ViewGroup) {
    val textColor = getProperTextColor()
    val backgroundColor = getProperBackgroundColor()
    val accentColor = getProperPrimaryColor()
    val cursorColor = getProperTextCursorColor()

    for (i in 0 until viewGroup.childCount) {
        when (val v = viewGroup.getChildAt(i)) {
            is MyTextView -> v.setColors(textColor, accentColor, backgroundColor)
            is MyAppCompatSpinner -> v.setColors(textColor, accentColor, backgroundColor)
            is MyCompatRadioButton -> v.setColors(textColor, accentColor, backgroundColor)
            is MyAppCompatCheckbox -> v.setColors(textColor, accentColor, backgroundColor)
            is MyMaterialSwitch -> v.setColors(textColor, accentColor, backgroundColor)
            is MyEditText -> v.setColors(textColor, accentColor, cursorColor)
            is MyAutoCompleteTextView -> v.setColors(textColor, accentColor, cursorColor)
            is MyFloatingActionButton -> v.setColors(textColor, accentColor, backgroundColor)
            is MySeekBar -> v.setColors(textColor, accentColor, backgroundColor.getContrastColor())
            is MyButton -> v.setColors(textColor, accentColor, backgroundColor)
            is MyTextInputLayout -> v.setColors(textColor, accentColor, backgroundColor)
            is ViewGroup -> updateTextColors(v)
        }
    }
}

// -------------------------------------------------------------
// Always-dark dialogs & pickers
// -------------------------------------------------------------

fun Context.getTimePickerDialogTheme() = R.style.MyTimePickerMaterialTheme_Dark
fun Context.getDatePickerDialogTheme() = R.style.MyDateTimePickerMaterialTheme
fun Context.getPopupMenuTheme(): Int = R.style.AppTheme_PopupMenuDarkStyle

// -------------------------------------------------------------
// ORIGINAL FUNCTIONS (UNCHANGED) — syncGlobalConfig etc.
// -------------------------------------------------------------

fun Context.syncGlobalConfig(callback: (() -> Unit)? = null) {
    if (canAccessGlobalConfig()) {
        withGlobalConfig {
            if (it != null) {
                baseConfig.apply {
                    showCheckmarksOnSwitches = it.showCheckmarksOnSwitches
                    if (it.isGlobalThemingEnabled()) {
                        isGlobalThemeEnabled = true
                        isSystemThemeEnabled = it.themeType == GLOBAL_THEME_SYSTEM
                        textColor = it.textColor
                        backgroundColor = it.backgroundColor
                        primaryColor = it.primaryColor
                        accentColor = it.accentColor

                        if (baseConfig.appIconColor != it.appIconColor) {
                            baseConfig.appIconColor = it.appIconColor
                            checkAppIconColor()
                        }
                    }
                }
            }
            callback?.invoke()
        }
    } else {
        baseConfig.isGlobalThemeEnabled = false
        baseConfig.showCheckmarksOnSwitches = false
        callback?.invoke()
    }
}

fun Context.withGlobalConfig(callback: (globalConfig: GlobalConfig?) -> Unit) {
    if (!isPro()) {
        callback(null)
    } else {
        val cursorLoader = getMyContentProviderCursorLoader()
        ensureBackgroundThread {
            callback(getGlobalConfig(cursorLoader))
        }
    }
}

fun Context.getGlobalConfig(cursorLoader: CursorLoader): GlobalConfig? {
    val cursor = cursorLoader.loadInBackground()
    cursor?.use {
        if (cursor.moveToFirst()) {
            try {
                return GlobalConfig(
                    themeType = cursor.getIntValue(MyContentProvider.COL_THEME_TYPE),
                    textColor = cursor.getIntValue(MyContentProvider.COL_TEXT_COLOR),
                    backgroundColor = cursor.getIntValue(MyContentProvider.COL_BACKGROUND_COLOR),
                    primaryColor = cursor.getIntValue(MyContentProvider.COL_PRIMARY_COLOR),
                    accentColor = cursor.getIntValue(MyContentProvider.COL_ACCENT_COLOR),
                    appIconColor = cursor.getIntValue(MyContentProvider.COL_APP_ICON_COLOR),
                    showCheckmarksOnSwitches = cursor.getIntValue(MyContentProvider.COL_SHOW_CHECKMARKS_ON_SWITCHES) != 0,
                    lastUpdatedTS = cursor.getIntValue(MyContentProvider.COL_LAST_UPDATED_TS)
                )
            } catch (_: Exception) { }
        }
    }
    return null
}

fun Context.checkAppIconColor() {
    val appId = baseConfig.appId
    if (appId.isNotEmpty() && baseConfig.lastIconColor != baseConfig.appIconColor) {
        getAppIconColors().forEachIndexed { index, color ->
            toggleAppIconColor(appId, index, color, false)
        }

        getAppIconColors().forEachIndexed { index, color ->
            if (baseConfig.appIconColor == color) {
                toggleAppIconColor(appId, index, color, true)
            }
        }
    }
}

fun Context.toggleAppIconColor(appId: String, colorIndex: Int, color: Int, enable: Boolean) {
    val className = "${appId.removeSuffix(".debug")}.activities.SplashActivity${appIconColorStrings[colorIndex]}"
    val state = if (enable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    try {
        packageManager.setComponentEnabledSetting(ComponentName(appId, className), state, PackageManager.DONT_KILL_APP)
        if (enable) baseConfig.lastIconColor = color
    } catch (e: Exception) {
        showErrorToast(e)
    }
}

fun Context.getAppIconColors() = resources.getIntArray(R.array.md_app_icon_colors).toCollection(ArrayList())
