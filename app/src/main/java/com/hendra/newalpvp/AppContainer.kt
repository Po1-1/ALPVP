package com.hendra.newalpvp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences


class AppContainer(
    private val dataStore: DataStore<Preferences>
) {
    private val backendURL = "192.168.100.228"
}