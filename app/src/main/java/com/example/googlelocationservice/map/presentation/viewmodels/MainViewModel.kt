package com.example.googlelocationservice.map.presentation.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlelocationservice.Constants
import com.example.googlelocationservice.map.domain.model.Coordinates
import com.example.googlelocationservice.map.domain.usecase.CoordinatesUseCase
import com.example.googlelocationservice.map.domain.util.CoordinatesOrder
import com.example.googlelocationservice.map.domain.util.OrderType
import com.example.googlelocationservice.map.domain.util.getCurrentDateTime
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("DEPRECATION")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val context: Context,
    private val coordinatesUseCase: CoordinatesUseCase
) : ViewModel() {

    private val _location = MutableStateFlow(LatLng(0.0, 0.0))

    private val _showMap = MutableStateFlow(false)

    private val _locations = MutableStateFlow<Polylines>(mutableListOf())

    private var isTracking = MutableStateFlow(false)

    init {
        getCurrentLocation()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        viewModelScope.launch {
            var loc: LatLng

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    loc = LatLng(latitude, longitude)
                    _location.value = loc
                    _showMap.value = true
                    addCoordinates(Coordinates(latitude, longitude, getCurrentDateTime()))
                }
            }.addOnFailureListener { exception: Exception ->
                Log.d("MAP-EXCEPTION", exception.message.toString())
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        viewModelScope.launch {
            if (isTracking.value) {
                val locationRequest = LocationRequest().apply {
                    interval = Constants.LOCATION_UPDATE_INTERVAL
                    fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    context.mainLooper
                ).addOnSuccessListener {
                    Log.d("MAP-LOCATION", "Location updates started")
                }.addOnFailureListener { exception: Exception ->
                    Log.d("MAP-EXCEPTION", exception.message.toString())
                }
            } else {
                Log.d("IS_TRACKING", "isTracking variable has failed!")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun stopLocationUpdates() {
        viewModelScope.launch {
            fusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("MAP-LOCATION", "Location updates stopped.")
                    } else {
                        Log.d("MAP-LOCATION", "Failed to stop location updates.")
                    }
                }
        }

        // check room database
        val data: Flow<List<Coordinates>> =
            coordinatesUseCase.getCoordinates(CoordinatesOrder.id(OrderType.Ascending))
                .onEach { data ->
                    if (data.isEmpty()) {
                        Log.d("COORDINATES_GET", "IS EMPTY!")
                    } else {
                        Log.d("COORDINATES_GET", "NOT EMPTY!")
                    }
                }
        viewModelScope.launch {
            data.collect()
        }
    }

    private val locationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val newPositions = locationResult.locations.map { location ->
                val currentLocation = LatLng(location.latitude, location.longitude)
                addCoordinates(
                    Coordinates(
                        location.latitude,
                        location.longitude,
                        getCurrentDateTime()
                    )
                )
                Log.d("UPDATE-LOCATION: ", "${location.latitude}, ${location.longitude}")
                currentLocation
            }

            if (newPositions.isNotEmpty()) {
                viewModelScope.launch {
                    val updatedLocation =
                        _locations.value.plus(mutableListOf(newPositions)) as Polylines
                    _locations.tryEmit(updatedLocation)
                }
            } else {
                Log.d("NEW_POSITIONS", "null!")
            }
        }
    }

    fun addCoordinates(coordinates: Coordinates) {
        viewModelScope.launch {
            coordinatesUseCase.insertCoordinate(coordinates)
        }
    }

    fun clearPathPoints() {
        viewModelScope.launch {
            _locations.emit(mutableListOf())
        }
    }

    fun getLocation(): MutableStateFlow<LatLng> {
        return _location
    }

    fun shouldShowMap(): MutableStateFlow<Boolean> {
        return _showMap
    }

    fun getPathPoints(): StateFlow<Polylines> {
        return _locations
    }

    fun getIsTracking(): MutableStateFlow<Boolean> {
        return isTracking
    }

    fun setIsTracking(check: Boolean) {
        isTracking.value = check
    }

}
