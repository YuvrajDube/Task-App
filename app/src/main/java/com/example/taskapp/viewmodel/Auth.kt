package com.example.taskapp.viewmodel

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object CodeSent : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId

    fun startPhoneVerification(phone: String, activity: Activity) {
        val fullPhone = "+91$phone"
        _authState.value = AuthState.Loading

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(fullPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("OTP_DEBUG", "Auto verification complete")
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("OTP_DEBG", "Verification failed: ${e.localizedMessage}")
                    _authState.value = AuthState.Error("Verification failed: ${e.localizedMessage}")
                    Toast.makeText(activity, "OTP Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d("OTP_DEBUG", " onCodeSent() called. hii Verification ID = $verificationId")
                    _verificationId.value = verificationId
                    _authState.value = AuthState.CodeSent
                }

            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(code: String) {
        val verId = _verificationId.value
        if (!verId.isNullOrEmpty()) {
            val credential = PhoneAuthProvider.getCredential(verId, code)
            signInWithCredential(credential)
        } else {
            Log.e("OTP_DEBUG", "Verification ID is null")
            _authState.value = AuthState.Error("Missing verification ID")
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Authentication failed: ${task.exception?.message}")
                }
            }
    }
}
