package com.example.propertyappg11.testshared

import com.example.propertyappg11.login.Credentials
import com.example.propertyappg11.login.CredentialsManager

class FakeCredentialsManager : CredentialsManager {

    private var credentials : Credentials? = null

    override fun getSavedCredentials(): Credentials? = credentials

    override fun saveCredentials(credentials: Credentials) {
        this.credentials = credentials
    }

    override fun deleteCredentials() {
        credentials = null
    }
}
