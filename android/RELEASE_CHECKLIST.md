# Untangle Rope: Twisted Knot — Android release preflight
Prepared 24 November 2026. This is a launch preparation kit, not a compiled Android project or approved release. The supplied 500-level web game is copied unchanged. Android SDK, native project, final package ID, production ad IDs and signing configuration are not available in this workspace.

## Apply at Android integration
- [ ] Confirm final unique applicationId with Play Console; never change it after the first release. Do not reuse Water Sort's ID.
- [ ] Use a supported Android Gradle plugin and compileSdk >= 36, targetSdk >= 36. Pick minSdk from actual SDK requirements and device coverage. Set a monotonic versionCode and deliberate versionName.
- [ ] Run `python android_preflight.py patch path/to/android/app/src/main/AndroidManifest.xml` on the existing manifest (it preserves the identity and activity settings; creates a backup). Review the diff. Source permissions assume the planned ads-enabled variant; if releasing without ads, reevaluate AD_ID and SDKs instead.
- [ ] Inspect the final merged release manifest, not only the source. Run `python android_preflight.py check path/to/merged/AndroidManifest.xml` or use `java -jar bundletool.jar dump manifest --bundle=app-release.aab --module=base > final-manifest.xml`, then check `final-manifest.xml`. A passing check covers the targeted manifest rules only.
- [ ] Inspect device catalog and AAPT2 badging for any remaining implied hardware filters. Declare only genuinely optional features optional. Test portrait phones, portrait tablets, foldables, desktop-window resizing and system insets; do not claim PC/XR support without tests. Keep the game layout and safe touch coordinates. Use game category and account for Android 16 edge-to-edge behavior. Verify banner does not cover board or controls, including smallest viewport.
- [ ] Use local assets with a secure WebView asset origin, DOM storage for save progress, no unrestricted JS bridge to remote pages, no cleartext HTTP, and no needless permissions. Keep test hooks out of production or inaccessible. Ensure back, resume, audio focus, network loss and save restoration work. Current source saves level completion/current level and sound locally; mid-puzzle restore needs actual verification/implementation if required.
- [ ] Build signed release AAB with Play App Signing. Keep upload key private. Verify 64-bit and 16 KB page-size compatibility of all native dependencies (including transitive SDKs). Inspect all modules, run bundletool validation, native library alignment checks and Android 16 device/emulator testing. Do not declare readiness based only on the web build.

## Advertising implementation (approved requirements; not yet integrated)
- [ ] Add maintained Google Mobile Ads and UMP SDKs and real application ID. Use Google test units in development; never click live ads in testing. Verify release contains real unit IDs and no test configuration. Configure consent at startup and provide privacy options where required; request ads only when permitted. Apply any required child/under-age settings before initialization based on actual target audience.
- [ ] First hint per level free, persisted across app restarts and puzzle restarts. Once used, show small film-strip AD badge at the button corner. Each further hint is an explicit rewarded-ad choice; grant exactly one hint only on SDK reward callback, not dismissal. If skipped/unavailable/fails, no hint; allow gameplay. Debounce and associate callback with the requesting level.
- [ ] Interstitial only after every two newly completed levels at natural Next Level transition, using persistent unique-completion bookkeeping. No ad after first level, failure, restart, replay, startup or during rope dragging. If unavailable, progress normally. No stacking with rewarded ads; avoid disruptive frequency and validate policy compliance.
- [ ] Adaptive banner in dedicated space below Undo/Hint/Restart; reserve space, prevent accidental clicks, respect safe insets, and remeasure after changes. Never overlay gameplay or controls. No fake placeholder ad in production.
- [ ] Verify ad lifecycle, callbacks, duplicate clicks, offline cases, consent rejection, pause/resume and persistence on device. Document all SDK collection/sharing including mediation, analytics if added, and actual settings.
- [ ] Set up app-ads.txt at the root of the developer website listed in Play Store. Use the exact personalized line from AdMob (do not invent a publisher ID). Verify app-ads.txt and AdMob app readiness.

## Play Console — complete before sending for review
- [ ] App content: Contains ads = Yes for actual ads-enabled release; Advertising ID = Yes if actual bundle/SDK uses it, advertising/marketing purpose and all other actual purposes (analytics only if applicable). Verify final bundle and SDK configuration first. A manifest permission alone does not submit the form.
- [ ] Data safety: complete using final SDK inventory, network behavior and Google's Mobile Ads data disclosure guide. Do not claim no collection just because game has no account. If analytics added, include its disclosures. Keep form consistent with policy and SDK behavior.
- [ ] Publish public HTTPS privacy policy naming app and developer, accessible in store AND in app. Include actual data, collection/sharing, purposes, retention/deletion, choices and contact. No copied or invented disclosures. If account creation is added, assess deletion requirements. Set app access instructions, content rating, target audience, ads and other applicable declarations accurately. Do not select an age group simply to avoid policies.
- [ ] Store listing: title within 30 characters, short description <=80, full description <=4000; icon, feature graphic, real screenshots, support email and developer website. No misleading claims.
- [ ] Internal test then closed testing if required: new personal accounts created after 13 Nov 2023 generally require at least 12 testers opted in continuously for 14 days before applying for production access. Check account-specific Console requirements and verification.
- [ ] Upload AAB; review pre-launch report, device exclusions, Android vitals, policy warnings and all App content tasks before production submission. Resolve blockers; review warnings rather than blindly proceed. Perform full gameplay/Android regression, especially known Level 24 issue if still present. Browser-only layout tests are not Android certification.

## Official references
- Target API: https://support.google.com/googleplay/android-developer/answer/11926878
- Hardware feature filtering: https://developer.android.com/guide/topics/manifest/uses-feature-element
- Advertising ID: https://support.google.com/googleplay/android-developer/answer/6048248
- Android 16 behavior: https://developer.android.com/about/versions/16/behavior-changes-16
- 16 KB pages: https://developer.android.com/guide/practices/page-sizes
- AdMob setup: https://developers.google.com/admob/android/quick-start
- Consent: https://developers.google.com/admob/android/privacy
- Data disclosure: https://developers.google.com/admob/android/privacy/play-data-disclosure
- App content: https://support.google.com/googleplay/android-developer/answer/9859455
- App-ads.txt: https://support.google.com/admob/answer/14538460
- Closed testing: https://support.google.com/googleplay/android-developer/answer/14151465


## Adaptive layout release gate
- [x] Source manifest: optional portrait/landscape features, resizable activity, no static orientation lock; runtime portrait request.
- [x] 500-level portrait DOM layout audit across 19 simulated Chromium content windows (including wide OS fallback).
- [ ] Compile final signed AAB and run android_preflight.py check on its bundletool-dumped merged manifest.
- [ ] Test native WebView, safe insets, touch input, screen rotation, tablet/foldable/multi-window and real test-ad banner resizing on Android devices or emulators.
- [ ] Confirm Google Play device catalog and pre-launch report; complete Advertising ID declaration.


### Device orientation release gate
- Verify full-screen portrait on compact phones, large phones, tablets and foldables. Verify portrait content remains centered if Android overrides the request.
- Verify folded/unfolded transition on actual foldable without WebView reload or lost moves.
- Verify multi-window resizing, Android orientation overrides, ad/consent return and banner sizing.
- Inspect merged release AAB manifest: optional portrait/landscape features, game category, resizable application/activity, no static orientation lock.
- JVM policy checks and source XML verification passed; native tests remain pending.

## Repository update
The root PUBLISHING.md supersedes earlier checklist entries concerning website, in-app legal access, icon and production level locking. Verify those changes in the compiled Android build.
