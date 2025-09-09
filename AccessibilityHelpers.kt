
package com.setouta.assistant.util

import android.content.Context
import android.content.Intent
import android.provider.Settings

object AccessibilityHelpers {
    fun openAccessibilitySettings(ctx: Context) {
        val i = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        ctx.startActivity(i)
    }
}
