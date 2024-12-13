package com.example.propertyappg11.util.helpers

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object BiometricHelper {

    private var canUseBiometric : Boolean? = null

    @JvmStatic
    fun canUseBiometric(context: Context) : Boolean {
        return canUseBiometric ?:
        (BiometricManager
            .from(context)
            .canAuthenticate(Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS).also {
            canUseBiometric = it
        }
    }


    fun showBiometricPrompt(
        fragment : Fragment,
        callback : BiometricPrompt.AuthenticationCallback) {

        val executor = ContextCompat.getMainExecutor(fragment.requireContext())
        val biometricPrompt = BiometricPrompt(fragment,  executor,callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()


        biometricPrompt.authenticate(promptInfo)

    }
}
