package com.goodwy.commons.views

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import com.google.android.material.appbar.AppBarLayout
import com.goodwy.commons.R
import com.goodwy.commons.activities.BaseSimpleActivity
import com.goodwy.commons.databinding.MenuSearchTopBinding
import com.goodwy.commons.extensions.*
import androidx.annotation.AttrRes

fun Context.getColorAttr(@AttrRes attr: Int, defaultColor: Int = Color.BLACK): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

open class MySearchMenuTop(context: Context, attrs: AttributeSet) : AppBarLayout(context, attrs) {
    var isSearchOpen = false
    var useArrowIcon = false
    var onSearchOpenListener: (() -> Unit)? = null
    var onSearchClosedListener: (() -> Unit)? = null
    var onSearchTextChangedListener: ((text: String) -> Unit)? = null
    var onNavigateBackClickListener: (() -> Unit)? = null

    val binding = MenuSearchTopBinding.inflate(LayoutInflater.from(context), this, true)

    fun getToolbar() = binding.topToolbar

    fun setupMenu() {
        binding.topToolbarSearchIcon.setOnClickListener {
            if (isSearchOpen) {
                closeSearch()
            } else if (useArrowIcon && onNavigateBackClickListener != null) {
                onNavigateBackClickListener!!()
            } else {
                binding.topToolbarSearch.requestFocus()
                (context as? Activity)?.showKeyboard(binding.topToolbarSearch)
            }
        }

        post {
            binding.topToolbarSearch.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    openSearch()
                }
            }
        }

        binding.topToolbarSearch.onTextChangeListener { text ->
            val size = text.length
//            if (size == 1) post { openSearch() }
            if (size == 0 && !useArrowIcon) {
//                binding.topToolbarSearchIcon.setImageResource(R.drawable.ic_search_vector)
                binding.topToolbarSearchIcon.contentDescription = resources.getString(R.string.search)
            }
            onSearchTextChangedListener?.invoke(text)
        }
    }

    fun focusView() {
        binding.topToolbarSearch.requestFocus()
    }

    private fun openSearch() {
        isSearchOpen = true
        onSearchOpenListener?.invoke()
        binding.topToolbarSearchIcon.setImageResource(R.drawable.ic_chevron_left_vector)
        binding.topToolbarSearchIcon.contentDescription = resources.getString(R.string.back)
    }

    fun closeSearch() {
        isSearchOpen = false
        onSearchClosedListener?.invoke()
        binding.topToolbarSearch.setText("")
        if (!useArrowIcon) {
            binding.topToolbarSearchIcon.setImageResource(R.drawable.ic_search_vector)
            binding.topToolbarSearchIcon.contentDescription = resources.getString(R.string.search)
        }
        (context as? Activity)?.hideKeyboard()
    }

    fun getCurrentQuery() = binding.topToolbarSearch.text.toString()

    fun updateHintText(text: String) {
        binding.topToolbarSearch.hint = text
    }

    fun toggleHideOnScroll(hideOnScroll: Boolean) {
        val params = binding.topAppBarLayout.layoutParams as LayoutParams
        if (hideOnScroll) {
            params.scrollFlags = LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        } else {
            params.scrollFlags = params.scrollFlags.removeBit(LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        }
    }

    fun toggleForceArrowBackIcon(useArrowBack: Boolean) {
        this.useArrowIcon = useArrowBack
        val (icon, accessibilityString) = if (useArrowBack) {
            Pair(R.drawable.ic_chevron_left_vector, R.string.back)
        } else {
            Pair(R.drawable.ic_search_vector, R.string.search)
        }

        binding.topToolbarSearchIcon.setImageResource(icon)
        binding.topToolbarSearchIcon.contentDescription = resources.getString(accessibilityString)
    }

    fun updateColors() {
        // Fetch colors from the Material3 Dark theme
        val surface = context.getColorAttr(com.google.android.material.R.attr.colorSurface)
        val onSurface = context.getColorAttr(com.google.android.material.R.attr.colorOnSurface)
        val primary = context.getColorAttr(com.google.android.material.R.attr.colorPrimary)
        val surfaceVariant = context.getColorAttr(com.google.android.material.R.attr.colorSurfaceVariant)
        val onSurfaceVariant = context.getColorAttr(com.google.android.material.R.attr.colorOnSurfaceVariant)

        // Set backgrounds
        setBackgroundColor(surface)
        binding.topAppBarLayout.setBackgroundColor(surface)

        // Search icon color: primary or onSurface
        val searchIconColor = if (context.baseConfig.topAppBarColorIcon) primary else onSurface
        binding.topToolbarSearchIcon.applyColorFilter(searchIconColor)

        // Text field styling: text and hint
        binding.topToolbarSearch.setTextColor(onSurface)
        binding.topToolbarSearch.setHintTextColor(onSurfaceVariant)

        // Top bar colors — match the BaseSimpleActivity signature
        // Most versions: updateTopBarColors(toolbar: Toolbar, colorBackground: Int)
        (context as? BaseSimpleActivity)?.updateTopBarColors(
            binding.topToolbar,
            Color.TRANSPARENT
        )

        // Toolbar holder background drawable tinted with surfaceVariant
        binding.topToolbarHolder.setBackgroundResource(R.drawable.search_bg)
        binding.topToolbarHolder.backgroundTintList = ColorStateList.valueOf(surfaceVariant)

        // Clear icon uses onSurface for readability
        binding.topToolbarSearchClear.applyColorFilter(onSurface)
    }


    fun updateTitle(title: String) {
        binding.topToolbar.title = title
    }

    fun searchBeVisibleIf(visible: Boolean = true) {
        binding.topToolbarHolder.beVisibleIf(visible)
    }

    fun requestFocusAndShowKeyboard() {
        binding.topToolbarSearch.requestFocus()
        (context as? Activity)?.showKeyboard(binding.topToolbarSearch)
    }

    fun clearSearch() {
        binding.topToolbarSearchClear.beVisibleIf(binding.topToolbarSearch.text!!.isNotEmpty())
        binding.topToolbarSearchClear.setOnClickListener {
            binding.topToolbarSearch.setText("")
        }
    }
}
