package com.example.mangavision.presentation.screens.facerecognition

import android.Manifest
import android.content.Context
import android.graphics.Rect
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRectF
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceRecognitionScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val faceInRect = remember { mutableStateOf(false) }
    val faceBox = remember { mutableStateOf<Rect?>(null) }
    val previewSize = remember { mutableStateOf(Size(0, 0)) }

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setTargetResolution(Size(480, 640))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(ContextCompat.getMainExecutor(ctx), FaceAnalyzer(
                                    onFaceDetected = { rect, size ->
                                        faceBox.value = rect
                                        previewSize.value = size
                                        faceInRect.value = isFaceInReferenceRect(rect, size)
                                    }
                                ))
                            }
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            preview,
                            imageAnalyzer
                        )
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
            // Draw overlay
            FaceOverlay(
                faceBox = faceBox.value,
                previewSize = previewSize.value,
                faceInRect = faceInRect.value
            )
        } else {
            Text(
                text = "Camera permission required",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private fun isFaceInReferenceRect(faceRect: Rect?, previewSize: Size): Boolean {
    if (faceRect == null || previewSize.width == 0 || previewSize.height == 0) return false
    // Reference rectangle: center 50% of the preview
    val refRect = Rect(
        (previewSize.width * 0.25).toInt(),
        (previewSize.height * 0.25).toInt(),
        (previewSize.width * 0.75).toInt(),
        (previewSize.height * 0.75).toInt()
    )
    return refRect.contains(faceRect)
}

private class FaceAnalyzer(
    val onFaceDetected: (Rect?, Size) -> Unit
) : ImageAnalysis.Analyzer {
    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
    )

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    val faceRect = faces.firstOrNull()?.boundingBox
                    onFaceDetected(faceRect, Size(image.width, image.height))
                }
                .addOnFailureListener {
                    onFaceDetected(null, Size(imageProxy.width, imageProxy.height))
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}

@Composable
fun FaceOverlay(
    faceBox: Rect?,
    previewSize: Size,
    faceInRect: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw reference rectangle (center 50% of the preview)
        val refLeft = size.width * 0.25f
        val refTop = size.height * 0.25f
        val refRight = size.width * 0.75f
        val refBottom = size.height * 0.75f
        drawRect(
            color = if (faceInRect) Color.Green else Color.Red,
            topLeft = Offset(refLeft, refTop),
            size = androidx.compose.ui.geometry.Size(refRight - refLeft, refBottom - refTop),
            style = Stroke(width = 6f)
        )
        // Draw face bounding box if detected
        faceBox?.let {
            val scaleX = size.width / previewSize.width
            val scaleY = size.height / previewSize.height
            drawRect(
                color = Color.Yellow,
                topLeft = Offset(it.left * scaleX, it.top * scaleY),
                size = androidx.compose.ui.geometry.Size(
                    (it.right - it.left) * scaleX,
                    (it.bottom - it.top) * scaleY
                ),
                style = Stroke(width = 4f)
            )
        }
    }
}