package com.goodwy.commons.extensions

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.loader.content.CursorLoader
import com.google.android.material.color.MaterialColors
import com.goodwy.commons.R
import com.goodwy.commons.helpers.MyContentProvider
import com.goodwy.commons.helpers.appIconColorStrings
import com.goodwy.commons.helpers.ensureBackgroundThread
import com.goodwy.commons.helpers.isSPlus
import com.goodwy.commons.models.GlobalConfig
import com.goodwy.commons.models.isGlobalThemingEnabled
import com.goodwy.commons.views.*

// System / Material You (S+) vs auto light–dark (pre-S): follow device when user chose System theme.
fun Context.isDynamicTheme(): Boolean =
    baseConfig.isSystemThemeEnabled && isSPlus()

fun Context.isAutoTheme(): Boolean =
    baseConfig.isSystemThemeEnabled && !isSPlus()

fun Context.isSystemInDarkMode(): Boolean {
    val mask = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return mask == Configuration.UI_MODE_NIGHT_YES
}

fun Context.isGrayTheme(): Boolean {
    if (baseConfig.isSystemThemeEnabled) return false
    return baseConfig.backgroundColor == ContextCompat.getColor(this, R.color.theme_gray_background_color)
}

fun Context.isBlackTheme(): Boolean {
    if (baseConfig.isSystemThemeEnabled) return false
    val blackPreset = ContextCompat.getColor(this, R.color.theme_black_background_color)
    return baseConfig.backgroundColor == blackPreset ||
        baseConfig.backgroundColor == Color.BLACK ||
        ColorUtils.calculateLuminance(baseConfig.backgroundColor) < 0.03
}

fun Context.isLightTheme(): Boolean {
    if (baseConfig.isSystemThemeEnabled) return !isSystemInDarkMode()
    if (isGrayTheme() || isBlackTheme()) return false
    val lightBg = ContextCompat.getColor(this, R.color.theme_light_background_color)
    val darkBg = ContextCompat.getColor(this, R.color.theme_dark_background_color)
    if (baseConfig.backgroundColor == lightBg) return true
    if (baseConfig.backgroundColor == darkBg) return false
    return ColorUtils.calculateLuminance(baseConfig.backgroundColor) > 0.5f
}

fun Context.isDarkTheme(): Boolean {
    if (baseConfig.isSystemThemeEnabled) return isSystemInDarkMode()
    if (isGrayTheme() || isBlackTheme()) return false
    val lightBg = ContextCompat.getColor(this, R.color.theme_light_background_color)
    val darkBg = ContextCompat.getColor(this, R.color.theme_dark_background_color)
    if (baseConfig.backgroundColor == lightBg) return false
    if (baseConfig.backgroundColor == darkBg) return true
    return ColorUtils.calculateLuminance(baseConfig.backgroundColor) <= 0.5f
}

// -------------------------------------------------------------
// Colors: Material You (system theme) vs saved customization
// -------------------------------------------------------------

fun Context.getProperTextColor(): Int =
    if (isDynamicTheme()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface, Color.WHITE)
    } else {
        baseConfig.textColor
    }

fun Context.getProperBackgroundColor(): Int =
    if (isDynamicTheme()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface, Color.BLACK)
    } else {
        baseConfig.backgroundColor
    }

fun Context.getProperPrimaryColor(): Int =
    if (isDynamicTheme()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, Color.WHITE)
    } else {
        baseConfig.primaryColor
    }

fun Context.getProperAccentColor(): Int =
    if (isDynamicTheme()) getProperPrimaryColor() else baseConfig.accentColor

fun Context.getProperStatusBarColor(): Int =
    if (isDynamicTheme()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurfaceVariant, Color.BLACK)
    } else {
        baseConfig.backgroundColor
    }

fun Context.getBottomNavigationBackgroundColor(): Int =
    if (isDynamicTheme()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurfaceVariant, Color.DKGRAY)
    } else {
        baseConfig.backgroundColor
    }

fun Context.getDialogBackgroundColor(): Int =
    if (isDynamicTheme()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface, Color.BLACK)
    } else {
        baseConfig.backgroundColor
    }

fun Context.getSurfaceColor(): Int =
    if (isDynamicTheme()) {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurfaceContainerLow, Color.BLACK)
    } else {
        baseConfig.backgroundColor.lightenColor(4)
    }

fun Context.getProperTextCursorColor(): Int =
    if (isDynamicTheme()) getProperPrimaryColor() else baseConfig.textCursorColor

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
// Dialogs & pickers (match light / dark / Material You)
// -------------------------------------------------------------

fun Context.getTimePickerDialogTheme(): Int =
    if (isDynamicTheme() && isLightTheme()) {
        R.style.MyDateTimePickerMaterialTheme
    } else if (isDynamicTheme()) {
        R.style.MyTimePickerMaterialTheme_Dark
    } else if (isLightTheme() || isGrayTheme()) {
        R.style.MyDateTimePickerMaterialTheme
    } else {
        R.style.MyTimePickerMaterialTheme_Dark
    }

fun Context.getDatePickerDialogTheme() = R.style.MyDateTimePickerMaterialTheme

fun Context.getPopupMenuTheme(): Int = when {
    isDynamicTheme() -> R.style.AppTheme_YouPopupMenuStyle
    isLightTheme() || isGrayTheme() -> R.style.AppTheme_PopupMenuLightStyle
    else -> R.style.AppTheme_PopupMenuDarkStyle
}

// -------------------------------------------------------------
// Thank You global config (optional cross-app sync)
// -------------------------------------------------------------

fun Context.syncGlobalConfig(callback: (() -> Unit)? = null) {
    if (canAccessGlobalConfig()) {
        withGlobalConfig {
            if (it != null) {
                baseConfig.showCheckmarksOnSwitches = it.showCheckmarksOnSwitches
                if (it.isGlobalThemingEnabled()) {
                    when (it.themeType) {
                        MyContentProvider.GLOBAL_THEME_SYSTEM -> {
                            baseConfig.isGlobalThemeEnabled = true
                            baseConfig.isSystemThemeEnabled = true
                            baseConfig.textColor = it.textColor
                            baseConfig.backgroundColor = it.backgroundColor
                            baseConfig.primaryColor = it.primaryColor
                            baseConfig.accentColor = it.accentColor
                            baseConfig.appIconColor = it.appIconColor
                        }
                        MyContentProvider.GLOBAL_THEME_CUSTOM -> {
                            baseConfig.isGlobalThemeEnabled = true
                            baseConfig.isSystemThemeEnabled = false
                            baseConfig.textColor = it.textColor
                            baseConfig.backgroundColor = it.backgroundColor
                            baseConfig.primaryColor = it.primaryColor
                            baseConfig.accentColor = it.accentColor
                            baseConfig.appIconColor = it.appIconColor
                        }
                        else -> {
                            baseConfig.isGlobalThemeEnabled = false
                        }
                    }
                } else {
                    baseConfig.isGlobalThemeEnabled = false
                }
            }
            callback?.invoke()
        }
    } else {
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
