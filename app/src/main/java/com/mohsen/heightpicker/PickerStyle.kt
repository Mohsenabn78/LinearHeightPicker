package com.mohsen.heightpicker

import android.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class PickerStyle(
    var modifier: Modifier = Modifier,
    var pickerWidth: Dp = 150.dp,
    var minHeight:Int =200,
    var maxHeight:Int =300,
    var initialHeight:Int =250,
    var normalTypeLineColor:Int=Color.LTGRAY,
    var tenTypeLineColor:Int=Color.BLACK,
    var fiveTypeLineColor:Int=Color.RED,
    var normalTypeLineHeight:Int=28,
    var tenTypeLineHeight:Int=50,
    var fiveTypeLineHeight:Int=38,
    var spaceInterval:Int=36,
    var numberPadding:Int=28,
    var lineStroke:Float=6f
)
