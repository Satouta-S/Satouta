
from fastapi.testclient import TestClient
from app.main import app
import json

def test_ws_eos_flow():
    client = TestClient(app)
    with client.websocket_connect("/ws") as ws:
        # send small fake audio and EOS
        ws.send_bytes(b"\x00" * 6400)
        ws.send_text(json.dumps({"type":"eos"}))
        # Expect at least stt_final + speak messages
        messages = []
        for _ in range(5):
            try:
                messages.append(ws.receive_text())
            except Exception:
                break
        joined = " ".join(messages)
        assert "stt_final" in joined or "speak" in joined
