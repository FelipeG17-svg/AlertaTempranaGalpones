package com.example.alertatempranagalpones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.alertatempranagalpones.navigation.AppNavigation
import com.example.alertatempranagalpones.ui.theme.AlertaTempranaGalponesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pedirPermisoRedLocal()
        enableEdgeToEdge()
        setContent {
            AlertaTempranaGalponesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
    private fun pedirPermisoRedLocal() {
        val permiso = "android.permission.ACCESS_LOCAL_NETWORK"
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, permiso)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            androidx.core.app.ActivityCompat.requestPermissions(this, arrayOf(permiso), 1001)
        }
    }
}