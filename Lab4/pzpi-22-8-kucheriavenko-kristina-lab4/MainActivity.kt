package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.*
import com.example.myapplication.screens.*
import java.util.*

val LocalAppLocale = compositionLocalOf { "uk" }
@Composable
fun ProvideAppLocale(content: @Composable () -> Unit) {
    val locale = LocalAppLocale.current
    val config = LocalConfiguration.current
    val ctx = LocalContext.current

    SideEffect {
        config.setLocale(Locale(locale))
        ctx.resources.updateConfiguration(config, ctx.resources.displayMetrics)
    }

    content()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    var locale by remember { mutableStateOf("uk") }

    CompositionLocalProvider(LocalAppLocale provides locale) {
        ProvideAppLocale {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.app_name)) },
                        actions = {
                            Spacer(Modifier.weight(2f))
                            TextButton(
                                onClick = { locale = if (locale=="uk") "en" else "uk" },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                            ) {
                                Text(if (locale=="uk") "EN" else "UA")
                            }
                        }
                    )
                },
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "login",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("login")         { LoginScreen(navController) }
                    composable("home")          { HomeScreen(navController) }
                    composable("signup")        { SignupScreen(navController) }
                    composable("materials")     { BiologicalMaterialsScreen(navController) }
                    composable("notifications") { NotificationsScreen(navController) }
                    composable("donors")        { DonorsScreen(navController) }
                    composable("storageConditions") {
                        StorageConditionalsScreen(navController)
                    }
                }
            }
        }
    }
}
