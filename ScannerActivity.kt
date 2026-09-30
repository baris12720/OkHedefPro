package com.helalguard.smart

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Full-screen camera. mode = "barcode": returns the first EAN/UPC value seen.
 * mode = "ocr": user taps the shutter, the photo is read on-device with ML Kit
 * text recognition and the raw text is returned. No image ever leaves the phone.
 */
class ScannerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "mode"
        const val EXTRA_VALUE = "value"
        const val EXTRA_ERROR = "error"
    }

    private lateinit var previewView: PreviewView
    private lateinit var mode: String
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var finished = false
    private var busy = false

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera() else finishWithError("permission")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = intent.getStringExtra(EXTRA_MODE) ?: "barcode"
        cameraExecutor = Executors.newSingleThreadExecutor()
        buildUi()

        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) startCamera() else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val root = FrameLayout(this)
        root.setBackgroundColor(Color.BLACK)

        previewView = PreviewView(this)
        root.addView(
            previewView,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )

        val hint = TextView(this)
        hint.text = if (mode == "ocr") {
            "İçindekiler yazısını çerçeveye al, net görünce fotoğraf çek"
        } else {
            "Ürün barkodunu kameraya göster"
        }
        hint.setTextColor(Color.WHITE)
        hint.setBackgroundColor(Color.parseColor("#99000000"))
        hint.textSize = 15f
        hint.gravity = Gravity.CENTER
        hint.setPadding(dp(16), dp(14), dp(16), dp(14))
        root.addView(
            hint,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP)
        )

        val cancel = Button(this)
        cancel.text = "İptal"
        cancel.setOnClickListener { finishWithError("cancelled") }
        val cancelLp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM or Gravity.START
        )
        cancelLp.setMargins(dp(20), 0, 0, dp(28))
        root.addView(cancel, cancelLp)

        if (mode == "ocr") {
            val shutter = Button(this)
            shutter.text = "Fotoğraf çek"
            shutter.setTypeface(shutter.typeface, Typeface.BOLD)
            shutter.setOnClickListener { takePhoto() }
            val shutterLp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.END
            )
            shutterLp.setMargins(0, 0, dp(20), dp(28))
            root.addView(shutter, shutterLp)
        }

        setContentView(root)
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            provider.unbindAll()

            if (mode == "ocr") {
                val capture = ImageCapture.Builder().build()
                imageCapture = capture
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, capture)
            } else {
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E
                    )
                    .build()
                val scanner = BarcodeScanning.getClient(options)
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                analysis.setAnalyzer(cameraExecutor) { proxy -> analyzeBarcode(proxy, scanner) }
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyzeBarcode(proxy: ImageProxy, scanner: BarcodeScanner) {
        if (finished) {
            proxy.close()
            return
        }
        val bitmap = proxy.toBitmap()
        val image = InputImage.fromBitmap(bitmap, proxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { list ->
                val value = list.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue
                if (value != null) finishWithValue(value)
            }
            .addOnCompleteListener { proxy.close() }
    }

    private fun takePhoto() {
        val capture = imageCapture ?: return
        if (busy || finished) return
        busy = true
        capture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = image.toBitmap()
                val rotation = image.imageInfo.rotationDegrees
                image.close()
                val input = InputImage.fromBitmap(bitmap, rotation)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(input)
                    .addOnSuccessListener { result -> finishWithValue(result.text) }
                    .addOnFailureListener {
                        busy = false
                        finishWithError("ocr")
                    }
            }

            override fun onError(exception: ImageCaptureException) {
                busy = false
                finishWithError("capture")
            }
        })
    }

    private fun finishWithValue(value: String) {
        runOnUiThread {
            if (finished) return@runOnUiThread
            finished = true
            val data = Intent()
            data.putExtra(EXTRA_MODE, mode)
            data.putExtra(EXTRA_VALUE, value)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    private fun finishWithError(code: String) {
        runOnUiThread {
            if (finished) return@runOnUiThread
            finished = true
            val data = Intent()
            data.putExtra(EXTRA_ERROR, code)
            setResult(Activity.RESULT_CANCELED, data)
            finish()
        }
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }
}
