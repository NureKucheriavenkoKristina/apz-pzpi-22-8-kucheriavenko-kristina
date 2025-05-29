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
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.text.SimpleDateFormat
import java.util.*

data class Donor(
    val donorID: Long = 0,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val gender: String,
    val idNumber: String,
    val bloodType: String,
    val transplantRestrictions: String?
)
data class DonorPost(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val gender: String,
    val idNumber: String,
    val bloodType: String,
    val transplantRestrictions: String?
)

interface DonorApiService {
    @GET("/api/donors")
    suspend fun getAllDonors(): List<Donor>

    @GET("/api/donors/{DonorID}")
    suspend fun getDonor(@Path("DonorID") donorID: Long): Donor

    @POST("/api/donors/admin/{userId}/add")
    suspend fun createDonor(@Path("userId") userId: Long, @Body donor: DonorPost): Donor

    @PUT("/api/donors/admin/{userId}/{DonorID}")
    suspend fun updateDonor(
        @Path("userId") userId: Long,
        @Path("DonorID") donorID: Long,
        @Body donor: DonorPost
    ): Donor

    @DELETE("/api/donors/admin/{userId}/{id}")
    suspend fun deleteDonor(
        @Path("userId") userId: Long,
        @Path("id") donorID: Long
    ): Response<Void>
}

@Composable
fun rememberGenderMap(): Map<String, String> {
    val mapOf = mapOf(
        "MALE" to stringResource(R.string.gender_male),
        "FEMALE" to stringResource(R.string.gender_female)
    )
    return mapOf
}

private fun formatDate(dateStr: String): String = try {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    formatter.format(parser.parse(dateStr) ?: Date())
} catch (e: Exception) { dateStr }

@Composable
fun rememberBloodTypeMap() = mapOf(
    "A_POS" to stringResource(R.string.blood_a_pos),
    "A_NEG" to stringResource(R.string.blood_a_neg),
    "B_POS" to stringResource(R.string.blood_b_pos),
    "B_NEG" to stringResource(R.string.blood_b_neg),
    "AB_POS" to stringResource(R.string.blood_ab_pos),
    "AB_NEG" to stringResource(R.string.blood_ab_neg),
    "O_POS" to stringResource(R.string.blood_o_pos),
    "O_NEG" to stringResource(R.string.blood_o_neg)
)

@Composable
fun DonorCard(
    donor: Donor,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val genderMap = rememberGenderMap()
    val bloodMap = rememberBloodTypeMap()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(
                    R.string.label_name, donor.firstName, donor.lastName
                ),
                style = MaterialTheme.typography.h6
            )
            Text(
                text = stringResource(
                    R.string.label_birthdate, formatDate(donor.birthDate)
                )
            )
            Text(
                text = stringResource(
                    R.string.label_gender,
                    genderMap[donor.gender] ?: donor.gender
                )
            )
            Text(
                text = stringResource(
                    R.string.label_id_number, donor.idNumber
                )
            )
            Text(
                text = stringResource(
                    R.string.label_blood_type,
                    bloodMap[donor.bloodType] ?: donor.bloodType
                )
            )
            donor.transplantRestrictions?.let {
                Text(
                    text = stringResource(R.string.label_restrictions, it)
                )
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onEdit) {
                    Text(stringResource(R.string.edit))
                }
                TextButton(onClick = onDelete) {
                    Text(stringResource(R.string.delete))
                }
            }
        }
    }
}

