package com.thomaskuenneth.zeitrechner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.*

class NewMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(title = {
                        Text(stringResource(id = R.string.app_name))
                    })
                }
            ) {
                Content()
            }
        }
    }
}

@Preview
@Composable
fun Content() {
    val input = remember { mutableStateOf("") }
    val output = remember { mutableStateOf("") }
    val result = remember { mutableStateOf(0) }
    val lastOp = remember { mutableStateOf("") }
    val state = rememberScrollState()
    val scope = rememberCoroutineScope()
    val callback = { text: String ->
        handleButtonClick(text, input, output, lastOp, result)
        scope.launch {
            state.animateScrollTo(state.maxValue)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Zeile1(input.value)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = output.value,
            modifier =
            Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .verticalScroll(state = state),
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyRow(
            listOf("7", "8", "9", "CE"),
            listOf(0.25f, 0.25f, 0.25f, 0.25f),
            callback
        )
        MyRow(
            listOf("4", "5", "6", "-"),
            listOf(0.25f, 0.25f, 0.25f, 0.25f),
            callback
        )
        MyRow(
            listOf("1", "2", "3", "+"),
            listOf(0.25f, 0.25f, 0.25f, 0.25f),
            callback
        )
        MyRow(
            listOf("0", ":", "="),
            listOf(0.5f, 0.25f, 0.25f),
            callback
        )
    }
}

@Composable
fun Zeile1(t: String) {
    val showHint = t.isEmpty()
    Text(
        text = if (showHint)
            stringResource(id = R.string.input_hint)
        else
            t,
        color = if (showHint)
            MaterialTheme.colors.secondary
        else
            MaterialTheme.colors.onSurface,
        style = MaterialTheme.typography.body1
    )
}

@Composable
fun MyRow(
    texts: List<String>,
    weights: List<Float>,
    callback: (text: String) -> Any
) {
    Row(modifier = Modifier.fillMaxWidth()) {
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
                total = line.toInt()
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
