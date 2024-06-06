package com.thomaskuenneth.zeitrechner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import eu.thomaskuenneth.adaptivescaffold.defaultColorScheme
import eu.thomaskuenneth.adaptivescaffold.setContentRepeatOnLifecycleStarted

@ExperimentalMaterial3Api
class TimeCalculatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentRepeatOnLifecycleStarted {
            MaterialTheme(
                content = { TimeCalculatorScreen() },
                colorScheme = defaultColorScheme()
            )
        }
    }
}
