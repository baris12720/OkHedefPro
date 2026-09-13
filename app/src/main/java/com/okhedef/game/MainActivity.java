package com.okhedef.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Color;

public class MainActivity extends Activity {
    private WebView web;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(7,11,22));
        getWindow().setNavigationBarColor(Color.rgb(7,11,22));
        web = new WebView(this);
        web.setBackgroundColor(Color.rgb(7,11,22));
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setDatabaseEnabled(true);
        s.setAllowFileAccess(false); s.setAllowContentAccess(false);
        s.setBuiltInZoomControls(false); s.setDisplayZoomControls(false);
        s.setMediaPlaybackRequiresUserGesture(false);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }
    @Override public void onBackPressed() {
        if (web != null) web.evaluateJavascript("window.gamePause && gamePause()", null);
        else super.onBackPressed();
    }
    @Override protected void onDestroy(){ if(web!=null) web.destroy(); super.onDestroy(); }
}
