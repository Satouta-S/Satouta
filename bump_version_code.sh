
#!/usr/bin/env bash
set -euo pipefail
FILE="client-android/gradle.properties"
if [ ! -f "$FILE" ]; then
  echo "VERSION_CODE=1" > "$FILE"
fi
VC=$(grep -E '^VERSION_CODE=' "$FILE" | cut -d'=' -f2 || echo "1")
if [ -z "$VC" ]; then VC=1; fi
NEW=$((VC+1))
sed -i.bak -E "s/^VERSION_CODE=.*/VERSION_CODE=$NEW/" "$FILE"
rm -f "$FILE.bak"
echo "Bumped VERSION_CODE to $NEW"
