# Untangle Rope: Twisted knot — release readiness

**Status: development package prepared; not yet cleared for Google Play production.**

## Checks supported by the current ZIP

- 500 level definitions are present and the QA report records the level data hash.
- Responsive portrait layout was swept across 19 viewport sizes; 9,500 level/layout combinations were checked with zero reported overlap or clipping issues. Very short windows may scroll vertically.
- Browser regression checks passed for the free first hint, rewarded hint completion/failure, hint persistence, interstitial frequency after two newly completed levels, and no-fill progression.
- The orientation policy JVM suite passes 45 cases. Full-screen app windows request portrait; multi-window leaves orientation to Android while the game content retains the portrait layout.
- Production selector copy now removes the test-mode wording; the saved current level is clamped to contiguous completed progress; debug selection methods are removed from the release bridge and solution arrays are not exposed by that bridge.
- Package name, Firebase Android client, supplied banner/rewarded/interstitial IDs, target SDK 36, game category and optional orientation hardware features were checked in source.

## Important testing limitation

The current QA does **not** demonstrate a full 500-level actual drag/play-through. All 500 levels received layout checks, but the actual drag checks in the included reports cover selected levels and viewports. Complete a recorded-solution play-through and mechanic regression pass for all 500 levels before calling the game fully gameplay-tested.

## Required before Play production

1. **Compile and inspect the actual Android release.** Push to GitHub and run the included Actions workflow, or build with Android Studio. Inspect the merged release manifest and the actual AAB; run the Play Console pre-launch report. The current ZIP contains source, not a signed AAB. The local audit environment did not have Gradle/Android SDK available, so native compilation is still pending.
2. **Test real devices.** Install the debug build on representative small/large phones and tablets, confirm portrait behavior, system insets, banner sizing, ad/consent overlays, background/resume, audio and progress persistence. Android can override orientation in some windowing scenarios; preserve the responsive portrait fallback.
3. **Check 16 KB page-size compatibility.** AGP is 8.12.0 (above the documented 8.5.1 packaging threshold), but SDKs may package native libraries. Inspect the release AAB and its `.so` libraries with current Android tooling and test a 16 KB emulator/device where applicable. Do not assume AGP version alone proves all transitive native libraries are compatible.
4. **Verify real AdMob behavior.** Confirm the app is approved/linked, UMP messages are published for the applicable regions, privacy choices work, test ads work in debug, and live banner/rewarded/interstitial inventory loads in a controlled internal test. Never click live ads to test. The game uses the standard Rewarded unit for extra hints and standard Interstitial after every two newly completed levels. The supplied Rewarded-interstitial unit `ca-app-pub-7725989747808511/2495425417` is not used because no separate placement is part of the approved flow.
5. **Verify Firebase/GA4.** Confirm Analytics is enabled and linked to the intended GA4 property/data stream in Firebase Console. Use DebugView to verify events with optional analytics enabled, then verify collection stops when the user disables it. The JSON config and SDK integration alone do not prove that GA4 is receiving data.
6. **Deploy the website.** Publish `website/` to a real HTTPS domain, then open the homepage, privacy policy, terms, support page and `/app-ads.txt` in a signed-out browser. Link that domain as the developer website in Play Console and AdMob; verify the seller file is crawled and accepted by AdMob.
7. **Complete trader details.** Add the actual verified Stralen Digital LLC business address and phone number wherever required by Play Console and applicable trader law. They are not included because they were not supplied. Verify account trader status and developer identity in Play Console.
8. **Complete Play Console declarations.** Declare Contains ads, Advertising ID use, Data Safety based on all SDK data flows, target audience, content rating, app access, privacy policy, and any applicable Families requirements. Do not guess the target audience.
9. **Prepare the store listing.** The package includes a 512×512 app icon and game preview captures, but not a finished 1024×500 feature graphic or a complete final store screenshot set. Prepare accurate phone screenshots (minimum two) and tablet screenshots if presenting tablet support (Google's large-screen screenshot guidance calls for at least four tablet/Chromebook screenshots at 1080 px or greater). Complete the app name, short description and full description.
10. **Sign and upload.** Configure Play App Signing and protect the upload key outside GitHub. Upload the signed AAB to internal testing, review device catalog, pre-launch results and policy status, then promote only after all issues are resolved.

## Current Android/Play policy references

- Target API: new apps and updates must target Android 16 / API 36 from 31 August 2026: https://support.google.com/googleplay/android-developer/answer/11926878
- Android 16 orientation and large-screen behavior; games are an exception to orientation restrictions based on `android:appCategory`: https://developer.android.com/about/versions/16/behavior-changes-16
- 16 KB page sizes: https://developer.android.com/guide/practices/page-sizes
- Trader status declaration: https://support.google.com/googleplay/android-developer/answer/17594752
- Store listing assets: https://support.google.com/googleplay/android-developer/answer/9866151
- App review, privacy policy and ads declaration: https://support.google.com/googleplay/android-developer/answer/9859455

**Bottom line:** the repository and browser-side work are in good shape, but do not call this production-ready until the native build, 500-level gameplay verification, real-device/ad/GA4 tests, website/trader setup, Play Console declarations, 16 KB check and signed AAB have passed.
