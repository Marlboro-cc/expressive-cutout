# Simplified Chinese localization — unofficial independent build

This is a modified version of **Expressive Cutout**, originally by Evan Koehler / Expressive By Evan.
Upstream: https://github.com/EvanKoe/expressive-cutout
License: GPL-3.0, see the unchanged `LICENSE` and upstream copyright notices.
These localization changes are distributed under the same license. This build is not an official
release, not signed by the original author, and not endorsed by upstream.

## Source baseline

- Latest published release checked on 2026-11-24 (Asia/Shanghai): `0.4.1-beta`.
- Commit: `80ceb0b29a7c710b10b6a9cbcd11dd32745a3db5`.
- Upstream `dev` inspected at `50a4ad8fe363951ae3656031e2ca16c4fb2db818`.
- The published tag was deliberately chosen instead of unreleased dev appearance/navigation
  changes: the goal is to translate the latest released feature set without folding in unrelated
  changes. The upstream release is marked non-prerelease on GitHub but its own name is `beta`;
  it must not be described as a fully stable product.
- Local version: `0.4.1-beta-zh.1`, version code `8`, unchanged package
  `com.ekoehler.expressivecutout`.

## Scope

All 745 original default string resources have a Simplified Chinese counterpart. Added resource
extractions cover permission documentation, event subtitles and previews, notification timestamps,
reply/call accessibility descriptions, test-caller labels, and searchable Chinese Material icon labels.
Chinese call-action keywords are added alongside (not in place of) English matching terms, so
translated test calls still retain answer/decline/hang-up behavior. French and Turkish keep the original English fallback for newly extracted hardcoded text; their
existing translations are unchanged. Stable icon IDs, serialization
keys, package name, permission identifiers, URLs, brands and other languages are retained.
The editable custom-text widget's initial example is localized without rewriting user-saved text.
All 150 strings in the 18-entry release history are translated in a separate asset.

A runtime smoke test exposed that Android accessibility windows could keep English resources
while the Activity was Chinese. A localized Compose context/configuration now follows the existing
language StateFlow in the overlay; service event lookups use the same selected language. Live
English → Chinese switching and simulated answer/hang-up were tested without restarting the service.

The language list is still driven by `res/xml/locales_config.xml`; `zh-Hans` uses the existing
in-app picker and Android 13+ per-app language integration. Chinese regional tags such as `zh-CN`
and `zh-SG` normalize to `zh-Hans`. The explicit picker label is `简体中文`.
The original first-launch language behavior (English until a language is chosen) is unchanged.

The application does not translate third-party notifications, music metadata, contact names,
user-entered widget text, manufacturer/device names, external websites, or Android system dialogs.
Those are not app-owned interface strings.

## Build and validation

Linux; OpenJDK 21; Gradle wrapper 8.13; Android Gradle Plugin 8.13.2; Kotlin 2.1.0.
Install Android SDK command-line tools and accept the SDK licenses, then install:

```sh
sdkmanager 'platforms;android-37.2' 'build-tools;35.0.0' 'platform-tools'
export ANDROID_SDK_ROOT=/absolute/path/to/android-sdk
printf 'sdk.dir=%s\n' "$ANDROID_SDK_ROOT" > local.properties
python3 tools/check_zh_translation.py
python3 tools/check_utf8_and_apk.py
./gradlew :app:assembleRelease :app:testReleaseUnitTest :app:lintRelease --continue --no-daemon --max-workers=2
```

The upstream project intentionally compiles against API 37.2 for guarded newer rendering APIs.
Its original minimum remains API 29 (Android 10); target API remains 35.
Do not lower compileSdk or remove upstream APIs merely to build this translation.
`local.properties` is machine-local and is intentionally not included in the source archive.

`tools/build_zh_apk.sh` creates an unsigned release APK, or signs when the documented environment
variables are set. Use your own keystore, alias, and private password file. The delivered key and
passwords are **not** included. There are no credentials needed to compile the public project.
To compare the compiled APK against every Chinese source resource, run:

```sh
python3 tools/check_utf8_and_apk.py --apk /path/to/app.apk --aapt2 "$ANDROID_SDK_ROOT/build-tools/35.0.0/aapt2"
```

The source and build instructions allow rebuilding the program; a different key/tool environment
will not reproduce the exact delivered APK hash/signature.

The upstream `ExpandedMediaHeightTest` has one pre-existing assertion failure. It was reproduced
on the unmodified tag in a separate worktree. This localization does not change its geometry logic
or weaken/delete that test. See the delivered test report for actual results and limits.
