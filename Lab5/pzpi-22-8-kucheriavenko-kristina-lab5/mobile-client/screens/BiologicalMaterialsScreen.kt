package com.example.myapplication.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.utils.LocalStorage
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.text.SimpleDateFormat
import java.util.*


data class DonorM(
    val donorID: Long,
    val firstName: String?,
    val lastName: String?,
    val birthDate: String?,
    val gender: String?,
    val idNumber: String?,
    val bloodType: String?,
    val transplantRestrictions: String?
)

data class DonorRef(
    val donorID: Long
)

data class BiologicalMaterial(
    val materialID: Long = 0,
    val materialName: String,
    val expirationDate: String,
    val status: String,
    val transferDate: String,
    val idealTemperature: Double,
    val idealOxygenLevel: Double,
    val idealHumidity: Double,
    val donorID: DonorM
)

data class BiologicalMaterialPost(
    val materialName: String,
    val expirationDate: String,
    val status: String,
    val transferDate: String,
    val idealTemperature: Double,
    val idealOxygenLevel: Double,
    val idealHumidity: Double,
    val donorID: DonorRef
)

interface MaterialApiService {
    @GET("/api/biological-materials")
    suspend fun getAllMaterials(): List<BiologicalMaterial>

    @DELETE("/api/biological-materials/admin/{userId}/{id}")
    suspend fun deleteMaterial(
        @Path("userId") userId: Long,
        @Path("id") id: Long?,
    ): retrofit2.Response<Void>

    @POST("/api/biological-materials/{userId}/add")
    suspend fun createMaterial(
        @Path("userId") userId: Long,
        @Body material: BiologicalMaterialPost
    ): BiologicalMaterial

    @PUT("/api/biological-materials/{userId}/{id}")
    suspend fun updateMaterial(
        @Path("userId") userId: Long,
        @Path("id") id: Long,
        @Body material: BiologicalMaterial
    ): BiologicalMaterial

    @GET("/api/donors")
    suspend fun getDonors(): List<DonorM>
}

@Composable
fun statusMap() = mapOf(
    "AVAILABLE" to stringResource(R.string.status_available),
    "DONATED"   to stringResource(R.string.status_donated),
    "DISPOSED"  to stringResource(R.string.status_disposed)
)


@Composable
fun BloodTypeDropdown(selected: String?, onSelect: (String?) -> Unit) {
    val bloodTypes =
        listOf("A_POS", "A_NEG", "B_POS", "B_NEG", "AB_POS", "AB_NEG", "O_POS", "O_NEG")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(text = selected ?: stringResource(R.string.filter_blood))
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = { onSelect(null); expanded = false }) {
                Text(stringResource(R.string.all))
            }
            bloodTypes.forEach { code ->
                DropdownMenuItem(onClick = { onSelect(code); expanded = false }) {
                    Text(formatBloodType(code))
                }
            }
        }
    }
}

@Composable
fun StatusDropdown(selected: String?, onSelect: (String?) -> Unit) {
    val statusOptions = listOf("AVAILABLE", "DONATED", "DISPOSED")
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(text = statusMap()[selected] ?: stringResource(R.string.filter_status))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = { onSelect(null); expanded = false }) {
                Text(stringResource(R.string.all))
            }
            statusOptions.forEach { code ->
                DropdownMenuItem(onClick = { onSelect(code); expanded = false }) {
                    Text(statusMap()[code] ?: code)
                }
            }
        }
    }
}


