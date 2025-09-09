
# Setouta — Voice Friend (Arabic)

من الصفر: أندرويد (Kotlin) + خادم FastAPI. يدعم ستريم الصوت، نية بسيطة، ذكريات قصيرة، وردّ صوتي placeholder.
- **client-android**: يسجل صوت 16k PCM ويرسله WebSocket، ويشغّل صوت الرد (Base64 MP3).
- **server-fastapi**: بايبلاين بدائية قابلة للاستبدال بـ Whisper/Piper/Azure.

## تشغيل سريع
1) شغّل السيرفر: `server-fastapi/README.md`
2) افتح مشروع أندرويد في Android Studio، شغّل على المحاكي.
3) زر "ابدأ" لبث الصوت، ثم "إيقاف" لإرسال `eos` ومعالجة الجملة.

## الخطوة التالية
- دمج STT/TTS حقيقيين.
- إضافة wake word وخدمة Foreground.
- إدارة "المقاطع الطويلة" وقطع الحديث (barge‑in).


## Google Play — Store-Safe Build
- استخدم Flavor `storeRelease` لرفع AAB إلى Play Console.
- هذا الطعم يُزيل AccessibilityService و SYSTEM_ALERT_WINDOW، ويعرض شاشة موافقات قبل الاستخدام.
- بعد الإطلاق واستقرار السياسة، يمكن اختبار Flavor `fullRelease` على قنوات مغلقة.
- تذكر تعبئة Data Safety ومضاهاة السلوك الفعلي لما هو مذكور هناك وفي سياسة الخصوصية.


## Play Publish Steps (Ready-to-Ship)
1) In Android Studio, select **storeRelease** variant.
2) Build → **Generate Signed Bundle** → Android App Bundle (AAB).
3) In Play Console:
   - Create app → App details → Upload AAB to **Closed testing**.
   - Store listing: use `/store-listing/title.txt`, `short_description.txt`, `full_description.txt`.
   - Privacy Policy: host `/PRIVACY_POLICY.html` and put the URL — or ship in‑app (we included WebView).
   - Data safety: import `/store-listing/data_safety_template.json` and adapt to your exact setup.
   - Content rating + Categorization.
4) Rollout to testers, then move to Production with a staged rollout.


## GitHub Pages (Privacy Policy)
- We added `/docs` with `PRIVACY_POLICY.html` and a Pages workflow.
- In your repo: Settings → Pages → Build & deployment → Source = GitHub Actions (or "Deploy from a branch" /docs).
- Your policy URL will look like:
  `https://<your-username>.github.io/<your-repo>/PRIVACY_POLICY.html`
- Use that URL in Play Console.

## Versioning Strategy
- Tag-based releases set `VERSION_NAME` from tag and `VERSION_CODE` date-based.
- Manual builds can bump `VERSION_CODE` in `client-android/gradle.properties`:
  - Run **Actions → Bump versionCode & Build** to auto-increment, commit, and build.
- Gradle precedence: ENV > gradle.properties > defaults.


## Server Env Secrets (Piper / Whisper)
- `PIPER_BIN` : path to piper binary on the runner (if available)
- `PIPER_VOICE` : path/URL to Arabic voice model (e.g. ar_XM-*.onnx)
- `WHISPER_MODEL` : small/base/medium/large-v3
- `WHISPER_DEVICE` : cpu/cuda
- `WHISPER_COMPUTE` : int8/float16/float32

> ملاحظة: ورش Android لا تحتاج هذه القيم؛ هي مذكورة هنا لتجهيزك لورش سيرفر لاحقًا (Docker/Tests).
