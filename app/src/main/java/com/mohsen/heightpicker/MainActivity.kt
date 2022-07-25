package com.mohsen.heightpicker

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PickerScreen(pickerStyle = PickerStyle(modifier = Modifier))
        }
    }
}

@Composable
fun PickerScreen(pickerStyle: PickerStyle) {


    var dragStartTransition by remember {
        mutableStateOf(Offset(0f,0f))
    }

    var heightNum by remember {
        mutableStateOf(pickerStyle.spaceInterval)
    }

    BoxWithConstraints(pickerStyle.modifier.fillMaxSize()) {

        Canvas(modifier = pickerStyle.modifier
            .pointerInput(true) {
                detectDragGestures(onDragStart = {


                }, onDragEnd = {


                }) { change, dragAmount ->

                    heightNum*2
                }
            }
        ) {
            drawContext.canvas.nativeCanvas.apply {
                val pickerLinesPath = Path().apply {
                    moveTo(0f, (constraints.maxHeight / 2) - pickerStyle.pickerWidth.toPx() / 2)
                    lineTo(
                        constraints.maxWidth.toFloat(),
                        (constraints.maxHeight / 2) - pickerStyle.pickerWidth.toPx() / 2
                    )
                    moveTo(0f, (constraints.maxHeight / 2) + pickerStyle.pickerWidth.toPx() / 2)
                    lineTo(
                        constraints.maxWidth.toFloat(),
                        (constraints.maxHeight / 2) + pickerStyle.pickerWidth.toPx() / 2
                    )
                }


                drawPath(pickerLinesPath, Paint().apply {
                    this.style = Paint.Style.FILL_AND_STROKE
                    this.strokeWidth = 6f
                    this.color = Color.BLACK
                    this.setShadowLayer(86f, 0f, 0f, Color.GRAY)
                })


                val indicator = Path().apply {
                    moveTo((constraints.maxWidth / 2).toFloat(), (constraints.maxHeight / 2 + 10f))
                    lineTo(
                        (constraints.maxWidth / 2 - 2f),
                        (constraints.maxHeight / 2) + pickerStyle.pickerWidth.toPx() / 2
                    )
                    moveTo((constraints.maxWidth / 2).toFloat(), (constraints.maxHeight / 2 + 10f))
                    lineTo(
                        (constraints.maxWidth / 2 + 2f),
                        (constraints.maxHeight / 2) + pickerStyle.pickerWidth.toPx() / 2
                    )
                }

                drawPath(indicator, Paint().apply {
                    this.color = Color.RED
                    this.style = Paint.Style.STROKE
                    this.strokeWidth = 8f
                    this.isAntiAlias = true
                })

                for (height in 1..pickerStyle.maxHeight - pickerStyle.minHeight) {
                    val lineType = when {
                        height % 10 == 0 -> DegreeLineType.TenTypeLine
                        height % 5 == 0 -> DegreeLineType.FiveTypeLine
                        else -> DegreeLineType.NormalTypeLine
                    }

                    val lineColor = when (lineType) {
                        DegreeLineType.TenTypeLine -> pickerStyle.tenTypeLineColor
                        DegreeLineType.FiveTypeLine -> pickerStyle.fiveTypeLineColor
                        else -> pickerStyle.normalTypeLineColor
                    }

                    val lineHeightSize = when (lineType) {
                        DegreeLineType.TenTypeLine -> pickerStyle.tenTypeLineHeight
                        DegreeLineType.FiveTypeLine -> pickerStyle.fiveTypeLineHeight
                        else -> pickerStyle.normalTypeLineHeight
                    }

                    // draw first line
                    if (height == 0) {
                        this.drawLine(pickerStyle.spaceInterval * height.toFloat(),
                            (constraints.maxHeight / 2) - pickerStyle.pickerWidth.toPx() / 2 + 4,
                            pickerStyle.spaceInterval * height.toFloat(),
                            (constraints.maxHeight / 2) - pickerStyle.pickerWidth.toPx() / 2 + pickerStyle.tenTypeLineHeight * 2,
                            Paint().apply {
                                this.style = Paint.Style.STROKE
                                this.strokeWidth = 4f
                                this.color = pickerStyle.tenTypeLineColor
                                this.isAntiAlias = true
                            }
                        )
                    }

                    this.drawLine(pickerStyle.spaceInterval * height.toFloat(),
                        (constraints.maxHeight / 2) - pickerStyle.pickerWidth.toPx() / 2 + 4,
                        pickerStyle.spaceInterval * height.toFloat(),
                        (constraints.maxHeight / 2) - pickerStyle.pickerWidth.toPx() / 2 + lineHeightSize * 2,
                        Paint().apply {
                            this.style = Paint.Style.STROKE
                            this.strokeWidth = 4f
                            this.color = lineColor
                            this.isAntiAlias = true
                        }
                    )


                    if (lineType == DegreeLineType.TenTypeLine) {
                        val textBound = Rect()
                        Paint().getTextBounds(
                            abs(height).toString(),
                            0,
                            height.toString().length,
                            textBound
                        )
                        drawText(
                            abs(height).toString(),
                            pickerStyle.spaceInterval * height.toFloat() - textBound.width() / 2,
                            (constraints.maxHeight / 2) - pickerStyle.pickerWidth.toPx() / 2 + lineHeightSize * 2 + textBound.height() * 2 + pickerStyle.numberPadding,
                            Paint().apply {
                                this.textSize = 20.sp.toPx()
                                this.textAlign = Paint.Align.CENTER
                                this.color = Color.BLACK
                                this.style = Paint.Style.FILL
                                this.isAntiAlias = true
                            }
                        )
                    }

                }
            }
        }
    }

}


