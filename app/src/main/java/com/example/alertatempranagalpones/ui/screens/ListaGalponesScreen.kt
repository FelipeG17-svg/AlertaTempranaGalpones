package com.example.alertatempranagalpones.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alertatempranagalpones.ui.components.GalponCard
import com.example.alertatempranagalpones.viewmodel.GalponesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaGalponesScreen(
    onGalponClick: (Long) -> Unit,
    viewModel: GalponesViewModel = viewModel()
) {
    val galpones by viewModel.galpones.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Galpones") })
        }
    ) { innerPadding ->
        if (cargando) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(galpones, key = { it.id }) { galpon ->
                    GalponCard(galpon = galpon, onClick = { onGalponClick(galpon.id) })
                }
            }
        }
    }
}

/* @Composable
fun ListaGalponesScreen(onGalponClick: (Long) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lista de galpones", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = { onGalponClick(1L) }, modifier = Modifier.padding(top = 24.dp)) {
            Text("Ver galpón 1")
        }
    }
}*/