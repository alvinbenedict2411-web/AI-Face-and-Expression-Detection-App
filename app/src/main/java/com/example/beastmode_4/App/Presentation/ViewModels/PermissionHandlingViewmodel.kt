package com.example.beastmode_4.App.Presentation.ViewModels

import android.Manifest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PermissionHandlingViewmodel: ViewModel() {
    var permissions=arrayOf(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO)
        private set
    var permssions_granted by mutableStateOf(false)
        private set

    var show_dialog by mutableStateOf(false)

    fun changevalue(value:Boolean){
        permssions_granted=value
    }

    fun showdialog(value:Boolean){
        show_dialog=value
    }

}