package com.example.beastmode_4.App.Presentation.Screens

import android.R.attr.textSize
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import android.graphics.Rect
import android.opengl.Matrix
import android.webkit.WebSettings
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beastmode_4.App.Presentation.Clases.FaceBoxes
import com.example.beastmode_4.App.Presentation.Clases.ModelLoading
import com.example.beastmode_4.App.Presentation.ViewModels.CameraScreenViewmodel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.collections.emptyList

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen(viewmodel: CameraScreenViewmodel =viewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current

    val camera_provider_future = remember {
        ProcessCameraProvider.getInstance(context)
    }

    val preview_view = remember {
        PreviewView(context).apply {
            scaleType= PreviewView.ScaleType.FILL_CENTER
        }
    }
    //Model Instances and Modifying Model options
    val face_detection_options= FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE).build()

    val Facedetection=remember {
        FaceDetection.getClient(face_detection_options)
    }

    var scale by remember { mutableStateOf(1f) }

    var horizontalOffset by remember { mutableStateOf(0f) }

    var verticaloffset by remember { mutableStateOf(0f) }

    //Loading model
    val model=remember {
        ModelLoading(context)
    }
   // model.inputtensorcheck()
   // model.outputtensorcheck()

    Scaffold() {innerpadding->
        Box(modifier = Modifier.fillMaxSize()
            .padding(innerpadding)) {

            AndroidView(
                factory = {
                    preview_view
                          },
                modifier = Modifier.fillMaxSize())

            if(viewmodel.results!=null) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val paint = android.graphics.Paint().apply {
                            textSize = 80f
                            isAntiAlias = true
                            color = Color.White.hashCode()
                        }
                        viewmodel.results?.forEach { result ->
                            val left=result.box.left*scale-horizontalOffset
                            val top=result.box.top*scale-verticaloffset
                            val right=result.box.right*scale-horizontalOffset
                            val bottom=result.box.bottom*scale-verticaloffset
                            val width=right-left
                            val height=bottom-top
                            //draw rectangle
                            drawRect(
                                color = Color.Red,
                                topLeft = Offset(
                                    x = left,
                                    y = top
                                ),
                                size = Size(
                                    width = width,
                                    height = height
                                ),
                                style = Stroke(width = 6f)
                            )
                            //write text
                            drawContext.canvas.nativeCanvas.drawText(
                                result.expression,
                                result.box.left * scale - horizontalOffset,
                                result.box.top * scale-10f,
                                paint
                            )
                        }
                    }
            }
            IconButton(onClick = {
                viewmodel.change_camera()
            },modifier = Modifier.safeContentPadding()
                .align(Alignment.TopEnd)) {
                Icon(Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera",
                    tint = Color.White)
            }

            viewmodel.Imageanalysis.setAnalyzer(viewmodel.executer, ImageAnalysis.Analyzer {imageProxy ->
                val image=imageProxy.image
                val templistresult=mutableListOf<FaceBoxes>()
                if(image!=null){
                    val bitmap=imageProxy.toBitmap()
                    val rotation=imageProxy.imageInfo.rotationDegrees
                    val rotatedbitmap=model.rotatebitmap(bitmap,rotation)
                    println("Rotation: ${imageProxy.imageInfo.rotationDegrees}")
                    val Input= InputImage.fromMediaImage(image,imageProxy.imageInfo.rotationDegrees)
                    //for dynamic height and width of camera frame
                    val image_width=if(rotation==90 || rotation==270) image.height else image.width
                    val image_height=if(rotation==90 || rotation==270) image.width else image.height
                    Facedetection.process(Input).addOnSuccessListener {faces->
                        if(faces.size>0) {
                            for (face in faces) {
                                 val box=face.boundingBox
                                //its crashing of we move out of frame so fix is as follows
                                val left=maxOf(0,box.left)
                                val top=maxOf(0,box.top)
                                val right=minOf(image_width,box.right)
                                val bottom=minOf(image_height,box.bottom)
                                val width=right-left
                                val height=bottom-top
                                if(width>0 && height>0){
                        /*        val facebitmap= Bitmap.createBitmap(
                                    rotatedbitmap,
                                    box.left,
                                    box.top,
                                    box.width(),
                                    box.height()
                                ) removed ths bcz out of frame face caused crash as box gave invalid  -ve or larger values than the preview screeen */
                                    /* now for dynamic box which fixes crash*/
                                    val facebitmap=Bitmap.createBitmap(
                                        rotatedbitmap,
                                        left,
                                        top,
                                        width,
                                        height
                                    )
                                val value=model.runlivetesting(facebitmap,context)
                                val tempresult= FaceBoxes(box,value)
                                templistresult.add(tempresult)
                            }
                                }
                            viewmodel.updateresults(templistresult)
                                scale=maxOf(
                                    preview_view.width.toFloat()/image_width,
                                    preview_view.height.toFloat()/image_height
                                )
                                //this is done do adjust horizontal width as it gets longer than the screen so we need to crop
                                val scale_width=image_width*scale
                            val scale_height=image_height*scale
                            verticaloffset=(scale_height-preview_view.height)/2f
                                horizontalOffset=
                                    (scale_width-preview_view.width)/2f
                        }
                        else{
                            viewmodel.setresult_to_null()
                        }
                    }.addOnFailureListener {exception ->
                        println("Error: $exception")
                    }.addOnCompleteListener {
                        imageProxy.close()
                    }
                }
                else{
                    imageProxy.close()
                }
            })

            DisposableEffect(lifecycleOwner,viewmodel.camera_selector){
                camera_provider_future.addListener({
                    viewmodel.set_camera_provider(camera_provider_future.get())
                    viewmodel.camera_provider?.unbindAll()
                    viewmodel.preview.surfaceProvider=preview_view.surfaceProvider
                    viewmodel.camera_provider?.bindToLifecycle(
                        lifecycleOwner,
                        viewmodel.camera_selector,
                        viewmodel.preview,
                        viewmodel.Imagecapture,
                        viewmodel.video_capture,
                        viewmodel.Imageanalysis
                    )
                }, ContextCompat.getMainExecutor(context))
                onDispose {
                    viewmodel.camera_provider?.unbindAll()
                }}
            }
        }
    }
