package com.econect.app.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

data class ClassificationResult(
    val label: String,
    val confidence: Float,
    val isReliable: Boolean
)

@Singleton
class MaterialClassifier @Inject constructor() {

    companion object {
        private const val MODEL_FILE           = "tflite_learn_968811_13.tflite"
        private const val INPUT_SIZE           = 192
        private const val NUM_THREADS          = 4
        private const val CONFIDENCE_THRESHOLD = 0.5f
        private const val INPUT_ZERO_POINT     = -128
        private const val OUT_SCALE            = 1f / 127f
        val LABELS = listOf("glass", "metal", "plastic")
    }

    private var interpreter: Interpreter? = null

    fun initialize(context: Context) {
        if (interpreter != null) return
        try {
            val model = loadModelFile(context)
            val options = Interpreter.Options().apply { numThreads = NUM_THREADS }
            interpreter = Interpreter(model, options)
        } catch (e: IOException) {
            throw RuntimeException("No se pudo cargar $MODEL_FILE", e)
        }
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(MODEL_FILE)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
    }

    suspend fun classify(context: Context, uri: Uri): ClassificationResult =
        withContext(Dispatchers.Default) {
            val interp = requireNotNull(interpreter) { "Llama a initialize() antes de classify()" }
            val bitmap = loadAndResizeBitmap(context, uri)
            val inputBuffer = bitmapToByteBuffer(bitmap)
            val outputBuffer = Array(1) { Array(756) { ByteArray(7) } }
            interp.run(inputBuffer, outputBuffer)
            parseYoloOutput(outputBuffer[0])
        }

    private fun loadAndResizeBitmap(context: Context, uri: Uri): Bitmap {
        val stream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("No se pudo abrir: $uri")
        val original = BitmapFactory.decodeStream(stream)
            ?: throw IOException("No se pudo decodificar la imagen")
        return Bitmap.createScaledBitmap(original, INPUT_SIZE, INPUT_SIZE, true)
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val rgbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
        val buffer = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 3)
            .order(ByteOrder.nativeOrder())
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        rgbBitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)
        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            buffer.put((r + INPUT_ZERO_POINT).coerceIn(-128, 127).toByte())
            buffer.put((g + INPUT_ZERO_POINT).coerceIn(-128, 127).toByte())
            buffer.put((b + INPUT_ZERO_POINT).coerceIn(-128, 127).toByte())
        }
        buffer.rewind()
        return buffer
    }

    private fun parseYoloOutput(raw: Array<ByteArray>): ClassificationResult {
        var bestLabel = LABELS[0]
        var bestScore = 0f

        for (detection in raw) {
            for (j in LABELS.indices) {
                val score = ((detection[4 + j].toInt() - INPUT_ZERO_POINT) * OUT_SCALE)
                    .coerceIn(0f, 1f)
                if (score > bestScore) {
                    bestScore = score
                    bestLabel = LABELS[j]
                }
            }
        }

        return ClassificationResult(
            label = bestLabel,
            confidence = bestScore,
            isReliable = bestScore >= CONFIDENCE_THRESHOLD
        )
    }

    fun release() {
        interpreter?.close()
        interpreter = null
    }
}