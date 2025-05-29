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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class SignupRequest(
    val role: String = "USER",
    val login: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val access_rights: String = "READ_ONLY"
)

data class SignupResponse(
    val userId: Long? = null,
    val message: String? = null,
    val sessionToken: String? = null
) {
    val success: Boolean get() = !sessionToken.isNullOrEmpty()
}

interface RegisterService {
    @POST("/api/user")
    fun register(@Body signupRequest: SignupRequest): Call<SignupResponse>
}

class RegisterManager(private val context: Context, private val onError: (String) -> Unit) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val registerService = retrofit.create(RegisterService::class.java)

    fun register(
        login: String,
        firstName: String,
        lastName: String,
        password: String,
        onSuccess: (SignupResponse) -> Unit
    ) {
        val signupRequest = SignupRequest(
            login = login,
            password = password,
            first_name = firstName,
            last_name = lastName
        )
        registerService.register(signupRequest).enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        onSuccess(SignupResponse(message = response.message()))
                    } else {
                        onError(context.getString(R.string.error_signup_status, response.message()))
                    }
                } else {
                    onError(response.errorBody()?.string()
                        ?: context.getString(R.string.error_signup))
                }
            }
            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                onError(context.getString(R.string.error_network) + ": ${t.message}")
            }
        })
    }
}

@Composable
fun SignupScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val registerManager = remember {
        RegisterManager(context) { msg -> errorMessage = msg }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.signup_title),
                style = MaterialTheme.typography.h5
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text(stringResource(R.string.first_name_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text(stringResource(R.string.last_name_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isBlank() || firstName.isBlank() || lastName.isBlank() || password.isBlank()) {
                        errorMessage = context.getString(R.string.error_fill_all_fields)
                    } else {
                        isLoading = true
                        errorMessage = null
                        registerManager.register(
                            email, firstName, lastName, password,
                            onSuccess = {
                                isLoading = false
                                navController.navigate("login") {
                                    popUpTo("signup") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.signup_button))
                }
            }

            Spacer(Modifier.height(8.dp))

            errorMessage?.let {
                Text(text = it, color = Color.Red, modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text(stringResource(R.string.have_account), color = MaterialTheme.colors.primary)
            }
        }
    }
}
