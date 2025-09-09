
package com.setouta.assistant.util

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout

object Overlay {
    private var view: View? = null
    fun showTap(ctx: Context, x: Float, y: Float) {
        try {
            val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val size = 32
            val v = View(ctx).apply { setBackgroundColor(Color.argb(160, 0, 150, 255)) }
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE
            val params = WindowManager.LayoutParams(
                size, size, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = x.toInt() - size/2
                y = y.toInt() - size/2
            }
            // remove previous
            view?.let { wm.removeViewImmediate(it) }
            view = v
            wm.addView(v, params)
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    wm.removeView(v)
                } catch (_: Exception) {}
                if (view === v) view = null
            }, 600)
        } catch (_: Exception) {}
    }
}
