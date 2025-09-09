
package com.setouta.assistant.ws

import okhttp3.*
import okio.ByteString

class WsClient(
    private val url: String,
    private val onText: (String) -> Unit
) : WebSocketListener() {

    private var ws: WebSocket? = null
    private val client = OkHttpClient()

    fun connect() {
        if (ws != null) return
        val req = Request.Builder().url(url).build()
        ws = client.newWebSocket(req, this)
    }

    fun sendBinary(bytes: ByteArray) {
        ws?.send(ByteString.of(*bytes))
    }

    fun sendText(text: String) {
        ws?.send(text)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        onText("{"type":"status","msg":"connected"}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        onText(text)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        onText("{"type":"error","msg":"${t.message}"}")
        ws = null
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        onText("{"type":"closed","code":$code}")
        ws = null
    }
}
