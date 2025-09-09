
import asyncio, json, base64, os
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from fastapi.responses import JSONResponse
from . import nlu, tts, memory, tools, stt, tts_piper

app = FastAPI(title="Setouta Server", version="0.1.0")

@app.get("/health")
async def health():
    return JSONResponse({"status": "ok", "app":"setouta"})

@app.websocket("/ws")
async def ws_conn(ws: WebSocket):
    await ws.accept()
    await ws.send_text(json.dumps({"type":"hello","msg":"connected"}))
    user_id = "mahmoud"  # later: pass token/header
    convo = memory.load(user_id)
    audio_buf = bytearray()

    try:
        while True:
            msg = await ws.receive()
            if "bytes" in msg and msg["bytes"]:
                audio_buf.extend(msg["bytes"])
                # TODO: stream to real STT; here we just send a placeholder
                await ws.send_text(json.dumps({"type":"stt_partial","text":"..."}))
            elif "text" in msg and msg["text"]:
                try:
                    data = json.loads(msg["text"])
                except Exception:
                    data = {"type":"raw", "text": msg["text"]}

                if data.get("type") == "eos":
                    # End of utterance: run STT (placeholder -> fake Arabic demo)
                    transcript = stt.transcribe_pcm16le(audio_buf)  # returns str
                    await ws.send_text(json.dumps({"type":"stt_final","text": transcript}))

                    # NLU / Planner
                    intent = nlu.detect_intent(transcript)
                    plan = nlu.plan(intent, transcript, convo)

                    # Tool proposal (only for sensitive tools)
                    if plan.get("tool_proposal"):
                        await ws.send_text(json.dumps({
                            "type":"tool_proposal",
                            "tool": plan["tool_proposal"]["tool"],
                            "args": plan["tool_proposal"]["args"],
                            "summary": plan["tool_proposal"]["summary"]
                        }))

                    # Execute small‑talk or safe plan
                    reply = await tools.execute_safe(plan, transcript, convo)

                    # Speak
                    audio_b64 = tts.speak(reply)
                    await ws.send_text(json.dumps({"type":"speak","audio": audio_b64}))

                    # Memory
                    convo = memory.update(user_id, transcript, reply, intent)

                    audio_buf = bytearray()

                elif data.get("type") == "confirm" and data.get("ok") is True:
                    # Example: proceed with proposed tool
                    result = await tools.execute_confirmed()
                    reply = f"تم التنفيذ: {result}"
                    audio_b64 = tts.speak(reply)
                    await ws.send_text(json.dumps({"type":"speak","audio": audio_b64}))
                else:
                    await ws.send_text(json.dumps({"type":"info","echo": data}))

    except WebSocketDisconnect:
        return
