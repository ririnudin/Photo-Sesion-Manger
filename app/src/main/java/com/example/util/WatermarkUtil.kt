package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WatermarkUtil {

    /**
     * Process photo file with watermark and quality settings if needed.
     */
    fun processPhoto(
        context: Context,
        inputPath: String,
        watermarkEnabled: Boolean,
        watermarkText: String,
        uploadQuality: String // "ORIGINAL", "HIGH", "COMPRESSED"
    ): File {
        val originalFile = File(inputPath)
        if (!originalFile.exists()) return originalFile

        // If no watermark and original quality, return original file directly
        if (!watermarkEnabled && uploadQuality == "ORIGINAL") {
            return originalFile
        }

        try {
            val options = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeFile(inputPath, options) ?: return originalFile

            var processedBitmap = bitmap

            // Apply Watermark if enabled
            if (watermarkEnabled) {
                processedBitmap = addWatermark(processedBitmap, watermarkText)
            }

            // Quality compression factor
            val qualityPercentage = when (uploadQuality) {
                "COMPRESSED" -> 60
                "HIGH" -> 85
                else -> 100
            }

            // Save to cached processed file
            val outputDir = File(context.cacheAreaOrFilesDir(), "processed_photos")
            if (!outputDir.exists()) outputDir.mkdirs()

            val outputFile = File(outputDir, "proc_${System.currentTimeMillis()}_${originalFile.name}")
            FileOutputStream(outputFile).use { out ->
                processedBitmap.compress(Bitmap.CompressFormat.JPEG, qualityPercentage, out)
            }

            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            return originalFile
        }
    }

    private fun Context.cacheAreaOrFilesDir(): File {
        return cacheDir ?: filesDir
    }

    private fun addWatermark(src: Bitmap, text: String): Bitmap {
        val width = src.width
        val height = src.height

        val result = Bitmap.createBitmap(width, height, src.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        // Draw original image
        canvas.drawBitmap(src, 0f, 0f, null)

        // Watermark Paint Setup
        val paint = Paint().apply {
            color = Color.WHITE
            alpha = 180 // Semi-transparent
            textSize = (height * 0.035f).coerceAtLeast(32f)
            isAntiAlias = true
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
        }

        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val fullText = "$text • $timestamp"

        val bounds = Rect()
        paint.getTextBounds(fullText, 0, fullText.length, bounds)

        // Bottom right corner padding
        val padding = (width * 0.04f).coerceAtLeast(24f)
        val x = width - bounds.width() - padding
        val y = height - padding

        canvas.drawText(fullText, x, y, paint)

        return result
    }
}
