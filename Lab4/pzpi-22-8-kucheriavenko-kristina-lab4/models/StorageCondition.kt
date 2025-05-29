package com.example.myapplication.models

import java.util.Date

data class StorageCondition(
    var recordID: Long,
    val temperature: Double,
    val oxygenLevel: Double,
    val humidity: Double,
    val measurementTime: Date,
    val materialID: BiologicalMaterial
)