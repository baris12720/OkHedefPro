package com.helalguard.smart

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import org.json.JSONObject

/**
 * Hosts the HelalGuard web UI (assets/) in a WebView and exposes a tiny
 * native bridge ("Native") so the UI can start the camera scanner.
 * The web UI and the rule engine do all halal/haram logic; native code only
 * scans barcodes and reads text from a photo (on device, ML Kit).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var web: WebView

    private val scanLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            val data = res.data
            val payload = JSONObject()
            if (res.resultCode == Activity.RESULT_OK && data != null) {
                payload.put("ok", true)
                payload.put("kind", data.getStringExtra(ScannerActivity.EXTRA_MODE) ?: "")
                payload.put("value", data.getStringExtra(ScannerActivity.EXTRA_VALUE) ?: "")
            } else {
                payload.put("ok", false)
                payload.put("error", data?.getStringExtra(ScannerActivity.EXTRA_ERROR) ?: "cancelled")
            }
            val js = "window.onNativeResult && window.onNativeResult(" +
                JSONObject.quote(payload.toString()) + ")"
            web.evaluateJavascript(js, null)
        }

    inner class Bridge {
        @JavascriptInterface
        fun scan(mode: String) {
            runOnUiThread {
                val intent = Intent(this@MainActivity, ScannerActivity::class.java)
                intent.putExtra(ScannerActivity.EXTRA_MODE, if (mode == "ocr") "ocr" else "barcode")
                scanLauncher.launch(intent)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        web = WebView(this)
        setContentView(web)

        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = false
        web.settings.allowContentAccess = false
        web.addJavascriptInterface(Bridge(), "Native")

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        web.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }

        web.loadUrl("https://appassets.androidplatform.net/assets/index.html")
    }

    override fun onDestroy() {
        web.removeJavascriptInterface("Native")
        super.onDestroy()
    }
}
