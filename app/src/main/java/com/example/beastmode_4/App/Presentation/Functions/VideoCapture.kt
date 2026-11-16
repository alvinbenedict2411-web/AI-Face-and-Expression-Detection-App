package com.example.beastmode_4.App.Presentation.Functions

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat

@RequiresPermission(Manifest.permission.RECORD_AUDIO)
fun VideoCapture(context: Context, Videocapture:VideoCapture<Recorder>,recorder: Recorder): Recording{

    val contentResolver= context.contentResolver

    val contentvalues= ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, "Video_${System.currentTimeMillis()}.mp4")
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
    }

    val MediastoreOutputFiles= MediaStoreOutputOptions.Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).setContentValues(contentvalues).build()

    val pendingrecording=Videocapture.output.prepareRecording(context, MediastoreOutputFiles).withAudioEnabled()

    return pendingrecording.start(ContextCompat.getMainExecutor(context)){
        event->
        when(event){
            is VideoRecordEvent.Start->{
                Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
            }
        }
    }
}