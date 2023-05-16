package com.thomaskuenneth.zeitrechner

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import eu.thomaskuenneth.adaptivescaffold.LocalWindowSizeClass
import java.util.Locale

enum class PanelType {
    FIRST, SECOND, BOTH
}

@Composable
fun TimeCalculatorPanel(
    panelType: PanelType,
    input: String,
    output: String,
    state: ScrollState,
    callback: (text: String) -> Any
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.BottomCenter
    ) {
        when (panelType) {
            PanelType.FIRST -> {
                NumKeypad(
                    callback = callback
                )
            }

            PanelType.SECOND -> {
                TimesAndResults(
                    input = input,
                    output = output,
                    state = state
                )
            }

            PanelType.BOTH -> {
                if (LocalWindowSizeClass.current.heightSizeClass != WindowHeightSizeClass.COMPACT) {
                    Column {
                        Box(modifier = Modifier.weight(1.0F)) {
                            TimesAndResults(
                                input = input,
                                output = output,
                                state = state
                            )
                        }
                        NumKeypad(
                            callback = callback
                        )
                    }
                } else {
                    Row {
                        Box(modifier = Modifier.weight(0.5F)) {
                            NumKeypad(
                                callback = callback
                            )
                        }
                        Box(modifier = Modifier.weight(0.5F)) {
                            TimesAndResults(
                                input = input,
                                output = output,
                                state = state
                            )
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
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
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
fun NumKeypad(
    callback: (text: String) -> Any
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(16.dp)
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
fun NumKeypadRow(
    texts: List<String>,
    weights: List<Float>,
    callback: (text: String) -> Any
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in texts.indices) {
            NumKeypadButton(
                text = texts[i],
                modifier = Modifier.weight(weights[i]),
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
        modifier = modifier
            .padding(4.dp),
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
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
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
                Divider(thickness = 1.dp)
        }
    }
}

fun handleButtonClick(
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
    println(inputTextView.value)
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
