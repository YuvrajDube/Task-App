package com.example.taskapp.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskapp.viewmodel.AuthState
import com.example.taskapp.viewmodel.AuthViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun SignUp(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(LocalContext.current as androidx.lifecycle.ViewModelStoreOwner)
) {
    val context = LocalContext.current as Activity
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.filter { c -> c.isDigit() }.take(10) },
            label = { Text("Phone Number (10 digits)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (error.isNotEmpty()) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (name.isBlank()) {
                    error = "Name is required"
                } else if (phone.length != 10) {
                    error = "Enter valid 10-digit phone number"
                } else {
                    error = ""
                    authViewModel.startPhoneVerification(phone, context)
                }
            },
            enabled = authState != AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (authState == AuthState.Loading) "Sending OTP..." else "Continue")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("signin") }) {
            Text("Already have an account? Sign In")
        }
    }

    // ✅ Navigate ONLY after OTP is sent
    LaunchedEffect(authState) {
        if (authState is AuthState.CodeSent) {
            navController.navigate("otp/signup")
        } else if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }
}
