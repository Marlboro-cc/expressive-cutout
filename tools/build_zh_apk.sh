#!/usr/bin/env bash
# Build locally. Optional signing uses your own key; this project contains no private key.
set -euo pipefail
cd "$(dirname "$0")/.."
python3 tools/check_zh_translation.py
./gradlew :app:assembleRelease --no-daemon --max-workers="${GRADLE_WORKERS:-2}"
unsigned=app/build/outputs/apk/release/app-release-unsigned.apk
output="${OUTPUT_DIR:-$PWD/local-apk}"
mkdir -p "$output"
if [[ -z "${KEYSTORE:-}" ]]; then
    cp "$unsigned" "$output/expressive-cutout-0.4.1-beta-zh.1-unsigned.apk"
    printf 'Unsigned build saved. Set KEYSTORE, KEY_ALIAS and KEYSTORE_PASSWORD_FILE to sign with your own key.\n'
    exit 0
fi
: "${ANDROID_SDK_ROOT:?Set ANDROID_SDK_ROOT}"
: "${KEY_ALIAS:?Set KEY_ALIAS}"
: "${KEYSTORE_PASSWORD_FILE:?Set KEYSTORE_PASSWORD_FILE}"
bt="$ANDROID_SDK_ROOT/build-tools/${BUILD_TOOLS_VERSION:-35.0.0}"
"$bt/zipalign" -P 16 -f 4 "$unsigned" "$output/aligned.apk"
key_password_args=()
if [[ -n "${KEY_PASSWORD_FILE:-}" ]]; then
    key_password_args=(--key-pass "file:$KEY_PASSWORD_FILE")
fi
"$bt/apksigner" sign --ks "$KEYSTORE" --ks-key-alias "$KEY_ALIAS" \
    --ks-pass "file:$KEYSTORE_PASSWORD_FILE" "${key_password_args[@]}" \
    --out "$output/expressive-cutout-0.4.1-beta-zh.1.apk" "$output/aligned.apk"
"$bt/apksigner" verify --verbose --print-certs "$output/expressive-cutout-0.4.1-beta-zh.1.apk"
rm "$output/aligned.apk"
