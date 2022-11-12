package com.thomaskuenneth.zeitrechner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetricsCalculator

@ExperimentalMaterial3Api
class TimeCalculatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            setContent {
                val layoutInfo by WindowInfoTracker.getOrCreate(this@TimeCalculatorActivity)
                    .windowLayoutInfo(this@TimeCalculatorActivity).collectAsState(
                        initial = null
                    )
                val windowMetrics = WindowMetricsCalculator.getOrCreate()
                    .computeCurrentWindowMetrics(this@TimeCalculatorActivity)
                MaterialTheme(
                    content = {
                        Scaffold(
                            topBar = {
                                TopAppBar(title = {
                                    Text(stringResource(id = R.string.app_name))
                                })
                            }
                        ) {
                            Content(
                                layoutInfo,
                                windowMetrics,
                                it
                            )
                        }
                    },
                    colorScheme = if (isSystemInDarkTheme())
                        darkColorScheme()
                    else
                        lightColorScheme()
                )
            }
        }
    }
}
