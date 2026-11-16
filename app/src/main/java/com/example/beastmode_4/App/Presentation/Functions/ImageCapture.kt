package com.example.beastmode_4.App.Presentation.Functions

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beastmode_4.App.Presentation.ViewModels.CameraScreenViewmodel

fun ImageCaptureFun(Imagecapture: ImageCapture,context: Context){
    val contentResolver=context.contentResolver
    val contentvalues= ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME,"Photo${System.currentTimeMillis()}")
        put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/CameraX-Image")
    }
    val outputfile= ImageCapture.OutputFileOptions.Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentvalues).build()
    Imagecapture.takePicture(
        outputfile,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(context, "Photo saved", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Photo not saved", Toast.LENGTH_SHORT).show()
            }
        }
    )
}