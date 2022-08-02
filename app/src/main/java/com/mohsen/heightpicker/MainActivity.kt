package com.mohsen.heightpicker

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color as ComposeColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var targetHeight by remember {
                mutableStateOf(0)
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                SelectHeightScreen(targetHeight)

                Spacer(modifier = Modifier.height(68.dp))

                PickerScreen(pickerStyle = PickerStyle()) { height ->
                    targetHeight = height
                }

            }
        }
    }
}

@Composable
fun SelectHeightScreen(targetHeight: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "Select Your Height :",
            color = ComposeColor.Black,
            textAlign = TextAlign.Center,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(36.dp))

        Row {
            Text(
                text = targetHeight.toString(),
                color = ComposeColor.Black,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier.align(Alignment.Bottom),
                text = "cm",
                textAlign = TextAlign.Center,
                color = ComposeColor.Red,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
fun PickerScreen(pickerStyle: PickerStyle, onHeightChange: (Int) -> Unit = {}) {

    var targetDistant by remember {
        mutableStateOf(0f)
    }

    var startDragPoint by remember {
        mutableStateOf(0f)
    }

    var oldDragPoint by remember {
        mutableStateOf(0f)
    }

    var selectedHeight by remember {
        mutableStateOf(0)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .align(Alignment.TopCenter)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        startDragPoint = it.x
                    },
                    onDragEnd = {
                        oldDragPoint = targetDistant
                    }
                ) { change, _ ->
                    val newDistance = oldDragPoint + (change.position.x - startDragPoint)
                    targetDistant = newDistance.coerceIn(
                        minimumValue = ((pickerStyle.initialHeight) * pickerStyle.spaceInterval - pickerStyle.maxHeight * pickerStyle.spaceInterval).toFloat(),
                        maximumValue = ((pickerStyle.initialHeight) * pickerStyle.spaceInterval - pickerStyle.minHeight * pickerStyle.spaceInterval).toFloat()
                    )
                }
            }
        ) {

            val middlePoint = Offset(x = maxWidth.toPx() / 2f, y = maxHeight.toPx() / 2f)

            drawContext.canvas.nativeCanvas.apply {
                val pickerLinesPath = Path().apply {
                    moveTo(0f, middlePoint.y - pickerStyle.pickerWidth.toPx() / 2)
                    lineTo(constraints.maxWidth.toFloat(), middlePoint.y - pickerStyle.pickerWidth.toPx() / 2)
                    moveTo(0f, middlePoint.y + pickerStyle.pickerWidth.toPx() / 2)
                    lineTo(constraints.maxWidth.toFloat(), middlePoint.y + pickerStyle.pickerWidth.toPx() / 2)
                }


                drawPath(pickerLinesPath, Paint().apply {
                    this.style = Paint.Style.STROKE
                    this.strokeWidth = pickerStyle.lineStroke
                    this.color = Color.BLACK
                    this.setShadowLayer(86f, 0f, 0f, Color.LTGRAY)
                })


                val indicator = Path().apply {
                    moveTo(middlePoint.x, (middlePoint.y + 10f))
                    lineTo((middlePoint.x - 2f), middlePoint.y + pickerStyle.pickerWidth.toPx() / 2)
                    moveTo(middlePoint.x, (middlePoint.y + 10f))
                    lineTo((middlePoint.x + 2f), middlePoint.y + pickerStyle.pickerWidth.toPx() / 2)
                    fillType = Path.FillType.EVEN_ODD
                }


                drawPath(indicator, Paint().apply {
                    this.color = Color.RED
                    this.style = Paint.Style.FILL_AND_STROKE
                    this.strokeWidth = pickerStyle.lineStroke
                    this.isAntiAlias = true
                })


                for (height in pickerStyle.minHeight..pickerStyle.maxHeight) {
                    val degreeLineScaleX =
                        middlePoint.x + (pickerStyle.spaceInterval * (height - pickerStyle.initialHeight.toFloat()) + targetDistant)
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

                    this.drawLine(degreeLineScaleX,
                        middlePoint.y - pickerStyle.pickerWidth.toPx() / 2 + 4,
                        degreeLineScaleX,
                        middlePoint.y - pickerStyle.pickerWidth.toPx() / 2 + lineHeightSize * 2,
                        Paint().apply {
                            this.style = Paint.Style.STROKE
                            this.strokeWidth = pickerStyle.lineStroke
                            this.color = lineColor
                            this.isAntiAlias = true
                        }
                    )


                    if (abs(middlePoint.x - degreeLineScaleX.roundToInt()) < 5) {
                        selectedHeight = height
                        onHeightChange(selectedHeight)
                    }

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
                            (degreeLineScaleX) - textBound.width() / 2,
                            middlePoint.y - pickerStyle.pickerWidth.toPx() / 2 + lineHeightSize * 2 + textBound.height() * 2 + pickerStyle.numberPadding,
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
