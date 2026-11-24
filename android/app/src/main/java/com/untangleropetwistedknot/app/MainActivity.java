package com.untangleropetwistedknot.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Insets;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.view.WindowInsets;
import android.view.Gravity;
import android.webkit.*;
import android.widget.*;
import androidx.webkit.WebViewAssetLoader;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.interstitial.*;
import com.google.android.gms.ads.rewarded.*;
import com.google.android.ump.*;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
    private static final String ORIGIN = "https://appassets.androidplatform.net/assets/index.html";
    private static final String BANNER = BuildConfig.LIVE_ADS ? "ca-app-pub-7725989747808511/7950611270" : "ca-app-pub-3940256099942544/6300978111";
    private static final String REWARDED = BuildConfig.LIVE_ADS ? "ca-app-pub-7725989747808511/5530577478" : "ca-app-pub-3940256099942544/5224354917";
    private static final String INTERSTITIAL = BuildConfig.LIVE_ADS ? "ca-app-pub-7725989747808511/8521456918" : "ca-app-pub-3940256099942544/1033173712";
    private static final Set<String> EVENTS = new HashSet<>(Arrays.asList("level_start", "level_end", "level_restart", "hint_used", "rewarded_ad_completed", "level_duration"));
    private WebView web;
    private LinearLayout root;
    private FrameLayout bannerSlot;
    private AdView banner;
    private ConsentInformation consent;
    private FirebaseAnalytics analytics;
    private SharedPreferences prefs;
    private RewardedAd rewarded;
    private InterstitialAd interstitial;
    private boolean initializing, initialized, rewardLoading, interstitialLoading, showing, resumed, gameVisible, bannerLoaded, consentFormActive, legalVisible;
    private int bannerWidth, bannerOrientation;
    private final Runnable resizeBanner=()->ensureBanner();
    private void scheduleBannerResize(){root.removeCallbacks(resizeBanner);root.postDelayed(resizeBanner,180);}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private boolean allowed(){return consent!=null && consent.canRequestAds() && !consentFormActive;}
    private boolean alive(){return !isFinishing() && !isDestroyed();}
    // All full-screen devices request portrait. Leave multi-window orientation to Android.
    private void applyDeviceOrientation(){
        if (!alive() || showing || consentFormActive) return;
        Configuration c = getResources().getConfiguration();
        boolean windowed = Build.VERSION.SDK_INT >= 24 && isInMultiWindowMode();
        OrientationPolicy.Mode mode = OrientationPolicy.choose(c.smallestScreenWidthDp,
            c.screenWidthDp, c.screenHeightDp, windowed);
        int target = mode == OrientationPolicy.Mode.PORTRAIT
                ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        if (getRequestedOrientation() != target) setRequestedOrientation(target);
    }
    @Override public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);
        applyDeviceOrientation();
        if(root!=null){root.requestApplyInsets();scheduleBannerResize();}
    }
    @Override public void onMultiWindowModeChanged(boolean inMultiWindowMode){
        super.onMultiWindowModeChanged(inMultiWindowMode);
        if(root!=null) root.post(this::applyDeviceOrientation);
    }
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        applyDeviceOrientation();
        prefs=getSharedPreferences("privacy",MODE_PRIVATE);
        analytics=FirebaseAnalytics.getInstance(this);
        analytics.setAnalyticsCollectionEnabled(prefs.getBoolean("analytics",false));
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.rgb(2,125,156));
        root.setOnApplyWindowInsetsListener((v,i)->{if(Build.VERSION.SDK_INT>=30){Insets safe=i.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());v.setPadding(safe.left,safe.top,safe.right,safe.bottom);}else{v.setPadding(i.getSystemWindowInsetLeft(),i.getSystemWindowInsetTop(),i.getSystemWindowInsetRight(),i.getSystemWindowInsetBottom());}scheduleBannerResize();return i.consumeSystemWindowInsets();});
        web=new WebView(this);web.setBackgroundColor(Color.rgb(2,125,156));
        web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAllowFileAccess(false);web.getSettings().setAllowContentAccess(false);
        web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        web.getSettings().setMediaPlaybackRequiresUserGesture(true);
        WebViewAssetLoader loader=new WebViewAssetLoader.Builder().addPathHandler("/assets/",new WebViewAssetLoader.AssetsPathHandler(this)).build();
        web.setWebViewClient(new WebViewClient(){
            @Override public WebResourceResponse shouldInterceptRequest(WebView v,WebResourceRequest r){WebResourceResponse local=loader.shouldInterceptRequest(r.getUrl());return local!=null?local:new WebResourceResponse("text/plain","UTF-8",new java.io.ByteArrayInputStream(new byte[0]));}
            @Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return !ORIGIN.equals(r.getUrl().toString());}
        });
        web.addJavascriptInterface(new Bridge(),"RopeNative");
        root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
        bannerSlot=new FrameLayout(this);bannerSlot.setPadding(0,dp(12),0,0);bannerSlot.setVisibility(View.GONE);
        root.addView(bannerSlot,new LinearLayout.LayoutParams(-1,-2));
        setContentView(root);root.requestApplyInsets();
        root.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob)->{if(initialized && (r-l!=or-ol || b-t!=ob-ot)) scheduleBannerResize();});
        web.loadUrl(ORIGIN);
        consent=UserMessagingPlatform.getConsentInformation(this);
        consentFormActive=true;
        consent.requestConsentInfoUpdate(this,new ConsentRequestParameters.Builder().build(),()->{
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(this,error->{consentFormActive=false;syncConsent();});
        },error->{consentFormActive=false;syncConsent();});
    }
    private void syncConsent(){
        if(!alive())return;
        applyDeviceOrientation();
        if(!allowed()){rewarded=null;interstitial=null;destroyBanner();return;}
        if(!initialized){if(initializing)return;initializing=true;MobileAds.initialize(this,status->runOnUiThread(()->{initializing=false;initialized=true;if(alive()&&allowed()){loadRewarded();loadInterstitial();ensureBanner();}}));}
        else{loadRewarded();loadInterstitial();ensureBanner();}
    }
    private void destroyBanner(){if(banner!=null){banner.destroy();banner=null;}bannerLoaded=false;bannerWidth=0;bannerSlot.removeAllViews();bannerSlot.setVisibility(View.GONE);}
    private void ensureBanner(){
        if(!alive()||!allowed()||!initialized)return;
        int w=(int)((root.getWidth()-root.getPaddingLeft()-root.getPaddingRight())/getResources().getDisplayMetrics().density);
        int h=(int)((root.getHeight()-root.getPaddingTop()-root.getPaddingBottom())/getResources().getDisplayMetrics().density);
        int orientation=getResources().getConfiguration().orientation;
        w=Math.min(440,w);if(w<320||h<420){destroyBanner();return;}if(banner!=null && bannerWidth==w && bannerOrientation==orientation){syncBanner();return;}
        destroyBanner();bannerWidth=w;bannerOrientation=orientation;AdView a=new AdView(this);banner=a;
        a.setAdUnitId(BANNER);a.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this,w));
        a.setAdListener(new AdListener(){@Override public void onAdLoaded(){if(banner==a){bannerLoaded=true;syncBanner();}} @Override public void onAdFailedToLoad(LoadAdError e){if(banner==a){bannerLoaded=false;syncBanner();}}});
        bannerSlot.addView(a,new FrameLayout.LayoutParams(-2,-2,Gravity.CENTER));
        a.loadAd(new AdRequest.Builder().build());
    }
    private void syncBanner(){boolean visible=allowed()&&gameVisible&&resumed&&bannerLoaded&&!showing&&!legalVisible;bannerSlot.setVisibility(visible?View.VISIBLE:(bannerLoaded&&allowed()?View.INVISIBLE:View.GONE));}
    private void loadRewarded(){
        if(!alive()||!initialized||!allowed()||rewardLoading||rewarded!=null)return;rewardLoading=true;
        RewardedAd.load(this,REWARDED,new AdRequest.Builder().build(),new RewardedAdLoadCallback(){@Override public void onAdLoaded(RewardedAd a){rewardLoading=false;if(alive()&&allowed())rewarded=a;}@Override public void onAdFailedToLoad(LoadAdError e){rewardLoading=false;}});
    }
    private void loadInterstitial(){
        if(!alive()||!initialized||!allowed()||interstitialLoading||interstitial!=null)return;interstitialLoading=true;
        InterstitialAd.load(this,INTERSTITIAL,new AdRequest.Builder().build(),new InterstitialAdLoadCallback(){@Override public void onAdLoaded(InterstitialAd a){interstitialLoading=false;if(alive()&&allowed())interstitial=a;}@Override public void onAdFailedToLoad(LoadAdError e){interstitialLoading=false;}});
    }
    private void reply(int id,boolean ok){if(alive()&&web!=null)web.evaluateJavascript("window.ropeAdResult&&window.ropeAdResult("+id+","+ok+")",null);}
    private void finishAd(int id,boolean ok){showing=false;syncBanner();reply(id,ok);applyDeviceOrientation();loadRewarded();loadInterstitial();}
    private void show(String kind,int id){
        if(!alive()||!resumed||!allowed()||showing){reply(id,false);return;}
        if("rewarded".equals(kind)){
            RewardedAd ad=rewarded;if(ad==null){loadRewarded();reply(id,false);return;}rewarded=null;showing=true;syncBanner();final boolean[] earned={false};
            ad.setFullScreenContentCallback(new FullScreenContentCallback(){@Override public void onAdDismissedFullScreenContent(){finishAd(id,earned[0]);}@Override public void onAdFailedToShowFullScreenContent(AdError e){finishAd(id,false);}});
            try{ad.show(this,reward->earned[0]=true);}catch(RuntimeException e){finishAd(id,false);}
        }else if("interstitial".equals(kind)){
            InterstitialAd ad=interstitial;if(ad==null){loadInterstitial();reply(id,false);return;}interstitial=null;showing=true;syncBanner();
            ad.setFullScreenContentCallback(new FullScreenContentCallback(){@Override public void onAdShowedFullScreenContent(){if(prefs.getBoolean("analytics",false))analytics.logEvent("interstitial_ad_shown",new Bundle());}@Override public void onAdDismissedFullScreenContent(){finishAd(id,true);}@Override public void onAdFailedToShowFullScreenContent(AdError e){finishAd(id,false);}});
            try{ad.show(this);}catch(RuntimeException e){finishAd(id,false);}
        }else reply(id,false);
    }
    private void privacy(){
        if(!alive()||showing||consentFormActive)return;
        new AlertDialog.Builder(this).setTitle("Privacy & legal").setItems(new String[]{"Privacy Policy","Terms of Use","Support & developer information","Advertising privacy options","Optional gameplay analytics"},(d,which)->{
            if(which<=2){showLegal(which==0?"privacy-policy.html":which==1?"terms-of-use.html":"support.html");return;}
            if(which==3){if(consent.getPrivacyOptionsRequirementStatus()==ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED){consentFormActive=true;rewarded=null;interstitial=null;destroyBanner();UserMessagingPlatform.showPrivacyOptionsForm(this,e->{consentFormActive=false;syncConsent();});}else new AlertDialog.Builder(this).setMessage("Advertising privacy options are not currently required for this device.").setPositiveButton("OK",null).show();}
            else new AlertDialog.Builder(this).setTitle("Optional gameplay analytics").setMessage("Allow collection of gameplay events to help improve puzzles? You can change your choice here at any time.").setPositiveButton("Allow",(a,b)->setAnalytics(true)).setNegativeButton("Don't allow",(a,b)->setAnalytics(false)).show();
        }).setNegativeButton("Close",null).show();
    }
    // Offline legal pages are read-only and use a separate WebView with no JavaScript bridge.
    private void showLegal(String file){
        if(!alive()||showing||legalVisible)return;
        final String prefix="https://appassets.androidplatform.net/assets/legal/";
        final Set<String> pages=new HashSet<>(Arrays.asList("privacy-policy.html","terms-of-use.html","support.html"));
        if(!pages.contains(file))return;
        WebViewAssetLoader loader=new WebViewAssetLoader.Builder().addPathHandler("/assets/",new WebViewAssetLoader.AssetsPathHandler(this)).build();
        WebView viewer=new WebView(this);
        viewer.setBackgroundColor(Color.rgb(7,50,71));
        viewer.getSettings().setJavaScriptEnabled(false);
        viewer.getSettings().setAllowFileAccess(false);viewer.getSettings().setAllowContentAccess(false);
        viewer.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        viewer.setWebViewClient(new WebViewClient(){
            @Override public WebResourceResponse shouldInterceptRequest(WebView v,WebResourceRequest r){
                if(r.getUrl().toString().startsWith(prefix)){WebResourceResponse local=loader.shouldInterceptRequest(r.getUrl());if(local!=null)return local;}
                return new WebResourceResponse("text/plain","UTF-8",new java.io.ByteArrayInputStream(new byte[0]));
            }
            @Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){
                String url=r.getUrl().toString();
                if(url.startsWith(prefix)&&pages.contains(url.substring(prefix.length())))return false;
                String scheme=r.getUrl().getScheme();
                if("https".equals(scheme)||"mailto".equals(scheme))try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}catch(android.content.ActivityNotFoundException e){Toast.makeText(MainActivity.this,"No compatible app installed",Toast.LENGTH_SHORT).show();}
                return true;
            }
        });
        legalVisible=true;syncBanner();
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Privacy & legal").setView(viewer).setPositiveButton("Close",null).create();
        dialog.setOnDismissListener(d->{viewer.stopLoading();viewer.destroy();legalVisible=false;syncBanner();});
        dialog.show();
        viewer.loadUrl(prefix+file);
    }
    private void setAnalytics(boolean allow){prefs.edit().putBoolean("analytics",allow).apply();analytics.setAnalyticsCollectionEnabled(allow);}
    public final class Bridge {
        @JavascriptInterface public void showAd(String kind,int id){runOnUiThread(()->show(kind,id));}
        @JavascriptInterface public void setBannerVisible(boolean visible){runOnUiThread(()->{gameVisible=visible;syncBanner();if(visible)ensureBanner();});}
        @JavascriptInterface public void privacy(){runOnUiThread(()->MainActivity.this.privacy());}
        @JavascriptInterface public boolean isReleaseBuild(){return !BuildConfig.DEBUG;}
        @JavascriptInterface public void event(String name,int level,int value){if(!EVENTS.contains(name)||level<1||level>500)return;runOnUiThread(()->{if(!prefs.getBoolean("analytics",false))return;Bundle b=new Bundle();b.putString("level_name",Integer.toString(level));b.putLong("level_number",level);if(name.equals("hint_used"))b.putString("hint_type",value==1?"rewarded":"free");if(name.equals("level_end"))b.putLong("success",1);if(name.equals("level_duration"))b.putLong("duration_seconds",Math.max(0,value));analytics.logEvent(name,b);});}
    }
    @Override protected void onResume(){super.onResume();resumed=true;applyDeviceOrientation();if(web!=null)web.onResume();if(banner!=null)banner.resume();syncBanner();}
    @Override protected void onPause(){resumed=false;syncBanner();if(banner!=null)banner.pause();if(web!=null)web.onPause();super.onPause();}
    @Override protected void onDestroy(){destroyBanner();if(web!=null){web.removeJavascriptInterface("RopeNative");web.destroy();web=null;}super.onDestroy();}
}
