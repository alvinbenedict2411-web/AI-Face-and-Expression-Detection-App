package com.example.beastmode_4.App.Presentation.Screens

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beastmode_4.App.Presentation.ViewModels.PermissionHandlingViewmodel


@Composable
fun PermissionHandling(viewmodel: PermissionHandlingViewmodel=viewModel()) {

    val context = LocalContext.current

    val permission_launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        viewmodel.changevalue(RecivedPermissions(viewmodel, context))
        viewmodel.showdialog(!viewmodel.permssions_granted)
    }

    if (viewmodel.show_dialog) {
        AlertDialog(
            onDismissRequest = {
                viewmodel.showdialog(false)
                permission_launcher.launch(viewmodel.permissions)
            }, confirmButton = {
                Button(onClick = {
                    viewmodel.showdialog(false)
                    permission_launcher.launch(viewmodel.permissions)
                }) {
                    Text(text = "Ok")
                }
            },
            dismissButton = {
                Button(onClick = {
                    viewmodel.showdialog(false)
                    permission_launcher.launch(viewmodel.permissions)
                }) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(text = "Permission Required")
            },
            text = {
                Text(text = "Permissions Required to use this app")
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    LaunchedEffect(viewmodel.permssions_granted) {
        if(!viewmodel.permssions_granted){
            permission_launcher.launch(viewmodel.permissions)
        }
    }
    if(viewmodel.permssions_granted){
        CameraScreen()
    }
}
fun RecivedPermissions(viewmodel: PermissionHandlingViewmodel, context: Context):Boolean{
    return viewmodel.permissions.all{
        ContextCompat.checkSelfPermission(context,
            it)== PackageManager.PERMISSION_GRANTED
    }
}
