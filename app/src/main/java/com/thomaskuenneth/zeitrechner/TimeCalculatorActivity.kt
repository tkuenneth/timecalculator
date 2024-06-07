package com.thomaskuenneth.zeitrechner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import eu.thomaskuenneth.adaptivescaffold.defaultColorScheme
import eu.thomaskuenneth.adaptivescaffold.setContentRepeatOnLifecycleStarted

@ExperimentalMaterial3Api
class TimeCalculatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentRepeatOnLifecycleStarted(enableEdgeToEdge = true) {
            MaterialTheme(
                content = { TimeCalculatorScreen() },
                colorScheme = defaultColorScheme()
            )
        }
    }
}
