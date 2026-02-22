package com.navarro.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Animation centrale type hologramme
 */
class HologramView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        // Exemple: trois points lumineux animés (placeholder)
        canvas.drawCircle(w * 0.3f, h / 2, 20f, paint)
        canvas.drawCircle(w * 0.5f, h / 2, 20f, paint)
        canvas.drawCircle(w * 0.7f, h / 2, 20f, paint)
    }
}
