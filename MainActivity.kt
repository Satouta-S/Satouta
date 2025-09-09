
package com.setouta.assistant

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.setouta.assistant.audio.AudioRecorder
import com.setouta.assistant.ws.WsClient
import org.json.JSONObject
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var statusText: TextView
    private lateinit var startBtn: Button
        private lateinit var privacyBtn: Button
        private lateinit var accBtn: Button
        private lateinit var policyBtn: Button
    private lateinit var stopBtn: Button
    private var recorder: AudioRecorder? = null
    private var ws: WsClient? = null
    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        startBtn = findViewById(R.id.startBtn)
        stopBtn = findViewById(R.id.stopBtn)

        ensurePermissions()

        ws = WsClient("ws://10.0.2.2:8000/ws", onText = { txt ->
            runOnUiThread {
                statusText.text = txt
                try {
                    val obj = JSONObject(txt)
                    when (obj.optString("type")) {
                        "stt_partial" -> { /* already in text */ }
                        "stt_final" -> { /* show final */ }
                        "speak" -> {
                            val b64 = obj.optString("audio")
                            if (b64.isNotEmpty()) playBase64Mp3(b64)
                        }
                    }
                } catch (_: Exception) {}
            }
        })

        startBtn.setOnClickListener {
            if (recorder == null) recorder = AudioRecorder { bytes -> ws?.sendBinary(bytes) }
            ws?.connect()
            recorder?.start()
        }
        privacyBtn.setOnClickListener {
                startActivity(android.content.Intent(this, PrivacyCenterActivity::class.java))
            }

            policyBtn.setOnClickListener {
                startActivity(android.content.Intent(this, PrivacyPolicyActivity::class.java))
            }

            accBtn.setOnClickListener {
                com.setouta.assistant.util.AccessibilityHelpers.openAccessibilitySettings(this)
            }

            stopBtn.setOnClickListener {
            recorder?.stop()
            ws?.sendText("{"type":"eos"}")
        }
    }

    private fun playBase64Mp3(b64: String) {
        try {
            val data = Base64.decode(b64, Base64.DEFAULT)
            val tmp = File.createTempFile("setouta_", ".mp3", cacheDir)
            tmp.writeBytes(data)
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(tmp.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            // ignore if silence
        }
    }

    private fun routeCommand(text: String) {
            val store = com.setouta.assistant.privacy.SkillStore(this)
            com.setouta.assistant.util.ArabicCommandRouter.handle(text, this, store) { prompt, action ->
                // confirmation dialog
                android.app.AlertDialog.Builder(this)
                    .setTitle("تأكيد")
                    .setMessage(prompt)
                    .setPositiveButton("نعم") { _, _ -> action() }
                    .setNegativeButton("لا", null)
                    .show()
            }
        }

        private fun playPcm16(b64: String, sampleRate: Int) {
            try {
                val bytes = android.util.Base64.decode(b64, android.util.Base64.DEFAULT)
                val minBuf = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
                val track = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBuf,
                    AudioTrack.MODE_STREAM
                )
                track.play()
                track.write(bytes, 0, bytes.size)
                track.stop()
                track.release()
            } catch (e: Exception) { /* ignore */ }
        }

        private fun ensurePermissions() {
        val toRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            toRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            toRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (toRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, toRequest.toTypedArray(), 101)
        }
    }
}
