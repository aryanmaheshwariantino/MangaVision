package com.example.mangavision.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangavision.util.FaceDetectorHelper
import com.example.mangavision.util.toBitmap
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FaceDetectionResult(
    val faceRect: Rect?,
    val isInTarget: Boolean
)

@HiltViewModel
class FaceRecognitionViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), FaceDetectorHelper.FaceDetectorListener {

    private val _faceDetectionResult = MutableStateFlow<FaceDetectionResult?>(null)
    val faceDetectionResult: StateFlow<FaceDetectionResult?> = _faceDetectionResult

    private var faceDetectorHelper: FaceDetectorHelper? = null

    init {
        initializeFaceDetector()
    }

    private fun initializeFaceDetector() {
        faceDetectorHelper = FaceDetectorHelper(context, this)
    }

    fun processImage(imageProxy: ImageProxy) {
        viewModelScope.launch {
            try {
                val bitmap = imageProxy.toBitmap()
                faceDetectorHelper?.detectAsync(bitmap, System.currentTimeMillis())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                imageProxy.close()
            }
        }
    }

    override fun onFaceDetectionResult(result: FaceDetectorResult) {
        if (result.detections().isEmpty()) {
            _faceDetectionResult.value = FaceDetectionResult(null, false)
            return
        }

        val face = result.detections()[0]
        val boundingBox = face.boundingBox()

        // Convert MediaPipe bounding box to Compose Rect
        val faceRect = Rect(
            left = boundingBox.left.toFloat(),
            top = boundingBox.top.toFloat(),
            right = boundingBox.right.toFloat(),
            bottom = boundingBox.bottom.toFloat()
        )

        // Define target area (center of screen)
        val targetRect = Rect(
            left = 0.4f * faceRect.width,
            top = 0.4f * faceRect.height,
            right = 0.6f * faceRect.width,
            bottom = 0.6f * faceRect.height
        )

        // Check if face is within target area
        val isInTarget = faceRect.overlaps(targetRect)

        _faceDetectionResult.value = FaceDetectionResult(faceRect, isInTarget)
    }

    override fun onCleared() {
        super.onCleared()
        faceDetectorHelper?.close()
    }
}