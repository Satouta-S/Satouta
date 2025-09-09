
# CI/CD — Android Store Release

## Required GitHub Secrets
- `ANDROID_KEYSTORE_BASE64`: Base64 of your release keystore (.jks)
- `ANDROID_KEYSTORE_PASSWORD`: Keystore password
- `ANDROID_KEY_ALIAS`: Key alias
- `ANDROID_KEY_PASSWORD`: Key password
- (Optional) `PLAY_SERVICE_ACCOUNT_JSON`: Full JSON of Google Play service account

## Run
- Go to GitHub → Actions → **Android Store Release** → **Run workflow**
- Choose `upload_to_play = true` to push to **Internal testing** (else it'll just build + upload artifact).


## Tag-based Releases
- Create a tag named like `v1.2.3` and push it:
  ```bash
  git tag v1.2.3
  git push origin v1.2.3
  ```
- The workflow **Release on Tag** will:
  - Set VERSION_NAME=1.2.3 and a date-based VERSION_CODE
  - Build `storeRelease` AAB
  - Attach it to a GitHub Release
  - (If service account configured) upload to Google Play Internal testing
  - Spin up an emulator and capture 3 screenshots as artifacts


---
## Android Client Docker Build
- Local:
  ```bash
  docker build -t setouta/android -f Dockerfile.android .
  docker run --rm -v "$PWD/client-android":/workspace setouta/android
  ```

## Firebase Test Lab
- Add GitHub Secrets:
  - `GCP_PROJECT_ID`
  - `GCP_SA_KEY` (JSON content of a service account with FTL access)
  - `GCP_RESULTS_BUCKET` (a GCS bucket you own)
- Run workflow **Firebase Test Lab (Android)** to exercise `androidTest` on a real device.

## Monitoring
- Add Secrets:
  - `SERVER_HEALTH_URL` (e.g., https://your-domain/health)
  - (optional) `SLACK_WEBHOOK_URL`
- Workflow **Uptime Monitor** runs every 30 min and on demand. Opens an issue and/or sends Slack alert.
