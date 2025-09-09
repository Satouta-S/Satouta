
package com.setouta.assistant.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.setouta.assistant.privacy.Policy
import com.setouta.assistant.privacy.SkillStore
import com.setouta.assistant.privacy.SkillsCatalog
import android.content.Intent

object ArabicCommandRouter {

        private fun sendTap(ctx: Context, x: Float, y: Float) {
            val i = Intent("com.setouta.TAP").apply { putExtra("x", x); putExtra("y", y) }
            ctx.sendBroadcast(i)
        }
        private fun sendSwipe(ctx: Context, x1: Float, y1: Float, x2: Float, y2: Float) {
            val i = Intent("com.setouta.SWIPE").apply { putExtra("x1", x1); putExtra("y1", y1); putExtra("x2", x2); putExtra("y2", y2) }
            ctx.sendBroadcast(i)
        }


    data class Result(val handled: Boolean, val message: String = "")

    fun handle(text: String, ctx: Context, store: SkillStore, confirm: (String, () -> Unit) -> Unit): Result {
        val t = text.lowercase().trim()

        // Maps commands
        if (t.startsWith("افتح جوجل ماب") || t.startsWith("افتح خرائط جوجل") || t.startsWith("افتح الخرائط")) {
            val policy = store.getPolicy(SkillsCatalog.MAPS_NAV)
            val action = { openMaps(ctx) }
            return gated(policy, "فتح خرائط جوجل؟", action)
        }

        // Navigate to place: "وصّلني إلى ..." / "ودّيني ..." / "روّحني ..."
        if (t.startsWith("وصلني") || t.startsWith("وصّلني") || t.startsWith("وديني") || t.startsWith("ودّيني") or t.startsWith("روحني") or t.startsWith("روّحني")) {
            val place = t.replace("وصلني", "").replace("وصّلني", "").replace("وديني", "").replace("ودّيني", "").replace("روحني", "").replace("روّحني", "").replace("الى","").replace("إلى","").trim()
            val policy = store.getPolicy(SkillsCatalog.MAPS_NAV)
            val action = { navigateTo(ctx, place) }
            return gated(policy, "ملاحة إلى: $place ؟", action)
        }

        // Open app by name: "افتح واتساب", "افتح يوتيوب"
        if (t.startsWith("افتح ")) {
            val appName = t.removePrefix("افتح").trim()
            val policy = store.getPolicy(SkillsCatalog.OPEN_APP)
            val action = { openAppByName(ctx, appName) }
            return gated(policy, "فتح تطبيق: $appName ؟", action)
        }

        return Result(false)
    }

    private fun gated(policy: Policy, prompt: String, action: () -> Unit): Result {
        return when (policy) {
            Policy.ALLOW -> { action(); Result(true, "تم") }
            Policy.DENY -> Result(true, "مرفوض حسب الإعدادات")
            Policy.ASK -> Result(true, prompt) // على الكلاينت يظهر تأكيد
        }
    }

    private fun openMaps(ctx: Context) {
        val pm = ctx.packageManager
        val intent = pm.getLaunchIntentForPackage("com.google.android.apps.maps")
        if (intent != null) {
            ctx.startActivity(intent)
        } else {
            val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com"))
            ctx.startActivity(web)
        }
    }

    private fun navigateTo(ctx: Context, place: String) {
        val uri = Uri.parse("google.navigation:q=" + Uri.encode(place))
        val i = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
        ctx.startActivity(i)
    }

    private fun openAppByName(ctx: Context, appName: String) {
        val pm = ctx.packageManager
        // Very simple heuristic: find first package whose label contains appName
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val target = apps.firstOrNull { pm.getApplicationLabel(it).toString().lowercase().contains(appName) }
        val intent = target?.let { pm.getLaunchIntentForPackage(it.packageName) }
        if (intent != null) ctx.startActivity(intent)
    }
}
