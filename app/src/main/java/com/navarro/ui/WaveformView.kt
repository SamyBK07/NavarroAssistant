package com.navarro.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 4f
    }
    private var amplitudes = FloatArray(0)

    fun updateAmplitudes(newAmplitudes: FloatArray) {
        amplitudes = newAmplitudes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (amplitudes.isEmpty()) return

        val widthPerSample = width.toFloat() / amplitudes.size
        var x = 0f
        for (amp in amplitudes) {
            val height = amp * height
            canvas.drawLine(x, height / 2f - height, x, height / 2f + height, paint)
            x += widthPerSample
        }
    }
}
