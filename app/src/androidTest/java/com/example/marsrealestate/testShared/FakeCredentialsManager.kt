package com.example.marsrealestate.testshared

import com.example.marsrealestate.login.Credentials
import com.example.marsrealestate.login.CredentialsManager

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