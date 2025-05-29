package com.example.myapplication.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.text.SimpleDateFormat
import java.util.*

data class MaterialRef(
    val materialID: Long
)

data class Notification(
    val notificationID: Long = 0,
    val eventType: String,
    val details: String,
    val notificationTime: String,
    val materialID: MaterialRef
)

interface NotificationApiService {
    @GET("/api/notifications")
    suspend fun getAllNotifications(): List<Notification>

    @GET("/api/biological-materials")
    suspend fun getMaterials(): List<BiologicalMaterial>
}

@Composable
fun NotificationCard(
    notification: Notification,
    materials: List<BiologicalMaterial>
) {
    val materialName = materials
        .find { it.materialID == notification.materialID.materialID }
        ?.materialName
        ?: stringResource(R.string.unknown)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(
                    R.string.label_event,
                    notification.eventType
                ),
                style = MaterialTheme.typography.h6
            )
            Text(
                text = stringResource(
                    R.string.label_details,
                    notification.details
                )
            )
            Text(
                text = stringResource(
                    R.string.label_time,
                    formatDate(notification.notificationTime)
                )
            )
            Text(
                text = stringResource(
                    R.string.label_material,
                    materialName
                )
            )
        }
    }
}

@Composable
fun NotificationsScreen(navController: NavController) {
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationApiService::class.java)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var materials by remember { mutableStateOf<List<BiologicalMaterial>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    var selectedMaterial by remember { mutableStateOf<Long?>(null) }
    var sortAsc by remember { mutableStateOf(true) }
    var expandedMat by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            notifications = retrofit.getAllNotifications()
            materials = retrofit.getMaterials()
        } catch (e: Exception) {
            errorMsg = context.getString(R.string.error_load)
            Log.e("API", context.getString(R.string.error_load), e)
        }
    }

    fun parseDate(str: String): Date = try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            .parse(str) ?: Date(0)
    } catch (_: Exception) { Date(0) }

    val filteredSorted = remember(notifications, selectedMaterial, sortAsc) {
        notifications
            .filter { notif ->
                (selectedMaterial == null || notif.materialID.materialID == selectedMaterial)
            }
            .sortedBy { parseDate(it.notificationTime) }
            .let { if (sortAsc) it else it.reversed() }
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

        errorMsg?.let { Text(it, color = MaterialTheme.colors.error) }

        Spacer(Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expandedMat = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = materials
                        .find { it.materialID == selectedMaterial }
                        ?.materialName
                        ?: stringResource(R.string.filter_all_materials)
                )
            }
            DropdownMenu(
                expanded = expandedMat,
                onDismissRequest = { expandedMat = false }
            ) {
                DropdownMenuItem(onClick = {
                    selectedMaterial = null
                    expandedMat = false
                }) {
                    Text(stringResource(R.string.filter_all_materials))
                }
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

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.sort_label))
            Switch(
                checked = sortAsc,
                onCheckedChange = { sortAsc = it },
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = if (sortAsc) stringResource(R.string.sort_asc) else stringResource(R.string.sort_desc),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(filteredSorted) { notif ->
                NotificationCard(notification = notif, materials = materials)
            }
        }
    }
}

private fun formatDate(dateStr: String): String = try {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    val out = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    out.format(parser.parse(dateStr) ?: Date())
} catch (e: Exception) {
    dateStr
}

