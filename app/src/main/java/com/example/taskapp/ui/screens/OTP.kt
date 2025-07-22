package com.example.taskapp.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskapp.viewmodel.AuthState
import com.example.taskapp.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun OtpScreen(
    navController: NavController,
    from: String?,
    authViewModel: AuthViewModel = viewModel(LocalContext.current as androidx.lifecycle.ViewModelStoreOwner)
) {
    var otp by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(120) }
    var isResendEnabled by remember { mutableStateOf(false) }

    val verificationId by authViewModel.verificationId.collectAsState()

    val state by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        // Countdown timer
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        isResendEnabled = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("OTP Verification", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = {
                if (it.length <= 6) otp = it.filter { c -> c.isDigit() }
            },
            label = { Text("Enter OTP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("Verification ID: ${verificationId ?: "null"}", color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            if (!isResendEnabled)
                "Resend in ${timeLeft / 60}:${String.format("%02d", timeLeft % 60)}"
            else
                "Didn't get OTP?"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                timeLeft = 120
                isResendEnabled = false
            },
            enabled = isResendEnabled
        ) {
            Text("Resend OTP")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (otp.length == 6) {
                    authViewModel.verifyOtp(otp)
                }
            },
            enabled = otp.length == 6 ,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify OTP")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state is AuthState.Error) {
            Text(
                (state as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }

        LaunchedEffect(state) {
            if (state is AuthState.Success) {
                navController.navigate("dashboard") {
                    popUpTo("otp/$from") { inclusive = true }
                }
            }
        }
    }
}
