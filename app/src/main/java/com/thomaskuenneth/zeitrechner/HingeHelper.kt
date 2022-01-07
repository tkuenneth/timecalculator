package com.thomaskuenneth.zeitrechner

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetrics

data class HingeDef(
    val hasGap: Boolean,
    val sizeLeft: Dp,
    val sizeRight: Dp,
    val widthGap: Dp
)

@Composable
fun createHingeDef(
    layoutInfo: WindowLayoutInfo?,
    windowMetrics: WindowMetrics
): HingeDef {
    var hasGap = false
    var sizeLeft = 0
    var sizeRight = 0
    var widthGap = 0
    layoutInfo?.displayFeatures?.forEach { displayFeature ->
        (displayFeature as FoldingFeature).run {
            hasGap = occlusionType == FoldingFeature.OcclusionType.FULL
                    && orientation == FoldingFeature.Orientation.VERTICAL
            sizeLeft = bounds.left
            sizeRight = windowMetrics.bounds.width() - bounds.right
            widthGap = bounds.width()
        }
    }
    return with(LocalDensity.current) {
        HingeDef(
            hasGap,
            sizeLeft.toDp(),
            sizeRight.toDp(),
            widthGap.toDp()
        )
    }
}

@Composable
fun windowWidthDp(windowMetrics: WindowMetrics): Dp = with(LocalDensity.current) {
    windowMetrics.bounds.width().toDp()
}