package com.navarro.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Affiche l'onde sonore du micro
 */
class WaveformView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var amplitudes: List<Float> = emptyList()

    fun setAmplitudes(data: List<Float>) {
        amplitudes = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val barWidth = w / (amplitudes.size.coerceAtLeast(1))

        amplitudes.forEachIndexed { i, value ->
            val barHeight = value * h
            canvas.drawRect(
                i * barWidth,
                h - barHeight,
                (i + 1) * barWidth,
                h,
                paint
            )
        }
    }
}
