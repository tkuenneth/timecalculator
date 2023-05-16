package com.thomaskuenneth.zeitrechner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
class TimeCalculatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setContent {
                    val input = rememberSaveable { mutableStateOf("") }
                    val output = rememberSaveable { mutableStateOf("") }
                    val result = rememberSaveable { mutableStateOf(0) }
                    val lastOp = rememberSaveable { mutableStateOf("") }
                    val state = rememberScrollState()
                    val scope = rememberCoroutineScope()
                    val callback = { text: String ->
                        handleButtonClick(text, input, output, lastOp, result)
                        scope.launch {
                            state.animateScrollTo(state.maxValue)
                        }
                    }
                    val content: @Composable (PanelType) -> Unit = { panel ->
                        TimeCalculatorPanel(
                            panelType = panel,
                            input = input.value,
                            output = output.value,
                            state = state,
                            callback = callback
                        )
                    }

                    MaterialTheme(
                        content = {
                            AdaptiveScaffold(
                                useDrawer = false,
                                index = 0,
                                onSelectedIndexChange = {},
                                destinations = emptyList(),
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
                                },
                                smallSecondaryBody = null
                            )
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
}

@Composable
fun shouldShowHelp() = with(LocalWindowSizeClass.current) {
    widthSizeClass != WindowWidthSizeClass.COMPACT
            && heightSizeClass != WindowHeightSizeClass.COMPACT
}