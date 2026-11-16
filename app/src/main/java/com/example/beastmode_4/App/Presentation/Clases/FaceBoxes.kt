package com.example.beastmode_4.App.Presentation.Clases

import android.graphics.Rect
//this is used to store the bounding box of the face and the expression of the face
data class FaceBoxes(
    val box: Rect,
    val expression: String
)
