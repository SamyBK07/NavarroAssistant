package com.navarro.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class HologramView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Dessine un cercle (exemple simple)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (Math.min(width, height) / 2.5).toFloat()
        canvas.drawCircle(centerX, centerY, radius, paint)
    }
}
