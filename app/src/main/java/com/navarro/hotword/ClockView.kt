// ClockView.kt
package com.navarro.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*

class ClockView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val clockNumbers = listOf("12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) * 0.8f

        // Fond transparent
        paint.color = Color.TRANSPARENT
        canvas.drawCircle(centerX, centerY, radius, paint)

        // Chiffres fluorescents
        paint.color = Color.GREEN
        paint.textSize = 40f
        paint.textAlign = Paint.Align.CENTER
        for (i in 0 until 12) {
            val angle = Math.toRadians(i * 30.0)
            val x = centerX + radius * 0.7f * sin(angle).toFloat()
            val y = centerY - radius * 0.7f * cos(angle).toFloat()
            canvas.drawText(clockNumbers[i], x, y, paint)
        }

        // Aiguilles
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Aiguille des heures
        paint.strokeWidth = 8f
        val hourAngle = Math.toRadians((hour * 30) + (minute * 0.5) - 90)
        canvas.drawLine(
            centerX, centerY,
            centerX + radius * 0.4f * sin(hourAngle).toFloat(),
            centerY - radius * 0.4f * cos(hourAngle).toFloat(),
            paint
        )

        // Aiguille des minutes
        paint.strokeWidth = 5f
        val minuteAngle = Math.toRadians(minute * 6 - 90)
        canvas.drawLine(
            centerX, centerY,
            centerX + radius * 0.6f * sin(minuteAngle).toFloat(),
            centerY - radius * 0.6f * cos(minuteAngle).toFloat(),
            paint
        )

        // Aiguille des secondes
        paint.strokeWidth = 3f
        paint.color = Color.RED
        val secondAngle = Math.toRadians(second * 6 - 90)
        canvas.drawLine(
            centerX, centerY,
            centerX + radius * 0.7f * sin(secondAngle).toFloat(),
            centerY - radius * 0.7f * cos(secondAngle).toFloat(),
            paint
        )

        postInvalidateDelayed(1000) // Rafraîchit chaque seconde
    }
}
