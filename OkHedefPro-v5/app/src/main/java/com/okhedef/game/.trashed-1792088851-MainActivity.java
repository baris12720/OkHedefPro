package com.okhedef.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
  private WebView web;
  @Override public void onCreate(Bundle b){ super.onCreate(b); fullscreen(); web=new WebView(this); WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setMediaPlaybackRequiresUserGesture(false); s.setBuiltInZoomControls(false); web.setWebViewClient(new WebViewClient()); setContentView(web); web.loadUrl("file:///android_asset/index.html"); }
  private void fullscreen(){ getWindow().setFlags(1024,1024); getWindow().getDecorView().setSystemUiVisibility(5894|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); }
  @Override public void onWindowFocusChanged(boolean h){super.onWindowFocusChanged(h);if(h)fullscreen();}
  @Override protected void onResume(){super.onResume();if(web!=null)web.onResume();}
  @Override protected void onPause(){if(web!=null)web.onPause();super.onPause();}
  @Override protected void onDestroy(){if(web!=null)web.destroy();super.onDestroy();}
}
