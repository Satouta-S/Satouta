
package com.setouta.assistant.audio

import android.media.*
import java.util.concurrent.atomic.AtomicBoolean

class AudioRecorder(private val onBytes: (ByteArray) -> Unit) {
    private val sampleRate = 16000
    private val channel = AudioFormat.CHANNEL_IN_MONO
    private val format = AudioFormat.ENCODING_PCM_16BIT
    private val bufSize = AudioRecord.getMinBufferSize(sampleRate, channel, format)
    private val record = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channel, format, bufSize)
    private val running = AtomicBoolean(false)

    fun start() {
        if (running.getAndSet(true)) return
        record.startRecording()
        Thread {
            val buf = ByteArray(3200) // ~100ms
            while (running.get()) {
                val n = record.read(buf, 0, buf.size)
                if (n > 0) onBytes(buf.copyOf(n))
            }
        }.start()
    }

    fun stop() {
        if (!running.getAndSet(false)) return
        try { record.stop() } catch (_: Exception) {}
    }
}
