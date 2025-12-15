package com.hendra.newalpvp.ui.container

import android.content.Context

object TokenManager {
    private const val PREF_NAME = "MomentumAuth"
    private const val KEY_TOKEN = "jwt_token"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        getPrefs(context).edit().remove(KEY_TOKEN).apply()
    }
}