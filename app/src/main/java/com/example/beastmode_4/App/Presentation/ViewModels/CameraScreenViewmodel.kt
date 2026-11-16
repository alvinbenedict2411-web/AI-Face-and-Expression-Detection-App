package com.example.beastmode_4.App.Presentation.ViewModels


import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList

import androidx.lifecycle.ViewModel
import com.example.beastmode_4.App.Presentation.Clases.FaceBoxes
import java.util.concurrent.Executors

class CameraScreenViewmodel: ViewModel() {

    var camera_provider by mutableStateOf<ProcessCameraProvider?>(null)
        private set

    fun set_camera_provider(value: ProcessCameraProvider) {
        camera_provider = value
    }

    var executer= Executors.newSingleThreadExecutor()
        private set

    var preview = Preview.Builder().build()
        private set

    var camera_selector by mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
        private set

    fun change_camera() {
        if (camera_selector == CameraSelector.DEFAULT_BACK_CAMERA) {
            camera_selector = CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            camera_selector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    var Imagecapture = ImageCapture.Builder().build()
        private set

    private val quality= QualitySelector.fromOrderedList(
        listOf(Quality.FHD, Quality.HD, Quality.SD),
        FallbackStrategy.higherQualityOrLowerThan(Quality.FHD)
    )

    val recorder= Recorder.Builder().setQualitySelector(quality).build()

    var video_capture = VideoCapture.withOutput(recorder)
        private set

    var recording by mutableStateOf<Recording?>(null)
        private set

    fun recording_Set_to_Null(){
        recording=null
    }

    fun updateRecording(recording: Recording){
        this.recording=recording
    }

    var Imageanalysis= ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
        private set

   /* var expression by mutableStateOf<String?>(null)
        private set

    fun expresionvalue(value:String){
        expression=value
    } no need as we no longer storing only one expression */

    var results by mutableStateOf<List<FaceBoxes>?>(null)
        private set

    fun updateresults(newresult:List<FaceBoxes>){
        results=newresult
    }

    fun setresult_to_null(){
        results=null
    }

    }