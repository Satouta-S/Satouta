
package com.setouta.assistant.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.view.WindowManager
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class SetoutaAccessibilityService : AccessibilityService() {

        companion object {
            const val ACTION_TAP = "com.setouta.TAP"
            const val ACTION_SWIPE = "com.setouta.SWIPE"
        }

        private var receiver: BroadcastReceiver? = null


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We don't need to handle stream of events for MVP
    }

    override fun onInterrupt() {
        // No-op
    }

    override fun onServiceConnected() {
            // Register receiver for gesture commands
            val filter = IntentFilter().apply {
                addAction(ACTION_TAP)
                addAction(ACTION_SWIPE)
            }
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (intent?.action) {
                        ACTION_TAP -> {
                            val x = intent.getFloatExtra("x", -1f)
                            val y = intent.getFloatExtra("y", -1f)
                            if (x >= 0 && y >= 0) {
                                Overlay.showTap(this@SetoutaAccessibilityService, x, y)
                                tap(x, y)
                            }
                        }
                        ACTION_SWIPE -> {
                            val x1 = intent.getFloatExtra("x1", -1f)
                            val y1 = intent.getFloatExtra("y1", -1f)
                            val x2 = intent.getFloatExtra("x2", -1f)
                            val y2 = intent.getFloatExtra("y2", -1f)
                            if (x1>=0 && y1>=0 && x2>=0 && y2>=0) {
                                Overlay.showTap(this@SetoutaAccessibilityService, x1, y1)
                                swipe(x1, y1, x2, y2)
                            }
                        }
                    }
                }
            }
            registerReceiver(receiver, filter)

        super.onServiceConnected()
    }

    fun tap(x: Float, y: Float, durationMs: Long = 50L, callback: (() -> Unit)? = null) {
        // Dispatch a single tap gesture at (x,y)
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                callback?.let { it() }
            }
        }, null)
    }
}


fun swipe(x1: Float, y1: Float, x2: Float, y2: Float, durationMs: Long = 250L, callback: (() -> Unit)? = null) {
    val path = android.graphics.Path().apply { moveTo(x1, y1); lineTo(x2, y2) }
    val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
    val gesture = GestureDescription.Builder().addStroke(stroke).build()
    dispatchGesture(gesture, object : GestureResultCallback() {
        override fun onCompleted(gestureDescription: GestureDescription?) { callback?.let { it() } }
    }, null)
}
