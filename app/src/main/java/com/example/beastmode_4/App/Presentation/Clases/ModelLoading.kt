package com.example.beastmode_4.App.Presentation.Clases

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.video.FileDescriptorOutputOptions
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import org.tensorflow.lite.Interpreter
import java.io.FileDescriptor
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ModelLoading(context: Context) {
    var interpreter: Interpreter

    init {
        val model = context.assets.openFd("modelface.tflite")
        val inputstream = FileInputStream(model.fileDescriptor)
        val modelbuffer = inputstream.channel.map(
            FileChannel.MapMode.READ_ONLY,
            model.startOffset,
            model.declaredLength
        )
        interpreter = Interpreter(modelbuffer)
    }

    fun inputtensorcheck() {
        val inputtensor = interpreter.getInputTensor(0)
        println("Input Shape: ${inputtensor.shape().contentToString()}")
        println("Input Type: ${inputtensor.dataType()}")
    }

    fun outputtensorcheck() {
        for (i in 0 until interpreter.outputTensorCount) {
            val outputtensor = interpreter.getOutputTensor(i)
            println("Output Shape: ${outputtensor.shape().contentToString()}")
            println("Output Type: ${outputtensor.dataType()}")
        }
    }

    fun runtesting(context: Context) {
        val inputstream = context.assets.open("tajmahal.jpg")

        val bitmap = BitmapFactory.decodeStream(inputstream)

        val resizedbitmap = Bitmap.createScaledBitmap(
            bitmap,
            48,
            48,
            true
        )

        val inputbuffer = ByteBuffer.allocateDirect(1 * 48 * 48 * 1 * 4)

        inputbuffer.order(ByteOrder.nativeOrder())

        for (y in 0 until 48) {
            for (x in 0 until 48) {
                val pixel = resizedbitmap.getPixel(x, y)
                val r = pixel.red
                val g = pixel.green
                val b = pixel.blue
                val grayvalue = (0.299f * r +
                        0.587f * g +
                        0.114f * b)
                //we divide by 255f because the model expects values between 0 and 1 and this for normalization
                inputbuffer.putFloat(grayvalue / 255f)
            }
        }

    }

    fun rotatebitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees == 0)
            return bitmap
        val matrix = android.graphics.Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    fun runlivetesting(bitmap: Bitmap, context: Context): String {

        val resizedbitmap = Bitmap.createScaledBitmap(
            bitmap,
            48,
            48,
            true
        )

        val inputbuffer = ByteBuffer.allocateDirect(1 * 48 * 48 * 1 * 4)

        inputbuffer.order(ByteOrder.nativeOrder())

        for (y in 0 until 48) {
            for (x in 0 until 48) {
                val pixel = resizedbitmap.getPixel(x, y)
                val r = pixel.red
                val g = pixel.green
                val b = pixel.blue
                val grayvalue = (0.299f * r +
                        0.587f * g +
                        0.114f * b)
                //we divide by 255f because the model expects values between 0 and 1 and this for normalization
                inputbuffer.putFloat(grayvalue / 255f)
            }
        }
        val output = Array(1) {
            FloatArray(8)
        }
        interpreter.run(inputbuffer, output)
        var best_idx = 0
        var bestscore = output[0][0]
        for (i in 1 until 8) {
            if (output[0][i] > bestscore) {
                bestscore = output[0][i]
                best_idx = i
            }
        }
        val expression = getnames(best_idx)
        return expression
    }

    fun getnames(idx: Int): String {
        val expression = when (idx) {
            0 -> "Neutral"
            1 -> "Happiness"
            2 -> "Surprise"
            3 -> "Sadness"
            4 -> "Anger"
            5 -> "Disgust"
            6 -> "Fear"
            7 -> "Contempt"
            else -> "Unknown"
        }
        return expression
    }
}