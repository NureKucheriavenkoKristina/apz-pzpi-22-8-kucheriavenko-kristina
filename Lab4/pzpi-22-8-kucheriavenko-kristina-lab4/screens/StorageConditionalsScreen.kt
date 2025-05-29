package com.example.myapplication.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.text.SimpleDateFormat
import java.util.*

enum class StorageZone(val description: String) {
    GREEN("Standard storage conditions"),
    YELLOW("Minor deviation"),
    RED("Critical conditions")
}

data class MaterialReq(
    @SerializedName("materialID")
    val materialID: Long
)

data class StorageCondition(
    @SerializedName("recordID")
    val recordID: Long = 0,

    @SerializedName("temperature")
    val temperature: Double,

    @SerializedName("oxygenLevel")
    val oxygenLevel: Double,

    @SerializedName("humidity")
    val humidity: Double,

    @SerializedName("measurementTime")
    val measurementTime: String,

    @SerializedName("storage_zone")
    val zone: StorageZone?,

    @SerializedName("materialID")
    val materialID: MaterialReq
)


interface StorageConditionApiService {
    @GET("/api/storage-conditions")
    suspend fun getAllConditions(): List<StorageCondition>

    @GET("/api/biological-materials")
    suspend fun getMaterials(): List<BiologicalMaterial>
}

@SuppressLint("StringFormatMatches")
@Composable
fun StorageConditionCard(
    condition: StorageCondition,
    materials: List<BiologicalMaterial>
) {
    val context = LocalContext.current
    val materialName = materials
        .find { it.materialID == condition.materialID.materialID }
        ?.materialName
        ?: context.getString(R.string.unknown)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                context.getString(R.string.record_number, condition.recordID),
                style = MaterialTheme.typography.h6
            )
            Text(
                context.getString(
                    R.string.zone_label,
                    condition.zone?.name ?: context.getString(R.string.unknown),
                    condition.zone?.description ?: ""
                )
            )
            Text(context.getString(R.string.temperature_label, condition.temperature))
            Text(context.getString(R.string.oxygen_level_label, condition.oxygenLevel))
            Text(context.getString(R.string.humidity_label, condition.humidity))
            Text(context.getString(R.string.time_label, formatDateTime(condition.measurementTime)))
            Text(context.getString(R.string.material_label, materialName))
        }
    }
}

@Composable
fun StorageConditionalsScreen(navController: NavController) {
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StorageConditionApiService::class.java)
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var conditions by remember { mutableStateOf<List<StorageCondition>>(emptyList()) }
    var materials by remember { mutableStateOf<List<BiologicalMaterial>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    var searchZone by remember { mutableStateOf("") }
    var selectedZone by remember { mutableStateOf<StorageZone?>(null) }
    var selectedMaterial by remember { mutableStateOf<Long?>(null) }
    var sortAsc by remember { mutableStateOf(true) }
    var expandedZone by remember { mutableStateOf(false) }
    var expandedMat by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            conditions = retrofit.getAllConditions()
            materials = retrofit.getMaterials()
        } catch (e: Exception) {
            errorMsg = context.getString(R.string.loading_error)
            Log.e("API", "Load error", e)
        }
    }

    fun parseDate(str: String): Date = runCatching {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            .parse(str) ?: Date(0)
    }.getOrDefault(Date(0))

    val filteredSorted = remember(conditions, searchZone, selectedZone, selectedMaterial, sortAsc) {
        conditions
            .filter { cond ->
                cond.zone?.name?.contains(searchZone, ignoreCase = true) == true &&
                        (selectedZone == null || cond.zone == selectedZone) &&
                        (selectedMaterial == null || cond.materialID.materialID == selectedMaterial)
            }
            .sortedBy { parseDate(it.measurementTime) }
            .let { if (sortAsc) it else it.reversed() }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { navController.popBackStack() },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(context.getString(R.string.back_button))
        }

        errorMsg?.let { Text(it, color = MaterialTheme.colors.error) }

        OutlinedTextField(
            value = searchZone,
            onValueChange = { searchZone = it },
            label = { Text(context.getString(R.string.search_by_zone)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { expandedZone = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedZone?.name ?: context.getString(R.string.all_zones))
            }
            DropdownMenu(expanded = expandedZone, onDismissRequest = { expandedZone = false }) {
                DropdownMenuItem(onClick = {
                    selectedZone = null
                    expandedZone = false
                }) { Text(context.getString(R.string.all_zones)) }
                StorageZone.values().forEach { zone ->
                    DropdownMenuItem(onClick = {
                        selectedZone = zone
                        expandedZone = false
                    }) {
                        Text(zone.name)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { expandedMat = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    materials.find { it.materialID == selectedMaterial }?.materialName
                        ?: context.getString(R.string.all_materials)
                )
            }
            DropdownMenu(expanded = expandedMat, onDismissRequest = { expandedMat = false }) {
                DropdownMenuItem(onClick = {
                    selectedMaterial = null
                    expandedMat = false
                }) { Text(context.getString(R.string.all_materials))}
                    materials.forEach { mat ->
                    DropdownMenuItem(onClick = {
                        selectedMaterial = mat.materialID
                        expandedMat = false
                    }) {
                        Text(mat.materialName)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(context.getString(R.string.sort_by_time))
            Switch(
                checked = sortAsc,
                onCheckedChange = { sortAsc = it },
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                if (sortAsc) context.getString(R.string.sort_asc) else context.getString(R.string.sort_desc) ,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(filteredSorted) { cond ->
                StorageConditionCard(cond, materials)
            }
        }
    }
}

private fun formatDateTime(dateStr: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val out = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        out.format(parser.parse(dateStr) ?: Date())
    } catch (e: Exception) {
        dateStr
    }
}