
package com.setouta.assistant

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity

class PrivacyPolicyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)
        val wv = findViewById<WebView>(R.id.policyWebView)
        wv.webViewClient = WebViewClient()
        // Load from local assets; you can later change to a hosted URL.
        wv.loadUrl("file:///android_asset/policy.html")
    }
}