@Composable
fun EditDonorDialog(
    initial: Donor,
    onDismiss: () -> Unit,
    onSave: (DonorPost) -> Unit
) {
    val context = LocalContext.current
    val genderMap = rememberGenderMap()
    val bloodMap = rememberBloodTypeMap()
    val calendar = Calendar.getInstance()

    var firstName by remember { mutableStateOf(initial.firstName) }
    var lastName by remember { mutableStateOf(initial.lastName) }
    var birthDate by remember { mutableStateOf(initial.birthDate) }
    var gender by remember { mutableStateOf(initial.gender) }
    var idNumber by remember { mutableStateOf(initial.idNumber) }
    var bloodType by remember { mutableStateOf(initial.bloodType) }
    var restrictions by remember { mutableStateOf(initial.transplantRestrictions ?: "") }

    fun openDatePicker(current: String, onSet: (String) -> Unit) {
        try {
            calendar.time = SimpleDateFormat(
                "yyyy-MM-dd'T'00:00:00.000+00:00",
                Locale.getDefault()
            ).parse(current) ?: Date()
        } catch (_: Exception) {}
        val cutoff = Calendar.getInstance().apply { add(Calendar.YEAR, -18) }.timeInMillis
        DatePickerDialog(
            context,
            { _, y, m, d ->
                val sel = Calendar.getInstance().apply { set(y, m, d) }
                val out = SimpleDateFormat(
                    "yyyy-MM-dd'T'00:00:00.000+00:00",
                    Locale.getDefault()
                ).format(sel.time)
                onSet(out)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = cutoff
        }.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initial.donorID == 0L)
                    stringResource(R.string.new_donor)
                else
                    stringResource(R.string.edit_donor)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(stringResource(R.string.label_first_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(stringResource(R.string.label_last_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { openDatePicker(birthDate) { birthDate = it } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(
                            R.string.label_birthdate,
                            formatDate(birthDate)
                        )
                    )
                }
                Spacer(Modifier.height(8.dp))
                var expGender by remember { mutableStateOf(false) }
                Box(Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expGender = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(genderMap[gender] ?: gender)
                    }
                    DropdownMenu(expanded = expGender, onDismissRequest = { expGender = false }) {
                        listOf("MALE", "FEMALE").forEach { g ->
                            DropdownMenuItem(onClick = { gender = g; expGender = false }) {
                                Text(genderMap[g] ?: g)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = idNumber,
                    onValueChange = { idNumber = it },
                    label = { Text(stringResource(R.string.label_id_number)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                var expBlood by remember { mutableStateOf(false) }
                Box(Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { expBlood = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(bloodMap[bloodType] ?: bloodType)
                    }
                    DropdownMenu(expanded = expBlood, onDismissRequest = { expBlood = false }) {
                        bloodMap.keys.forEach { bt ->
                            DropdownMenuItem(onClick = { bloodType = bt; expBlood = false }) {
                                Text(bloodMap[bt] ?: bt)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = restrictions,
                    onValueChange = { restrictions = it },
                    label = { Text(stringResource(R.string.label_restrictions)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    DonorPost(
                        firstName, lastName, birthDate,
                        gender, idNumber, bloodType,
                        restrictions.ifBlank { null }
                    )
                )
                onDismiss()
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
fun DonorsScreen(navController: NavController) {
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DonorApiService::class.java)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var donorsList by remember { mutableStateOf(emptyList<Donor>()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedBlood by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var toDelete by remember { mutableStateOf<Donor?>(null) }
    var editing by remember { mutableStateOf<Donor?>(null) }
    val bloodMap = rememberBloodTypeMap()

    LaunchedEffect(Unit) {
        try {
            donorsList = retrofit.getAllDonors()
        } catch (e: Exception) {
            errorMsg = context.getString(R.string.error_load_donors)
        }
    }

    val filtered = donorsList.filter {
        (searchQuery.isBlank() ||
                it.firstName.contains(searchQuery, ignoreCase = true) ||
                it.lastName.contains(searchQuery, ignoreCase = true)) &&
                (selectedBlood == null || it.bloodType == selectedBlood)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(stringResource(R.string.btn_back))
        }
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(stringResource(R.string.search_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        BloodTypeDropdown(selected = selectedBlood, onSelect = { selectedBlood = it })
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                editing = Donor(
                    donorID = 0,
                    firstName = "",
                    lastName = "",
                    birthDate = SimpleDateFormat(
                        "yyyy-MM-dd'T'00:00:00.000+00:00",
                        Locale.getDefault()
                    ).format(Date()),
                    gender = "MALE",
                    idNumber = "",
                    bloodType = "O_POS",
                    transplantRestrictions = null
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.add_donor))
        }
        Spacer(Modifier.height(16.dp))
        errorMsg?.let { Text(it, color = MaterialTheme.colors.error) }
        LazyColumn { items(filtered) { donor ->
            DonorCard(donor, onEdit = { editing = donor }, onDelete = { toDelete = donor })
        }}
    }

    val userID = LocalStorage.getUserId(context) ?: 1L
    toDelete?.let { donor ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text(stringResource(R.string.confirm_delete_donor)) },
            text = {
                Text(
                    stringResource(
                        R.string.confirm_delete_name,
                        donor.firstName, donor.lastName
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        try {
                            val resp = retrofit.deleteDonor(userID, donor.donorID)
                            if (resp.isSuccessful)
                                donorsList = donorsList.filterNot { it.donorID == donor.donorID }
                            else
                                errorMsg = context.getString(
                                    R.string.error_delete_code,
                                    resp.code()
                                )
                        } catch (e: Exception) {
                            Log.e("SAVE", "ERROR", e)
                            errorMsg = context.getString(R.string.error_network)
                        } finally {
                            toDelete = null
                        }
                    }
                }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { toDelete = null }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }

    editing?.let { donor ->
        EditDonorDialog(initial = donor, onDismiss = { editing = null }, onSave = { post ->
            scope.launch {
                try {
                    val saved = if (donor.donorID == 0L)
                        retrofit.createDonor(userID, post)
                    else
                        retrofit.updateDonor(userID, donor.donorID, post)
                    donorsList = donorsList.filterNot { it.donorID == saved.donorID } + saved
                } catch (e: HttpException) {
                    val code = e.code()
                    errorMsg = context.getString(R.string.error_http_save, code)
                } catch (e: Exception) {
                    Log.e("SAVE", "ERROR", e)
                    errorMsg = context.getString(R.string.error_save)
                } finally {
                    editing = null
                }
            }
        })
    }
}
