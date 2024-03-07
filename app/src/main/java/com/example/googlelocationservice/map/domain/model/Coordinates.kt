package com.example.googlelocationservice.map.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Coordinates(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val lastCreated: String
)
