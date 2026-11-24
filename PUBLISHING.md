# Publishing guide — Untangle Rope: Twisted knot

## 1. GitHub and Netlify

Create a **private** GitHub repository named `Untangle-Rope-Twisted-knot`. Use the title and description from README. From the extracted repository root, you can run:

```sh
git init -b main
git add .
git commit -m "Initial Android and official website source"
git remote add origin https://github.com/YOUR-ACCOUNT/Untangle-Rope-Twisted-knot.git
git push -u origin main
```

Alternatively, upload all the extracted folders and root files through GitHub. Ensure hidden files are included. In Netlify, choose **Add new project → Import an existing project → GitHub**, authorize access to this repository, and deploy. No build command or base directory is necessary; `netlify.toml` sets the publish directory to `public`. Choose a dedicated site hostname (for example, `untangle-rope-twisted-knot.netlify.app` if available, or a verified custom domain). The example hostname is not registered or deployed by this package. Do not use a project subpath as your advertising seller file needs to be at the domain root.

After deployment open each public URL in a signed-out browser (replace `YOUR-SITE` with your actual hostname):

- `https://YOUR-SITE/`
- `https://YOUR-SITE/privacy-policy.html`
- `https://YOUR-SITE/terms-of-use.html`
- `https://YOUR-SITE/support.html`
- `https://YOUR-SITE/app-ads.txt` (plain text, HTTP 200, no password or redirect to homepage)

Play Store listing: enter the actual homepage in Website, privacy policy URL in the App content privacy policy field, and `stralendigital@gmail.com` for support. Terms/support are linked from the site and bundled within the app. Verify public URLs are active, non-editable by visitors, HTTPS, and accessible in all distributed regions. Google Play URL in the website is constructed from the confirmed package ID; the listing will work only once available. Do not distribute an unverified or unsigned APK from this site.

## 2. Trader declaration and company identity — owner action required

Known legal entity: **Stralen Digital LLC**; registered in the United States as supplied by owner. Email: **stralendigital@gmail.com**. This package does not invent an address, telephone number, state, registration number, D-U-N-S number or claim that Google has verified them.

Because this is a commercially published ad-supported game, trader status is likely applicable; the company should confirm its declaration under the relevant legal definition, not choose non-trader to conceal contact details. Complete the Trader status declaration and developer verification in Play Console using the exact legal entity, actual verified business mailing/legal address and telephone number. Match the Google payments profile and D&B profile where applicable. Add the verified public business address and phone to the website's support page (and corresponding `android/app/src/main/assets/legal/support.html`), and to the contact blocks in the website/in-app legal pages if applicable law requires them. Do not publish fictitious information. The current site points to the official Play listing for verified trader contact details when available. That link does not replace any applicable website-specific disclosure obligations. Obtain any necessary EU/UK representative details and other business disclosures where legally required. Verify local legal requirements with qualified counsel. Google Play requires a declaration from all active developer accounts by February 1, 2027 according to its current help guidance; complete it now rather than wait.

Official guidance:
- https://support.google.com/googleplay/android-developer/answer/17594752
- https://support.google.com/googleplay/android-developer/answer/13628312
- https://support.google.com/googleplay/android-developer/answer/10841920

## 3. AdMob configuration

| Field | Production value |
|---|---|
| App ID | `ca-app-pub-7725989747808511~8701741117` |
| Banner | `ca-app-pub-7725989747808511/7950611270` |
| Rewarded hint | `ca-app-pub-7725989747808511/5530577478` |
| Standard interstitial | `ca-app-pub-7725989747808511/8521456918` |

The supplied Rewarded interstitial unit `ca-app-pub-7725989747808511/2495425417` is intentionally **not used** in the current approved ad flow. The game uses the standard Rewarded unit for extra hints and the standard Interstitial unit after every two newly completed levels. Do not report the rewarded-interstitial unit as integrated unless a separate placement is later approved and implemented.
| Authorized seller | `google.com, pub-7725989747808511, DIRECT, f08c47fec0942fa0` |

Confirm app-ads.txt against the exact snippet in your AdMob account. Link this app to its actual Play listing and set its developer website to the deployed hostname in Play Console. Verify app-ads.txt in AdMob after crawling. Use UMP Privacy & messaging: publish the applicable consent messages for the EEA/UK/Switzerland and US states, configure any other region/age-related requirements, and test both consent acceptance/refusal and privacy options. Do not initialize ad requests before UMP permits them; respect no-fill and unavailable ads. Confirm ad content rating, blocking controls, target audience and age-related settings for this game. Existing source has no age gate or child-directed ad treatment; if children are included in the declared audience, implement the required Families policies, age-screening/child-directed request configuration, compliant SDKs and revised disclosure before releasing. Do not select a false audience.

Expected native behavior: one free usable hint per level, subsequent hints only after earned reward callback, standard interstitial attempts after each two newly completed levels on Next Level, and adaptive banner below controls. The banner is intentionally hidden during overlays/fullscreen ads and suppressed in very small windows. Debug builds use sample test IDs. No live ads on the informational website. Ad serving and fill depend on account approval, network, UMP, inventory, test settings and device. No ad must cover controls or encourage accidental clicks.

Google references: https://developers.google.com/admob/android/quick-start · https://developers.google.com/admob/android/privacy · https://support.google.com/admob/answer/9363762 · https://developers.google.com/admob/android/privacy/play-data-disclosure

## 4. Firebase and GA4

