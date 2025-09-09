
# STT using faster-whisper if available; otherwise fallback to placeholder Arabic.
import os, tempfile, wave, struct
try:
    from faster_whisper import WhisperModel
    _HAS_FW = True
except Exception:
    _HAS_FW = False

_model = None

def _init_model():
    global _model
    if not _HAS_FW:
        return None
    if _model is None:
        name = os.getenv("WHISPER_MODEL", "small")
        device = os.getenv("WHISPER_DEVICE", "cpu")
        compute = os.getenv("WHISPER_COMPUTE", "int8")
        _model = WhisperModel(name, device=device, compute_type=compute)
    return _model

def transcribe_pcm16le(pcm: bytes) -> str:
    if not pcm:
        return "لم أسمعك جيدًا"
    if not _HAS_FW:
        return "هاي ستوته، عامل ايه؟"
    model = _init_model()
    # Write temp wav mono 16k
    fd, path = tempfile.mkstemp(suffix=".wav")
    try:
        wf = wave.open(path, "wb")
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(16000)
        wf.writeframes(pcm)
        wf.close()
        segments, info = model.transcribe(path, language="ar")
        text = "".join([s.text for s in segments]).strip()
        return text or "لم أسمعك جيدًا"
    finally:
        try: os.close(fd)
        except: pass
        try: os.unlink(path)
        except: pass