@Composable
fun MaterialCard(material: BiologicalMaterial, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(
                    R.string.material_label,
                    material.materialName
                ),
                style = MaterialTheme.typography.h6
            )
            Text(
                text = stringResource(
                    R.string.status_label,
                    statusMap()[material.status] ?: material.status
                )
            )
            Text(
                text = stringResource(
                    R.string.expiration_date_label,
                    formatDate(material.expirationDate)
                )
            )
            Text(
                text = stringResource(
                    R.string.transfer_date_label,
                    formatDate(material.transferDate)
                )
            )
            Text(
                text = stringResource(
                    R.string.temperature_label,
                    material.idealTemperature
                )
            )
            Text(
                text = stringResource(
                    R.string.oxygen_label,
                    material.idealOxygenLevel
                )
            )
            Text(
                text = stringResource(
                    R.string.humidity_label,
                    material.idealHumidity
                )
            )
            Text(
                text = stringResource(
                    R.string.donor_label,
                    material.donorID.firstName.orEmpty(),
                    material.donorID.lastName.orEmpty()
                )
            )
            Text(
                text = stringResource(
                    R.string.blood_group_label,
                    formatBloodType(material.donorID.bloodType)
                )
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onEdit) { Text(stringResource(R.string.edit)) }
                TextButton(onClick = onDelete) { Text(stringResource(R.string.delete)) }
            }
        }
    }
}


private fun formatBloodType(code: String?): String {
    return when (code) {
        "A_POS" -> "A+"
        "A_NEG" -> "A-"
        "B_POS" -> "B+"
        "B_NEG" -> "B-"
        "AB_POS" -> "AB+"
        "AB_NEG" -> "AB-"
        "O_POS" -> "O+"
        "O_NEG" -> "O-"
        else -> "Невідомо"
    }
}


private fun formatDate(dateStr: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = parser.parse(dateStr)
        formatter.format(date ?: Date())
    } catch (e: Exception) {
        dateStr
    }
}