`android/app/google-services.json` contains only the matching supplied Firebase Android client. Project ID `untangle-rope-twisted-knot`; package `com.untangleropetwistedknot.app`; Android app ID `1:495860591804:android:90bf4edb8d332de0eeb4b9`. The original JSON also had an unrelated Water Sort client; that client was excluded from this repository, not deleted from the Firebase account. Google Services plugin and Firebase Analytics BoM are already configured in the source. In Firebase console verify Analytics is enabled and linked to the correct GA4 property and Android data stream, and verify the correct retention setting; choose a data minimization setting appropriate to your needs. Optional gameplay analytics defaults off, separately from advertising consent. Analytics advertising ID collection is disabled in the Android manifest; AdMob can still use the Android advertising ID where permitted.

Actual event bridge: `level_start`, `level_end`, `level_restart`, `hint_used`, `rewarded_ad_completed`, `level_duration`, `interstitial_ad_shown`. Level events may include `level_number`, `level_name`, duration in seconds, completion and hint type. No name or email is attached. Enable gameplay analytics in the debug build via Help → Privacy & legal → Optional gameplay analytics. Enable DebugView on your Android test device:

```sh
adb shell setprop debug.firebase.analytics.app com.untangleropetwistedknot.app
# play, restart, use hints and complete levels with test ads; observe GA4 DebugView
adb shell setprop debug.firebase.analytics.app .none.
```

Verify events with analytics allowed, then disable and verify optional gameplay event collection stops. Check Google Analytics data sharing, ad personalization and retention settings. Do not represent that GA4 has received events until verified. See https://firebase.google.com/docs/analytics/android/get-started and https://firebase.google.com/docs/analytics/android/configure-data-collection.

## 5. Play Console data safety / app content worksheet (review actual SDK and account settings)

This is a worksheet, not an already submitted declaration. Play Console's final labels depend on actual release dependencies, settings, purposes and data flows. Review every category and definitions against official SDK disclosures, including how SDK data is collected, used, transferred, shared and handled ephemerally. Do not simply copy Yes/No answers without review.

| Field | Source-based finding / action |
|---|---|
| Contains ads | Yes — AdMob banner, rewarded and standard interstitial |
| Advertising ID | Yes — AD_ID permission and Google Mobile Ads; review Advertising or marketing, analytics and fraud prevention purposes as applicable |
| Accounts | No in-game account creation or sign-in; account deletion requirement is not triggered solely by this version's local save; verify any later change |
| Financial transactions | No in-app purchases or payment flow in this version |
| Location | No precise location permission; SDK may infer approximate location from IP; review Approximate location |
| Device or other IDs | Advertising ID, app set ID, app instance and other SDK identifiers where applicable; review collection and sharing |
| App activity | SDK app/ad interactions and optional gameplay events; review collection and sharing |
| App info and performance | SDK diagnostics, crash/performance information; review collection and sharing |
| Personal info | No game sign-in; email received only when player voluntarily contacts developer outside app; consider applicable disclosure definitions |
| Data encryption | SDKs use encrypted transport where supported; confirm all applicable network flows before answering |
| Data deletion | Local progress can be cleared; privacy/support email accepts requests and SDK data limitations are disclosed; implement request handling and verify response in console |
| Target audience and content rating | Confirm actual target ages, content rating questionnaire, ad suitability and Families requirements before release |
| App access | No account or credential needed to reach levels; describe sequential progression for reviewers |
| Privacy policy | Publish non-editable website and use in-app Help → Privacy & legal; review accurate retention details and company contacts |

Google Play user data policy: https://support.google.com/googleplay/android-developer/answer/10144311 ; Firebase SDK data disclosures: https://firebase.google.com/docs/android/play-data-disclosure ; Google Mobile Ads disclosure: https://developers.google.com/admob/android/privacy/play-data-disclosure

## 6. Build and release gate

1. Push the repo, enable GitHub Actions and run Android build checks. Debug APK and unsigned release AAB will be available as workflow artifacts if compilation passes; the workflow has not yet run. For manual local compilation use `cd android` and `gradle :app:assembleDebug`. Run actual native debug APK on emulator/phones/tablets with Google test ads. Verify portrait orientation on phones and tablets, centered portrait content under OS overrides, and banner spacing, first free hint and reward completion, skipped reward, interstitial interval and no-fill. Confirm in-app legal viewer and UMP choices. Test analytics consent and DebugView.
2. Confirm release level unlocking, app title/package, launcher icon, version, supported APIs and SDK/16 KB compatibility. Use actual Play Console app icon requirements for final listing artwork; included icon is a crop of original splash, not a Play-approved icon. Build final production bundle using real IDs with `gradle :app:bundleRelease -PenableLiveAds=true` after configuring signing in Android Studio or a protected local/CI signing setup. Use Android Studio's Generate Signed Bundle/APK workflow with the correct upload key and release variant (ensure live property enabled for release). Never commit the upload key or its passwords. The repository includes no signing secrets or signed AAB.
3. Inspect the **actual AAB's** base merged manifest (Android Studio Analyze APK or `bundletool dump manifest --bundle=... --module=base`), run `python android/android_preflight.py check merged-manifest.xml` from repo root. Both portrait and landscape features must be optional; resizable activity true; no static screen orientation (portrait requested at runtime); target SDK 36 configured; production AdMob App ID. Confirm native libraries/16 KB page size if applicable, and verify package/version/signing.
4. Upload to internal testing; check device catalog and pre-launch report, then fulfill any account-specific testing requirements. Complete accurate data safety, ads, advertising ID, trader, content rating, target audience, app access, privacy and developer contact declarations. Validate app-ads.txt and UMP. Google Play review and ad serving are not guaranteed.
5. Publish only after any native errors and policy gaps have been resolved. Review legal documents whenever actual practices change; do not claim they replace legal review.
