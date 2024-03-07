package com.example.googlelocationservice.map.presentation.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _location = MutableStateFlow<LatLng>(LatLng(0.0, 0.0))
    val location: StateFlow<LatLng?> = _location

    private val _showMap = MutableStateFlow(false)
    val showMap: StateFlow<Boolean> = _showMap

    init {
        getCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            var loc: LatLng?

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    loc = LatLng(latitude, longitude)
                    _location.value = loc as LatLng
                    _showMap.value = true
                }
            }.addOnFailureListener { exception: Exception ->
                Log.d("MAP-EXCEPTION", exception.message.toString())
            }
        }
    }

//    @SuppressLint("MissingPermission")
//    fun getCurrentLocation(context: Context, onLocationFetched: (location: LatLng) -> Unit) {
//        viewModelScope.launch {
//            var loc: LatLng
//            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//
//            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                if (location != null) {
//                    val latitude = location.latitude
//                    val longitude = location.longitude
//                    loc = LatLng(latitude, longitude)
//                    onLocationFetched(loc)
//                }
//            }.addOnFailureListener { exception: Exception ->
//                Log.d("MAP-EXCEPTION", exception.message.toString())
//            }
//        }
//    }
}