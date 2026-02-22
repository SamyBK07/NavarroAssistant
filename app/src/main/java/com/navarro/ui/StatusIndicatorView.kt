package com.navarro.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Indicateurs batterie / micro / réseau
 */
class StatusIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    var batterie: Int = 100
    var microActif: Boolean = true
    var reseauLocal: Boolean = true

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        // Batterie
        paint.color = Color.CYAN
        canvas.drawRect(w - 60f, 10f, w - 10f, 10f + batterie * 0.5f, paint)

        // Micro
        paint.color = if (microActif) Color.GREEN else Color.RED
        canvas.drawCircle(20f, 20f, 10f, paint)

        // Réseau
        paint.color = if (reseauLocal) Color.GREEN else Color.RED
        canvas.drawCircle(50f, 20f, 10f, paint)
    }
}
