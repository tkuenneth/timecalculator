package com.thomaskuenneth.zeitrechner

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetrics

data class HingeDef(
    val foldOrientation: FoldingFeature.Orientation?,
    val foldWidth: Dp,
    val foldHeight: Dp,
    val widthLeftOrTop: Dp,
    val heightLeftOrTop: Dp,
    val widthRightOrBottom: Dp,
    val heightRightOrBottom: Dp,
)

@Composable
fun createHingeDef(
    layoutInfo: WindowLayoutInfo?,
    windowMetrics: WindowMetrics
): HingeDef {
    var foldOrientation: FoldingFeature.Orientation? = null
    var widthLeftOrTop = 0
    var heightLeftOrTop = 0
    var widthRightOrBottom = 0
    var heightRightOrBottom = 0
    var foldWidth = 0
    var foldHeight = 0
    layoutInfo?.displayFeatures?.forEach { displayFeature ->
        (displayFeature as FoldingFeature).run {
            foldOrientation = orientation
            if (orientation == FoldingFeature.Orientation.VERTICAL) {
                widthLeftOrTop = bounds.left
                heightLeftOrTop = windowMetrics.bounds.height()
                widthRightOrBottom = windowMetrics.bounds.width() - bounds.right
                heightRightOrBottom = heightLeftOrTop
            } else if (orientation == FoldingFeature.Orientation.HORIZONTAL) {
                widthLeftOrTop = windowMetrics.bounds.width()
                heightLeftOrTop = bounds.top
                widthRightOrBottom = windowMetrics.bounds.width()
                heightRightOrBottom = windowMetrics.bounds.height() - bounds.bottom
            }
            foldWidth = bounds.width()
            foldHeight = bounds.height()
        }
    }
    return with(LocalDensity.current) {
        HingeDef(
            foldOrientation = foldOrientation,
            widthLeftOrTop = widthLeftOrTop.toDp(),
            heightLeftOrTop = heightLeftOrTop.toDp(),
            widthRightOrBottom = widthRightOrBottom.toDp(),
            heightRightOrBottom = heightRightOrBottom.toDp(),
            foldWidth = foldWidth.toDp(),
            foldHeight = foldHeight.toDp()
        )
    }
}

@Composable
fun windowWidthDp(windowMetrics: WindowMetrics): Dp = with(LocalDensity.current) {
    windowMetrics.bounds.width().toDp()
}
