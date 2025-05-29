package com.example.myapplication.models

import com.example.myapplication.enums.DonationStatus
import java.util.Date

data class BiologicalMaterial(
    var materialID: Long,
    val materialName: String,
    val expirationDate: Date,
    val status: DonationStatus,
    val transferDate: Date,
    val donor: Donor
)
