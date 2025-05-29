package com.example.myapplication.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.utils.LocalStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


data class LoginRequest(val login: String, val password: String)

interface AuthService {
    @POST("api/user/login")
    fun login(@Body loginRequest: LoginRequest): Call<Long>
}

class AuthManager( private val context: Context,  private val onError: (String) -> Unit) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    private val authService = retrofit.create(AuthService::class.java)

    fun login(
        email: String,
        password: String,
        onSuccess: (Long) -> Unit
    ) {
        val request = LoginRequest(login = email, password = password)
        authService.login(request).enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    response.body()?.let(onSuccess)
                        ?: onError(context.getString(R.string.error_empty_response))
                } else {
                    val msg = response.errorBody()?.string()
                        ?: context.getString(R.string.error_login)
                    onError(msg)
                }
            }
            override fun onFailure(call: Call<Long>, t: Throwable) {
                onError(context.getString(R.string.error_network) + ": ${t.message}")
            }
        })
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val authManager = remember {
        AuthManager(context) { msg -> errorMessage = msg }
    }

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
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.h5
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.label_email)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.label_password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = context.getString(R.string.error)
                    } else {
                        isLoading = true
                        errorMessage = null
                        authManager.login(email, password) { userId ->
                            isLoading = false
                            LocalStorage.setUserId(context, userId)
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(stringResource(R.string.login_button))
                }
            }

            Spacer(Modifier.height(8.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("signup") }) {
                Text(
                    text = stringResource(R.string.signup_prompt),
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}

