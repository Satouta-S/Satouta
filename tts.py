
import base64
# Placeholder TTS: returns empty audio (silence).
# Swap with Piper or Azure: generate MP3/PCM bytes, then base64.b64encode.
def speak(text: str) -> str:
    fake_mp3 = b""
    return base64.b64encode(fake_mp3).decode("ascii")
