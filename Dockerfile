
# syntax=docker/dockerfile:1.6
FROM python:3.11-slim

# System deps
RUN apt-get update && apt-get install -y --no-install-recommends         ffmpeg curl ca-certificates tini         && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Python deps
COPY server-fastapi/requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt         && pip install --no-cache-dir uvicorn[standard]==0.30.0

# App
COPY server-fastapi/app /app/app

# Piper/Whisper envs are optional; if provided, server will use them.
ENV PIPER_BIN=""
ENV PIPER_VOICE=""
ENV WHISPER_MODEL="small"
ENV WHISPER_DEVICE="cpu"
ENV WHISPER_COMPUTE="int8"

EXPOSE 8000
ENTRYPOINT ["/usr/bin/tini", "--"]
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
