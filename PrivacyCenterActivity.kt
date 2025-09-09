
package com.setouta.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.setouta.assistant.privacy.SkillStore
import com.setouta.assistant.privacy.Skill
import com.setouta.assistant.privacy.Policy

class PrivacyCenterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

            val prefs = getSharedPreferences("setouta.consent", MODE_PRIVATE)
            val mic = findViewById<android.widget.CheckBox>(R.id.cbMicConsent)
            val analytics = findViewById<android.widget.CheckBox>(R.id.cbAnalyticsConsent)
            val overlay = findViewById<android.widget.CheckBox>(R.id.cbOverlayConsent)
            val accessibility = findViewById<android.widget.CheckBox>(R.id.cbAccessibilityConsent)

            fun reload() {
                mic.isChecked = prefs.getBoolean("mic", true)
                analytics.isChecked = prefs.getBoolean("analytics", false)
                overlay.isChecked = prefs.getBoolean("overlay", false)
                accessibility.isChecked = prefs.getBoolean("accessibility", false)
            }
            reload()

            val editor = prefs.edit()
            mic.setOnCheckedChangeListener { _, v -> editor.putBoolean("mic", v).apply() }
            analytics.setOnCheckedChangeListener { _, v -> editor.putBoolean("analytics", v).apply() }
            overlay.setOnCheckedChangeListener { _, v -> editor.putBoolean("overlay", v).apply() }
            accessibility.setOnCheckedChangeListener { _, v -> editor.putBoolean("accessibility", v).apply() }

        val store = SkillStore(this)
        val list = store.load()
        val rv = findViewById<RecyclerView>(R.id.skillsRecycler)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = SkillsAdapter(list, onChange = { id, policy ->
            store.setPolicy(id, policy)
        })
    }
}