@Composable
fun EditMaterialDialog(
    initial: BiologicalMaterial,
    donors: List<DonorM>,
    onDismiss: () -> Unit,
    onSave: (BiologicalMaterial) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var name by remember { mutableStateOf(initial.materialName) }
    var status by remember { mutableStateOf(initial.status) }
    var expirationDate by remember { mutableStateOf(initial.expirationDate) }
    var transferDate by remember { mutableStateOf(initial.transferDate) }
    var temperature by remember { mutableStateOf(initial.idealTemperature.toString()) }
    var oxygen by remember { mutableStateOf(initial.idealOxygenLevel.toString()) }
    var humidity by remember { mutableStateOf(initial.idealHumidity.toString()) }
    var selectedDonor by remember { mutableStateOf(initial.donorID) }

    val statusOptions = listOf("AVAILABLE", "DONATED", "DISPOSED")
    val statusMap = mapOf(
        "AVAILABLE" to stringResource(R.string.status_available),
        "DONATED"   to stringResource(R.string.status_donated),
        "DISPOSED"  to stringResource(R.string.status_disposed)
    )

    val bloodTypes =
        listOf("A_POS", "A_NEG", "B_POS", "B_NEG", "AB_POS", "AB_NEG", "O_POS", "O_NEG")
    var selectedBloodType by remember { mutableStateOf(selectedDonor.bloodType ?: "") }
    var expandedBlood by remember { mutableStateOf(false) }

    fun openDatePicker(current: String, minDate: Long?, maxDate: Long?, onSet: (String) -> Unit) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.time = sdf.parse(current) ?: Date()
        } catch (_: Exception) {
        }

        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selected = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                onSet(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selected.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            minDate?.let { datePicker.minDate = it }
            maxDate?.let { datePicker.maxDate = it }
        }.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initial.materialID == 0L) stringResource(R.string.new_material) else stringResource(
                    R.string.edit_material
                )
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name_label)) })

                Spacer(modifier = Modifier.height(8.dp))

                var expandedStatus by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { expandedStatus = true }) {
                        Text(statusMap[status] ?: status)
                    }
                    DropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }) {
                        statusOptions.forEach {
                            DropdownMenuItem(onClick = {
                                status = it
                                expandedStatus = false
                            }) {
                                Text(statusMap[it] ?: it)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(onClick = {
                    openDatePicker(transferDate, null, System.currentTimeMillis()) {
                        transferDate = it
                    }
                }) {
                    Text(
                        text = stringResource(
                            R.string.transfer_date_label,
                            formatDate(transferDate)
                        )
                    )
                }

                OutlinedButton(onClick = {
                    openDatePicker(expirationDate, System.currentTimeMillis(), null) {
                        expirationDate = it
                    }
                }) {
                    Text(
                        text = stringResource(
                            R.string.expiration_date_label,
                            formatDate(expirationDate)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = temperature,
                    onValueChange = {
                        if (it.toDoubleOrNull() != null && it.toDouble() in -100.0..100.0) temperature =
                            it
                    },
                    label = {
                        Text(stringResource(R.string.temperature_label))
                    }
                )

                OutlinedTextField(
                    value = oxygen,
                    onValueChange = {
                        if (it.toDoubleOrNull() != null && it.toDouble() in 0.0..100.0) oxygen = it
                    },
                    label = {
                        Text(stringResource(R.string.oxygen_label))
                    }
                )

                OutlinedTextField(
                    value = humidity,
                    onValueChange = {
                        if (it.toDoubleOrNull() != null && it.toDouble() in 0.0..100.0) humidity =
                            it
                    },
                    label = {
                        Text(stringResource(R.string.humidity_label))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                var expandedDonor by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { expandedDonor = true }) {
                        Text(
                            text = stringResource(
                                R.string.donor_label,
                                selectedDonor.firstName.orEmpty(),
                                selectedDonor.lastName.orEmpty()
                            )
                        )
                    }
                    DropdownMenu(
                        expanded = expandedDonor,
                        onDismissRequest = { expandedDonor = false }) {
                        donors.forEach {
                            DropdownMenuItem(onClick = {
                                selectedDonor = it
                                selectedBloodType = it.bloodType ?: ""
                                expandedDonor = false
                            }) {
                                Text("${it.firstName} ${it.lastName}")
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedBlood = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = formatBloodType(selectedBloodType))
                    }

                    DropdownMenu(
                        expanded = expandedBlood,
                        onDismissRequest = { expandedBlood = false }) {
                        bloodTypes.forEach {
                            DropdownMenuItem(onClick = {
                                selectedBloodType = it
                                selectedDonor = selectedDonor.copy(bloodType = it)
                                expandedBlood = false
                            }) {
                                Text(formatBloodType(it))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    initial.copy(
                        materialName = name,
                        status = status,
                        transferDate = transferDate,
                        expirationDate = expirationDate,
                        idealTemperature = temperature.toDoubleOrNull() ?: 0.0,
                        idealOxygenLevel = oxygen.toDoubleOrNull() ?: 0.0,
                        idealHumidity = humidity.toDoubleOrNull() ?: 0.0,
                        donorID = selectedDonor
                    )
                )
            }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun BiologicalMaterialsScreen(navController: NavController) {
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MaterialApiService::class.java)
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var materials by remember { mutableStateOf<List<BiologicalMaterial>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedBloodType by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var materialToDelete by remember { mutableStateOf<BiologicalMaterial?>(null) }
    var editingMaterial by remember { mutableStateOf<BiologicalMaterial?>(null) }
    var donors by remember { mutableStateOf<List<DonorM>>(emptyList()) }

    LaunchedEffect(true) {
        try {
            materials = retrofit.getAllMaterials()
            donors = retrofit.getDonors()
        } catch (e: Exception) {
            errorMessage = context.getString(R.string.error)
        }
    }

    val filteredMaterials = materials.filter {
        (searchQuery.isEmpty() || it.materialName.contains(searchQuery, ignoreCase = true)) &&
                (selectedBloodType == null || it.donorID.bloodType == selectedBloodType) &&
                (selectedStatus == null || it.status == selectedStatus)
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(stringResource(R.string.btn_back))
        }

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(stringResource(R.string.search_by_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        BloodTypeDropdown(selectedBloodType) { selectedBloodType = it }
        Spacer(modifier = Modifier.height(8.dp))
        StatusDropdown(selectedStatus) { selectedStatus = it }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            editingMaterial = BiologicalMaterial(
                materialName = "",
                expirationDate = "",
                status = "AVAILABLE",
                transferDate = "",
                idealTemperature = 0.0,
                idealOxygenLevel = 0.0,
                idealHumidity = 0.0,
                donorID = donors.firstOrNull() ?: DonorM(0, "", "", "", "", "", "", "")
            )
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.add_material))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(errorMessage ?: "", color = MaterialTheme.colors.error)
        }

        LazyColumn {
            items(filteredMaterials) { material ->
                MaterialCard(
                    material,
                    onEdit = { editingMaterial = material },
                    onDelete = { materialToDelete = material })
            }
        }
    }
    var userID = LocalStorage.getUserId(context)
    if (userID == null)
        userID = 8
    materialToDelete?.let { material ->
        AlertDialog(
            onDismissRequest = { materialToDelete = null },
            title = { Text(stringResource(R.string.confirmation)) },
            text = { Text(stringResource(
                R.string.confirm_delete_material,
                material.materialName
            )) },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        try {
                            Log.e("USER", userID.toString())
                            val resp = retrofit.deleteMaterial(userID, material.materialID)
                            if (resp.isSuccessful) {
                                materials =
                                    materials.filterNot { it.materialID == material.materialID }
                            } else {
                                errorMessage = "Не вдалося видалити (код ${resp.code()})"
                            }
                        } catch (e: Exception) {
                           errorMessage = "Помилка мережі при видаленні"
                        } finally {
                            materialToDelete = null
                        }
                    }


                }) { Text(stringResource(R.string.yes)) }
            },
            dismissButton = {
                TextButton(onClick = { materialToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    editingMaterial?.let { material ->
        EditMaterialDialog(
            initial = material,
            donors = donors,
            onDismiss = { editingMaterial = null },
            onSave = { updated ->
                coroutineScope.launch {
                    try {
                        val userId = LocalStorage.getUserId(context) ?: return@launch

                        if (updated.materialName.isBlank()) {
                            errorMessage = context.getString(R.string.error_name_blank)
                            return@launch
                        }
                        if (updated.expirationDate.isBlank()) {
                            errorMessage = context.getString(R.string.error_pick_expiration)
                            return@launch
                        }
                        if (updated.transferDate.isBlank()) {
                            errorMessage = context.getString(R.string.error_pick_transfer)
                            return@launch
                        }
                        if (updated.donorID.donorID == 0L) {
                            errorMessage = context.getString(R.string.error_pick_donor)
                            return@launch
                        }
                        val result = if (updated.materialID == 0L) {
                            val postPayload = BiologicalMaterialPost(
                                materialName = updated.materialName,
                                expirationDate = updated.expirationDate,
                                status = updated.status,
                                transferDate = updated.transferDate,
                                idealTemperature = updated.idealTemperature,
                                idealOxygenLevel = updated.idealOxygenLevel,
                                idealHumidity = updated.idealHumidity,
                                donorID = DonorRef(updated.donorID.donorID)
                            )
                            val gson = Gson()
                            Log.d("DEBUG", "SENDING JSON: ${gson.toJson(postPayload)}")
                            val created = retrofit.createMaterial(userId, postPayload)
                            Log.d("DEBUG", "POST RESULT: $created")
                            created
                        } else {
                            val updatedMaterial =
                                retrofit.updateMaterial(userId, updated.materialID, updated)
                            Log.d("DEBUG", "PUT RESULT: $updatedMaterial")
                            updatedMaterial
                        }

                        materials =
                            materials.filterNot { it.materialID == result.materialID } + result
                    } catch (e: Exception) {
                        errorMessage = context.getString(R.string.error)
                        Log.e("SAVE", "POST/PUT ERROR", e)
                    } finally {
                        editingMaterial = null
                    }
                }
            }
        )
    }

}