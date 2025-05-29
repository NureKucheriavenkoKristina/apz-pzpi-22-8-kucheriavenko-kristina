package com.example.myapplication.models

import java.util.Date

data class Notification(
    var notificationID: Long,
    val eventTime: Date,
    val eventType: String,
    val details: String,
    val status: String,
    val materialID: BiologicalMaterial
)