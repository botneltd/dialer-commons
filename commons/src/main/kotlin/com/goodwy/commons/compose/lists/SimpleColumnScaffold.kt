package com.goodwy.commons.compose.lists

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.ui.unit.dp
import com.goodwy.commons.compose.extensions.AdjustNavigationBarColors
import com.goodwy.commons.compose.extensions.rememberMutableInteractionSource
import com.goodwy.commons.compose.theme.AppThemeSurface

// -----------------------------
// MAIN SimpleColumnScaffold
// -----------------------------
@Composable
fun SimpleColumnScaffold(
    title: String,
    goBack: () -> Unit,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    isTopAppBarColorIcon: Boolean = false,
    isTopAppBarColorTitle: Boolean = false,
    content: @Composable ColumnScope.(PaddingValues) -> Unit
) {
    val scrollState = rememberScrollState()
    val navigationIconInteractionSource = rememberMutableInteractionSource()

    AdjustNavigationBarColors()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        color = if (isTopAppBarColorTitle) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isTopAppBarColorIcon) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(paddingValues),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) {
            content(paddingValues)
        }
    }
}

// -----------------------------
// OVERLOAD: composable title
// -----------------------------
@Composable
fun SimpleColumnScaffold(
    title: @Composable (scrolledColor: Color) -> Unit,
    goBack: () -> Unit,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.(PaddingValues) -> Unit
) {
    SimpleColumnScaffold(
        title = "About",
        goBack = goBack,
        modifier = modifier,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

// -----------------------------
// OVERLOAD: composable title + actions
// -----------------------------
@Composable
fun SimpleColumnScaffold(
    title: @Composable (scrolledColor: Color) -> Unit,
    actions: @Composable RowScope.() -> Unit,
    goBack: () -> Unit,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.(PaddingValues) -> Unit
) {
    SimpleColumnScaffold(
        title = "About",
        goBack = goBack,
        modifier = modifier,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

// -----------------------------
// OVERLOAD: custom TopBar
// -----------------------------
@Composable
fun SimpleColumnScaffold(
    customTopBar: @Composable (scrolledColor: Color) -> Unit,
    goBack: () -> Unit,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollState: androidx.compose.foundation.ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.(PaddingValues) -> Unit
) {
    val navigationIconInteractionSource = rememberMutableInteractionSource()

    AdjustNavigationBarColors()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            customTopBar(Color.Transparent)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(paddingValues),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) {
            content(paddingValues)
        }
    }
}

// -----------------------------
// PREVIEW
// -----------------------------
@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
fun SimpleColumnScaffoldPreview() {
    AppThemeSurface {
        SimpleColumnScaffold(title = "About", goBack = {}) { padding ->
            Text(
                text = "This is preview content",
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Scrolling works with multiple items",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
