package com.thomaskuenneth.zeitrechner

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetrics
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun Content(
    layoutInfo: WindowLayoutInfo?,
    windowMetrics: WindowMetrics,
    paddingValues: PaddingValues
) {
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
    val hingeDef = createHingeDef(layoutInfo, windowMetrics)
    val isLargeScreen = windowWidthDp(windowMetrics) >= 600.dp
    val isFoldable = hingeDef.widthLeft > 0.dp && hingeDef.widthRight > 0.dp
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .background(color = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        if (isFoldable || !isLargeScreen) {
            val leftColumnWidth = if (isFoldable) hingeDef.widthLeft else maxWidth
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                // left column
                Column(
                    modifier = Modifier
                        .width(leftColumnWidth)
                        .fillMaxHeight()
                ) {
                    TimesAndResults(
                        modifier = Modifier.weight(1.0F),
                        input = input,
                        output = output,
                        state = state
                    )
                    NumKeypad(callback = callback)
                }
                // optional right column
                if (isFoldable) {
                    if (hingeDef.hasVerticalGap) {
                        with(LocalDensity.current) {
                            Spacer(modifier = Modifier.width(hingeDef.boundsGap.width().toDp()))
                        }
                    }
                    Help(hingeDef.widthRight)
                }
            }
        } else {
            val width = min(maxWidth / 2, 400.dp)
            val helpWidth = maxWidth - (width + width)
            val shouldShowHelp = helpWidth >= 300.dp
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumKeypad(
                    modifier = Modifier.width(width = width),
                    callback = callback
                )
                TimesAndResults(
                    modifier = if (shouldShowHelp)
                        Modifier.width(width)
                    else
                        Modifier.weight(1F),
                    input = input,
                    output = output,
                    state = state
                )
                if (shouldShowHelp)
                    Help(width = helpWidth)
            }
        }
    }
}


@Composable
fun TimesAndResults(
    modifier: Modifier,
    input: MutableState<String>,
    output: MutableState<String>,
    state: ScrollState
) {
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        TimeInput(input.value)
        Text(
            text = output.value,
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
fun NumKeypad(modifier: Modifier = Modifier, callback: (text: String) -> Any) {
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
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
            MyButton(
                text = texts[i],
                modifier = Modifier.weight(weights[i]),
                callback = callback
            )
        }
    }
}

@Composable
fun MyButton(
    text: String,
    callback: (text: String) -> Any,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier
            .padding(4.dp),
        onClick = {
            callback(text)
        }
    ) {
        Text(text)
    }
}

@Composable
fun Help(width: Dp) {
    LazyColumn(
        modifier = Modifier
            .width(width)
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
