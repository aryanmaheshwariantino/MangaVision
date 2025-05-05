package com.example.mangavision.util

import android.util.Log

object Logging {
    private const val TAG = "MangaVision"

    fun logApiSuccess(endpoint: String, response: String) {
        Log.d(TAG, "✅ API Success - $endpoint")
        Log.d(TAG, "Response: $response")
    }

    fun logApiError(endpoint: String, error: String) {
        Log.e(TAG, "❌ API Error - $endpoint")
        Log.e(TAG, "Error: $error")
    }

    fun logApiRequest(endpoint: String, params: Map<String, Any?> = emptyMap()) {
        Log.d(TAG, "🚀 API Request - $endpoint")
        if (params.isNotEmpty()) {
            val nonNullParams = params.filterValues { it != null }
            if (nonNullParams.isNotEmpty()) {
                Log.d(TAG, "Parameters: $nonNullParams")
            }
        }
    }
} 