package com.example.medicana.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.medicana.R
import com.example.medicana.SHARED_PREFS
import com.example.medicana.util.navController
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.qrcode.QRCodeMultiReader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_scanner.*
import kotlinx.android.synthetic.main.layout_need_auth.*
import kotlinx.android.synthetic.main.layout_scanner.*
import java.nio.ByteBuffer
import java.util.concurrent.ExecutionException


class ScannerFragment : Fragment() {

    private lateinit var act: Activity
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var qrCode: String? = null
    private val PERMISSION_REQUEST_CAMERA = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()

        cameraProviderFuture = ProcessCameraProvider.getInstance(act)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val connected =
            (act.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE))
                .getBoolean("connected", false)
        return if (connected) {
            inflater.inflate(R.layout.fragment_scanner, container, false)
        } else {
            inflater.inflate(R.layout.layout_need_auth, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.VISIBLE

        the_scanner?.setOnClickListener {
            requestCamera()
        }

        need_auth_button?.setOnClickListener {
            navController(act).navigate(R.id.action_nav_host_to_authFragment)
        }
    }

    private fun requestCamera() {
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
            } else {
                ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
                scanner_preview?.visibility = View.VISIBLE
            } else {
                Toast.makeText(act, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                bindCameraPreview(cameraProvider)
            } catch (e: ExecutionException) {
                Toast.makeText(act, "Error starting camera " + e.message, Toast.LENGTH_SHORT).show()
            } catch (e: InterruptedException) {
                Toast.makeText(act, "Error starting camera " + e.message, Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(act))
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        scanner_preview?.preferredImplementationMode = PreviewView.ImplementationMode.SURFACE_VIEW

        val preview = Preview.Builder()
                .build()

        val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

        preview.setSurfaceProvider(scanner_preview?.createSurfaceProvider())

        val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(act), QRCodeImageAnalyzer(object : QRCodeFoundListener {
            override fun onQRCodeFound(_qrCode: String?) {
                qrCode = _qrCode
                Toast.makeText(act, "Found QR code : " + qrCode, Toast.LENGTH_LONG).show()
            }

            override fun qrCodeNotFound() {}
        }))

        val camera = cameraProvider.bindToLifecycle((this as LifecycleOwner)!!, cameraSelector, imageAnalysis, preview)

    }

    interface QRCodeFoundListener {
        fun onQRCodeFound(qrCode: String?)
        fun qrCodeNotFound()
    }

    class QRCodeImageAnalyzer(private val listener: QRCodeFoundListener) : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            if (image.format == YUV_420_888 || image.format == YUV_422_888 || image.format == YUV_444_888) {
                val byteBuffer: ByteBuffer = image.planes[0].buffer
                val imageData = ByteArray(byteBuffer.capacity())
                byteBuffer.get(imageData)
                val source = PlanarYUVLuminanceSource(
                        imageData,
                        image.width, image.height,
                        0, 0,
                        image.width, image.height,
                        false
                )
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                try {
                    val result = QRCodeMultiReader().decode(binaryBitmap)
                    listener.onQRCodeFound(result.text)
                } catch (e: FormatException) {
                    listener.qrCodeNotFound()
                } catch (e: ChecksumException) {
                    listener.qrCodeNotFound()
                } catch (e: NotFoundException) {
                    listener.qrCodeNotFound()
                }
            }
            image.close()
        }
    }

}