
import base64, io, os, subprocess, wave, tempfile

# This wrapper calls Piper CLI to synthesize Arabic to WAV, then strips WAV header to raw PCM16.
# Env vars:
# - PIPER_BIN: path to piper executable (e.g., /usr/local/bin/piper)
# - PIPER_VOICE: path to voice model (e.g., ar_XM-... .onnx + .json)
# Example voice models: https://github.com/rhasspy/piper#voices

def speak_pcm16(text: str):
    bin_path = os.getenv("PIPER_BIN")
    voice = os.getenv("PIPER_VOICE")
    if not bin_path or not voice:
        # Fallback: return empty audio
        return {"pcm_b64": base64.b64encode(b"").decode("ascii"), "sample_rate": 16000}

    with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as out_wav:
        cmd = [bin_path, "-m", voice, "-f", out_wav.name, "-t", text]
        subprocess.run(cmd, check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

    # Read WAV, extract PCM16 frames & sample rate
    with wave.open(out_wav.name, "rb") as wf:
        sr = wf.getframerate()
        n = wf.getnframes()
        pcm = wf.readframes(n)

    try:
        os.unlink(out_wav.name)
    except Exception:
        pass

    return {"pcm_b64": base64.b64encode(pcm).decode("ascii"), "sample_rate": sr}
