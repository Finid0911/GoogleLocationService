package com.example.googlelocationservice.map.presentation.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.googlelocationservice.map.presentation.viewmodels.MainViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MainScreen(mainViewModel: MainViewModel, navController: NavHostController) {

//    var location by remember {
//        mutableStateOf(LatLng(0.0, 0.0))
//    }
//    var showMap by remember { mutableStateOf(false) }
//    val currentContext = LocalContext.current
//
//    LaunchedEffect(true) {
//        mainViewModel.getCurrentLocation(currentContext) {
//            location = it
//            showMap = true
//        }
//    }

    val location by mainViewModel.location.collectAsState()
    val showMap by mainViewModel.showMap.collectAsState()

    if (showMap) {
        location?.let { MapContent(location = it) }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                Text(text = "Main screen", modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading map screen...",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Composable
fun MapContent(location: LatLng) {
    val sampleLocation = LatLng(location.latitude, location.longitude)
    val state = MarkerState(position = sampleLocation)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sampleLocation, 16f)
    }
    Toast.makeText(
        LocalContext.current, "${location.latitude} + ${location.longitude}", Toast.LENGTH_LONG
    ).show()
    Log.d("Sample Location *****", sampleLocation.toString())

    GoogleMap(modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = {
        }) {
        Marker(
            state = state.also { markerState ->
                Log.d("Marker State *****", markerState.position.toString())
            },
            title = "my Marker",
            draggable = true,
            snippet = "Your current location",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )

    }
}