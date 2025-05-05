package com.example.mangavision.util

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import com.google.mediapipe.framework.image.BitmapImageBuilder

class FaceDetectorHelper(
    private val context: Context,
    private val listener: FaceDetectorListener
) {
    private var faceDetector: FaceDetector? = null

    init {
        setupFaceDetector()
    }

    private fun setupFaceDetector() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("face_detection_short_range.tflite")
            .build()

        val options = FaceDetector.FaceDetectorOptions.builder()
            .setBaseOptions(baseOptions)
            .setMinDetectionConfidence(0.5f)
            .setMinSuppressionThreshold(0.3f)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                listener.onFaceDetectionResult(result)
            }
            .build()

        faceDetector = FaceDetector.createFromOptions(context, options)
    }

    fun detectAsync(image: Bitmap, timestamp: Long) {
        val mpImage = BitmapImageBuilder(image).build()
        faceDetector?.detectAsync(mpImage, timestamp)
    }

    fun close() {
        faceDetector?.close()
    }

    interface FaceDetectorListener {
        fun onFaceDetectionResult(result: FaceDetectorResult)
    }
} 