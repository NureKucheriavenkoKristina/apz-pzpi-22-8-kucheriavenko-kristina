package com.example.myapplication.models

import com.example.myapplication.enums.*
import java.util.Date

data class Donor(
    var donorID: Long,
    val firstName: String,
    val lastName: String,
    val birthDate: Date,
    val gender: Gender,
    val idNumber: String,
    val bloodType: RhFactorOfBlood,
    val transplantRestrictions: String
)