package com.thomaskuenneth.zeitrechner

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconButtonWithTooltip(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip { Text(text = contentDescription) }
        }, state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription
            )
        }
    }
}
