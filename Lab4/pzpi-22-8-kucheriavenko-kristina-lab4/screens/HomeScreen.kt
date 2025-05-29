package com.example.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.R

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            val buttons = listOf(
                stringResource(R.string.btn_materials) to "materials",
                stringResource(R.string.btn_donors) to "donors",
                stringResource(R.string.btn_notifications) to "notifications",
                stringResource(R.string.btn_storage_conditions) to "storageConditions",
                stringResource(R.string.btn_logout) to "login"
            )

            buttons.forEach { (label, route) ->
                val colors = if (route == "login")
                    ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                else
                    ButtonDefaults.buttonColors()

                Button(
                    onClick = {
                        navController.navigate(route) {
                            if (route == "login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = colors
                ) {
                    Text(
                        text = label,
                        color = if (route == "login") MaterialTheme.colors.onError else MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    }
}
