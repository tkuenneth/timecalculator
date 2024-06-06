package com.thomaskuenneth.zeitrechner

import android.app.Activity
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.FoldingFeature
import eu.thomaskuenneth.adaptivescaffold.AdaptiveScaffold
import eu.thomaskuenneth.adaptivescaffold.LocalFoldDef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

enum class PanelType {
    FIRST, SECOND, BOTH
}

data class UiState(
    val input: MutableState<String>,
    val output: MutableState<String>,
    val result: MutableState<Int>,
    val lastOp: MutableState<String>,
    val scope: CoroutineScope,
    val scrollState: ScrollState
) {
    val callback = { text: String ->
        handleButtonClick(text, input, output, lastOp, result)
        scope.launch {
            scrollState.animateScrollTo(Int.MAX_VALUE)
        }
    }
}

@Composable
fun Activity.TimeCalculatorScreen() {
    val clipboardManager = LocalClipboardManager.current
    val uiState = UiState(
        input = rememberSaveable { mutableStateOf("") },
        output = rememberSaveable { mutableStateOf("") },
        result = rememberSaveable { mutableIntStateOf(0) },
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

    AdaptiveScaffold(
        topBar = {
            TimeCalculatorAppBar(
                input = uiState.input.value,
                onCopyClicked = {
                    clipboardManager.setText(AnnotatedString(text = it))
                },
                onPasteClicked = {
                    uiState.input.value = clipboardManager.getText().toString().filter {
                        it.isDigit() || it == ':'
                    }
                }
            )
        },
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeCalculatorAppBar(
    input: String,
    onCopyClicked: (String) -> Unit,
    onPasteClicked: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            if (input.isNotBlank()) {
                IconButtonWithTooltip(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = stringResource(id = R.string.copy)
                ) { onCopyClicked(input) }
            }
            IconButtonWithTooltip(
                imageVector = Icons.Default.ContentPaste,
                contentDescription = stringResource(id = R.string.paste)
            ) { onPasteClicked() }
        }
    )
}

@Composable
fun TimeCalculatorPanel(
    panelType: PanelType,
    uiState: UiState
) {
    with(uiState) {
        val timesAndResult = @Composable {
            TimesAndResults(
                input = input.value,
                output = output.value,
                state = scrollState
            )
        }
        val numKeyPad = @Composable {
            NumKeypad(
                callback = callback
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.BottomCenter
        ) {
            val swap = with(LocalFoldDef.current) {
                orientation == FoldingFeature.Orientation.HORIZONTAL
            }
            when (panelType) {
                PanelType.FIRST -> {
                    if (swap) timesAndResult() else numKeyPad()
                }

                PanelType.SECOND -> {
                    if (swap) numKeyPad() else timesAndResult()
                }

                PanelType.BOTH -> {
                    BoxWithConstraints() {
                        if (maxWidth / maxHeight < 1F) {
                            Column {
                                Box(modifier = Modifier
                                    .weight(1F)
                                    .padding(horizontal = 16.dp)) {
                                    timesAndResult()
                                }
                                numKeyPad()
                            }
                        } else {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Box(
                                    modifier = Modifier.weight(0.7F),
                                    contentAlignment = Alignment.BottomStart
                                ) {
                                    numKeyPad()
                                }
                                Box(modifier = Modifier.weight(0.3F)) {
                                    timesAndResult()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimesAndResults(
    input: String,
    output: String,
    state: ScrollState
) {
    Column {
        TimeInput(input)
        Text(
            text = output,
            color = MaterialTheme.colorScheme.primary,
            modifier =
            Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .verticalScroll(state = state),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TimeInput(t: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.tertiaryContainer)
            .padding(8.dp)
    ) {
        Text(
            text = t.ifEmpty { stringResource(id = R.string.input_hint) },
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun NumKeypad(
    callback: (text: String) -> Any
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        NumKeypadRow(
            listOf("7", "8", "9", "CE"),
            listOf(0.25f, 0.25f, 0.25f, 0.25f),
            callback
        )
        NumKeypadRow(
            listOf("4", "5", "6", "-"),
            listOf(0.25f, 0.25f, 0.25f, 0.25f),
            callback
        )
        NumKeypadRow(
            listOf("1", "2", "3", "+"),
            listOf(0.25f, 0.25f, 0.25f, 0.25f),
            callback
        )
        NumKeypadRow(
            listOf("0", ":", "="),
            listOf(0.5f, 0.25f, 0.25f),
            callback
        )
    }
}

@Composable
fun NumKeypadRow(
    texts: List<String>,
    weights: List<Float>,
    callback: (text: String) -> Any
) {
    Row {
        for (i in texts.indices) {
            NumKeypadButton(
                text = texts[i],
                modifier = Modifier
                    .weight(weights[i])
                    .padding(end = if (i < texts.size - 1) 8.dp else 0.dp),
                callback = callback
            )
        }
    }
}

@Composable
fun NumKeypadButton(
    text: String,
    callback: (text: String) -> Any,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    Button(
        modifier = modifier,
        onClick = {
            hapticFeedback.performHapticFeedback(hapticFeedbackType = LongPress)
            callback(text)
        }
    ) {
        Text(text)
    }
}

@Composable
fun Help() {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val items = listOf(R.string.info1, R.string.info2, R.string.info3, R.string.info4)
        itemsIndexed(
            items = items,
        ) { index, item ->
            Text(
                text = stringResource(id = item),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
            if (index < items.lastIndex)
                HorizontalDivider(thickness = 1.dp)
        }
    }
}

@Composable
fun shouldShowHelp() = with(LocalFoldDef.current) {
    if (hasFold) {
        when (orientation) {
            FoldingFeature.Orientation.VERTICAL ->
                windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
                        windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT

            FoldingFeature.Orientation.HORIZONTAL ->

                windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT &&
                        windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT

            else -> false
        }
    } else {
        if (isPortrait)
            windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.EXPANDED
        else
            windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    }
}

private fun handleButtonClick(
    txt: String,
    inputTextView: MutableState<String>,
    outputTextView: MutableState<String>,
    lastOp: MutableState<String>,
    result: MutableState<Int>
) {
    when (txt) {
        "CE" -> if (inputTextView.value.isNotEmpty()) {
            inputTextView.value = ""
        } else {
            result.value = 0
            lastOp.value = "+"
            outputTextView.value = ""
        }

        "+", "-", "=" -> handleInput(inputTextView, outputTextView, txt, lastOp, result)
        ":" -> inputTextView.value += txt
        else -> inputTextView.value += txt
    }
}

private fun handleInput(
    inputTextView: MutableState<String>,
    outputTextView: MutableState<String>,
    op: String,
    lastOp: MutableState<String>,
    result: MutableState<Int>
) {
    var line = inputTextView.value
    if (!line.contains(":")) {
        if (line.length == 4) {
            line = line.substring(0, 2) + ":" + line.substring(2)
        } else {
            var total = 0
            if (line.isNotEmpty()) {
                total = getIntFromString(line)
            }
            val hours = total / 60
            val minutes = total % 60
            // trim() is important because we further process the result
            line = getTimeAsString(hours, minutes).trim { it <= ' ' }
        }
    }
    val pos = line.indexOf(':')
    var hours = getIntFromString(line.substring(0, pos))
    var minutes = getIntFromString(line.substring(pos + 1))
    var total = hours * 60 + minutes
    if ("-" == lastOp.value) {
        total = -total
    }
    lastOp.value = op
    result.value += total
    if (outputTextView.value.isNotEmpty()) {
        outputTextView.value += "\n"
    }
    outputTextView.value += "${getTimeAsString(hours, minutes)} $op"
    if ("=" == op) {
        val temp: Int = if (result.value < 0) -result.value else result.value
        hours = temp / 60
        minutes = temp % 60
        val strResult = getTimeAsString(hours, minutes).trim { it <= ' ' }
        outputTextView.value += " ${if (result.value < 0) "-" else ""}${strResult}\n"
        result.value = 0
        inputTextView.value = strResult
    } else {
        inputTextView.value = ""
    }
}

private fun getTimeAsString(hours: Int, minutes: Int): String {
    return String.format(Locale.US, "%3d:%02d", hours, minutes)
}

private fun getIntFromString(s: String): Int {
    return try {
        s.toInt()
    } catch (e: NumberFormatException) {
        0
    }
}
