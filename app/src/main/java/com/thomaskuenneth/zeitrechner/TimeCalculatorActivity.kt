package com.thomaskuenneth.zeitrechner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import eu.thomaskuenneth.adaptivescaffold.AdaptiveScaffold
import eu.thomaskuenneth.adaptivescaffold.LocalWindowSizeClass
import eu.thomaskuenneth.adaptivescaffold.defaultColorScheme
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
class TimeCalculatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setContent {
                    val uiState = UiState(
                        input = rememberSaveable { mutableStateOf("") },
                        output = rememberSaveable { mutableStateOf("") },
                        result = rememberSaveable { mutableStateOf(0) },
                        lastOp = rememberSaveable { mutableStateOf("") },
                        scope = rememberCoroutineScope(),
                        scrollState = rememberScrollState()
                    )
                    val content: @Composable (PanelType) -> Unit = { panel ->
                        TimeCalculatorPanel(
                            panelType = panel,
                            uiState = uiState
                        )
                    }

                    MaterialTheme(
                        content = {
                            AdaptiveScaffold(
                                body = {
                                    content(
                                        if (shouldShowHelp())
                                            PanelType.BOTH
                                        else
                                            PanelType.FIRST
                                    )
                                },
                                secondaryBody = {
                                    if (shouldShowHelp())
                                        Help()
                                    else
                                        content(
                                            PanelType.SECOND
                                        )
                                },
                                smallBody = {
                                    content(
                                        PanelType.BOTH
                                    )
                                }
                            )
                        },
                        colorScheme = defaultColorScheme()
                    )
                }
            }
        }
    }
}

@Composable
fun shouldShowHelp() = with(LocalWindowSizeClass.current) {
    widthSizeClass != WindowWidthSizeClass.COMPACT
            && heightSizeClass != WindowHeightSizeClass.COMPACT
}
