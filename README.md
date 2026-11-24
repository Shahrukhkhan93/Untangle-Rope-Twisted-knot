# Untangle Rope: Twisted knot

**Official Android game and developer website — Stralen Digital LLC**

500 underwater rope puzzles for Android. Package: `com.untangleropetwistedknot.app`. Support: `stralendigital@gmail.com`.

## Repository setup

- GitHub repository name: `Untangle-Rope-Twisted-knot` (GitHub repository names cannot contain the colon or spaces in the Play Store title). The display title above matches the requested Play Console app name exactly.
- Description: `Official Android source and website for Untangle Rope: Twisted knot by Stralen Digital LLC. 500 rope puzzles, AdMob, Firebase Analytics, and player support.`
- Recommended visibility: **Private**. The game source/artwork is proprietary; only the static website needs to be public. Do not upload signing keys, service-account credentials or release secrets. The bundled Firebase client configuration identifies the Android app; it is not a service-account credential. Review API restrictions and Firebase security configuration in the console.
- Create an empty repository, unzip this package, and upload its contents (including `.github`, `.gitignore`, and `netlify.toml`) at repository root. Do not upload the ZIP itself. See `PUBLISHING.md` for exact deployment and Play Console instructions.

## What's included

| Path | Purpose |
|---|---|
| `public/index.html` | Official responsive home page with actual game artwork and Google Play link |
| `public/privacy-policy.html` | Public privacy policy, including advertising and optional analytics |
| `public/terms-of-use.html` | Consumer-facing terms |
| `public/support.html` | Contact, trader identity and privacy/deletion request guidance |
| `public/app-ads.txt` | Authorized Google advertising seller record |
| `netlify.toml` | Publishes only `public/` at the hosting domain root |
| `android/` | Complete existing native Java/WebView Android source; 500 levels; orientation policy; Firebase and AdMob configuration; offline legal pages |
| `.github/workflows/android-build.yml` | Build checks, unsigned AAB and debug APK artifacts (not yet executed on GitHub) |
| `PUBLISHING.md` | Publication guide, required trader and legal checks, data safety worksheet, live testing and release gate |
| `QA_REPORT.json` | Local checks for this package |

## Android build

Use Android Studio to open the `android/` directory. Install JDK 17, SDK 36, and Gradle 8.13; generate a wrapper with `gradle wrapper --gradle-version 8.13` if needed (wrapper binaries are not bundled). Or use GitHub Actions after pushing the repo, then download the debug APK artifact. Debug builds use official Google test ads; release builds default to test ads unless `-PenableLiveAds=true` is explicitly passed. Never install a live-ad build for click testing. A release AAB must be signed with the correct upload key and checked before publication.

Production level selection permits only completed/current levels and the next unlocked level. Debug/browser selection remains available for QA. Phones and tablets both request portrait in full screen. The same proportional portrait artwork scales to the available area, with vertical scrolling on very short windows and a centered portrait fallback if Android overrides the request. The underlying puzzle definitions and physics are unchanged.

Privacy Policy, Terms of Use, Support and privacy controls are available inside the app via **Help → Privacy & legal**. Local copies load offline in a separate read-only WebView without the gameplay bridge. Optional analytics is off by default; players can enable it under this menu. AdMob consent forms must be configured in the AdMob console. No ad fill, live analytics reception, Play acceptance, native build or signing is asserted by this package.

**Release blockers:** Verify company trader address and phone, actual Play target audience and ad settings, GA4 association and retention, UMP forms, final deployed URLs, real native ad display, device testing and signed AAB manifest. The policy text must be reviewed against actual console settings and applicable laws before publication. Do not claim a verified trader status until declared and accepted by Google Play.


## Release readiness
See `RELEASE_READINESS.md` for the distinction between source/browser checks that passed and the external/native checks still required before Play production.
