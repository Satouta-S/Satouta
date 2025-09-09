
package com.setouta.assistant

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.text.HtmlCompat

class ConsentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consent)

        val title = findViewById<TextView>(R.id.consentTitle)
        val desc = findViewById<TextView>(R.id.consentDesc)
        val mic = findViewById<CheckBox>(R.id.cbMic)
        val analytics = findViewById<CheckBox>(R.id.cbAnalytics)
        val overlay = findViewById<CheckBox>(R.id.cbOverlay)
        val accessibility = findViewById<CheckBox>(R.id.cbAccessibility)
        val accept = findViewById<Button>(R.id.btnAccept)
        val decline = findViewById<Button>(R.id.btnDecline)

        title.text = getString(R.string.consent_title)
        desc.text = HtmlCompat.fromHtml(getString(R.string.consent_text_html), HtmlCompat.FROM_HTML_MODE_LEGACY)

        accept.setOnClickListener {
            val prefs = getSharedPreferences("setouta.consent", MODE_PRIVATE).edit()
            prefs.putBoolean("mic", mic.isChecked)
            prefs.putBoolean("analytics", analytics.isChecked)
            prefs.putBoolean("overlay", overlay.isChecked)
            prefs.putBoolean("accessibility", accessibility.isChecked)
            prefs.apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        decline.setOnClickListener {
            finish()
        }
    }
}
