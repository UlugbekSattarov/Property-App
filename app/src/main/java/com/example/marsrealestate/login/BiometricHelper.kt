package com.example.marsrealestate.login

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class BiometricHelper {

    companion object {

        @JvmStatic
        fun canUseBiometric(context: Context) : Boolean =
            BiometricManager.from(context).canAuthenticate(Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS


        fun showBiometricPrompt(
            fragment : Fragment,
            callback : BiometricPrompt.AuthenticationCallback) {

            val executor = ContextCompat.getMainExecutor(fragment.context)
            val biometricPrompt = BiometricPrompt(fragment,  executor,callback)

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build()


            biometricPrompt.authenticate(promptInfo)

        }
    }
}