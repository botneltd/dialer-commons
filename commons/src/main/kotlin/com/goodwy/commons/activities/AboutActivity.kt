package com.goodwy.commons.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goodwy.commons.R
import com.goodwy.commons.compose.extensions.config
import com.goodwy.commons.compose.extensions.enableEdgeToEdgeSimple
import com.goodwy.commons.compose.screens.AboutNewSection
import com.goodwy.commons.compose.screens.AboutScreen
import com.goodwy.commons.compose.theme.AppThemeSurface
import com.goodwy.commons.extensions.*
import com.goodwy.commons.helpers.*
import com.goodwy.commons.models.FAQItem

class AboutActivity : BaseComposeActivity() {

    private val appName get() = intent.getStringExtra(APP_NAME) ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        enableEdgeToEdgeSimple()

        setContent {
            val isTopAppBarColorIcon by config.isTopAppBarColorIcon.collectAsStateWithLifecycle(
                initialValue = config.topAppBarColorIcon
            )
            val isTopAppBarColorTitle by config.isTopAppBarColorTitle.collectAsStateWithLifecycle(
                initialValue = config.topAppBarColorTitle
            )
            AppThemeSurface {
                // Only one AboutScreen with TopAppBar
                AboutScreen(
                    goBack = ::finish,
                    aboutSection = {
                        AboutNewSection(
                            appName = appName,
                            appVersion = intent.getStringExtra(APP_VERSION_NAME) ?: "",
                            onMoreAppsClick = ::launchMoreAppsFromUsIntent,
                            onPrivacyPolicyClick = ::onPrivacyPolicyClick,
                            onFAQClick = ::launchFAQActivity,
                            onTipJarClick = ::onTipJarClick,
                            onGithubClick = ::onGithubClick,
                            showGithub = showGithub(),
                        )
                    },
                    isTopAppBarColorIcon = isTopAppBarColorIcon,
                    isTopAppBarColorTitle = isTopAppBarColorTitle
                )
            }
        }
    }

    private fun launchFAQActivity() {
        val faqItems = intent.getSerializableExtra(APP_FAQ) as ArrayList<FAQItem>
        Intent(applicationContext, FAQActivity::class.java).apply {
            putExtra(APP_ICON_IDS, intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList<String>())
            putExtra(APP_LAUNCHER_NAME, intent.getStringExtra(APP_LAUNCHER_NAME) ?: "")
            putExtra(APP_FAQ, faqItems)
            startActivity(this)
        }
    }

    private fun onPrivacyPolicyClick() {
        val appId = baseConfig.appId.removeSuffix(".debug")
        val url = when (appId) {
            "com.sorwi.aidialer" -> "https://annoying.fi/dialerapp/privacy_policy"
            "com.goodwy.smsmessenger" -> "https://www.goodwy.dev/about/privacy-policy-right-messages"
            "com.goodwy.contacts" -> "https://www.goodwy.dev/about/privacy-policy-right-contacts"
            "com.goodwy.gallery" -> "https://www.goodwy.dev/about/privacy-policy-right-gallery"
            "com.goodwy.filemanager" -> "https://www.goodwy.dev/about/privacy-policy-right-files"
            "com.goodwy.voicerecorder", "com.goodwy.voicerecorderfree" -> "https://www.goodwy.dev/about/privacy-policy-right-voice-recorder"
            "com.goodwy.calendar" -> "https://www.goodwy.dev/about/privacy-policy-right-calendar"
            else -> "https://www.goodwy.dev/about/privacy-policy"
        }
        launchViewIntent(url)
    }

    private fun onTipJarClick() {
        Intent(applicationContext, PurchaseActivity::class.java).apply {
            putExtra(APP_ICON_IDS, intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList<String>())
            putExtra(APP_LAUNCHER_NAME, intent.getStringExtra(APP_LAUNCHER_NAME) ?: "")
            putExtra(APP_NAME, intent.getStringExtra(APP_NAME) ?: "")
            putExtra(PRODUCT_ID_LIST, intent.getStringArrayListExtra(PRODUCT_ID_LIST) ?: arrayListOf("", "", ""))
            putExtra(PRODUCT_ID_LIST_RU, intent.getStringArrayListExtra(PRODUCT_ID_LIST_RU) ?: arrayListOf("", "", ""))
            putExtra(SUBSCRIPTION_ID_LIST, intent.getStringArrayListExtra(SUBSCRIPTION_ID_LIST) ?: arrayListOf("", "", ""))
            putExtra(SUBSCRIPTION_ID_LIST_RU, intent.getStringArrayListExtra(SUBSCRIPTION_ID_LIST_RU) ?: arrayListOf("", "", ""))
            putExtra(SUBSCRIPTION_YEAR_ID_LIST, intent.getStringArrayListExtra(SUBSCRIPTION_YEAR_ID_LIST) ?: arrayListOf("", "", ""))
            putExtra(SUBSCRIPTION_YEAR_ID_LIST_RU, intent.getStringArrayListExtra(SUBSCRIPTION_YEAR_ID_LIST_RU) ?: arrayListOf("", "", ""))
            putExtra(SHOW_LIFEBUOY, resources.getBoolean(R.bool.show_lifebuoy))
            putExtra(PLAY_STORE_INSTALLED, intent.getBooleanExtra(PLAY_STORE_INSTALLED, true))
            putExtra(RU_STORE, intent.getBooleanExtra(RU_STORE, false))
            putExtra(SHOW_COLLECTION, resources.getBoolean(R.bool.show_collection))
            startActivity(this)
        }
    }

    private fun onGithubClick() {
        val repositoryName = intent.getStringExtra(APP_REPOSITORY_NAME) ?: return
        val url = "https://github.com/Goodwy/$repositoryName"
        launchViewIntent(url)
    }

    @Composable
    private fun showGithub() = remember { !intent.getStringExtra(APP_REPOSITORY_NAME).isNullOrEmpty() }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changeAutoTheme()
    }

    private fun changeAutoTheme() {
        syncGlobalConfig {
            baseConfig.apply {
                if (isAutoTheme()) {
                    val isUsingSystemDarkTheme = isSystemInDarkMode()
                    textColor = resources.getColor(
                        if (isUsingSystemDarkTheme) R.color.theme_dark_text_color
                        else R.color.theme_light_text_color
                    )
                    backgroundColor = resources.getColor(
                        if (isUsingSystemDarkTheme) R.color.theme_dark_background_color
                        else R.color.theme_light_background_color
                    )
                    finish()
                    startActivity(intent)
                }
            }
        }
    }
}
