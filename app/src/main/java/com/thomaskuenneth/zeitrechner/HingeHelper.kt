package com.thomaskuenneth.zeitrechner

import android.graphics.Rect
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetrics

data class HingeDef(
    val hasVerticalGap: Boolean,
    val hasHorizontalGap: Boolean,
    val widthLeft: Dp,
    val widthRight: Dp,
    val boundsGap: Rect
)

@Composable
fun createHingeDef(
    layoutInfo: WindowLayoutInfo?,
    windowMetrics: WindowMetrics
): HingeDef {
    var hasVerticalGap = false
    var hasHorizontalGap = false
    var widthLeft = 0
    var widthRight = 0
    var boundsGap = Rect(0, 0, 0, 0)
    layoutInfo?.displayFeatures?.forEach { displayFeature ->
        (displayFeature as FoldingFeature).run {
            hasVerticalGap = occlusionType == FoldingFeature.OcclusionType.FULL
                    && orientation == FoldingFeature.Orientation.VERTICAL
            hasHorizontalGap = occlusionType == FoldingFeature.OcclusionType.FULL
                    && orientation == FoldingFeature.Orientation.HORIZONTAL
            widthLeft = bounds.left
            widthRight = windowMetrics.bounds.width() - bounds.right
            boundsGap = bounds
        }
    }
    return with(LocalDensity.current) {
        HingeDef(
            hasVerticalGap,
            hasHorizontalGap,
            widthLeft.toDp(),
            widthRight.toDp(),
            boundsGap
        )
    }
}

@Composable
fun windowWidthDp(windowMetrics: WindowMetrics): Dp = with(LocalDensity.current) {
    windowMetrics.bounds.width().toDp()
}
