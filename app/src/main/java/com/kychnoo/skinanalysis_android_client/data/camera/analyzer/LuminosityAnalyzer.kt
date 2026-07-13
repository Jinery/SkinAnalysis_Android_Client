package com.kychnoo.skinanalysis_android_client.data.camera.analyzer

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

/**
 * Exposure analyzer.
 * Used to detect shooting conditions that are too dark.
 */
class LuminosityAnalyzer(
    private val onLuminosityChanged: (Double) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer // Get Y (Luma) channel.
        buffer.rewind() // Reset position to start of buffer.

        val size = buffer.remaining() // All pixels in buffer.
        var sum = 0L // Sum of all luminosities.

        while (buffer.hasRemaining()) {
            // Convert from signed bytes(-128..127) to unsigned(0..255) and sum them.
            sum += buffer.get().toInt() and 0xFF
        }

        val luminosity = if (size > 0) sum.toDouble() / size else 0.0 // Get average luminosity.

        onLuminosityChanged(luminosity) // Notify observer with calculated luminosity.
        image.close()
    }
}
