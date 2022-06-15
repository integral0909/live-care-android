package com.onseen.livecare.models.Utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.location.LocationListener

class LocalstorageManager {
    companion object {

        val LOCALSTORAGE_PREFIX: String = "RIDESHARE.LOCALSTORAGE.V1"

        fun saveGlobalObject(context: Context, data: String?, keySuffix: String) {

            val key: String = LOCALSTORAGE_PREFIX + "." + keySuffix
            val editor: SharedPreferences.Editor = context.getSharedPreferences(LOCALSTORAGE_PREFIX, MODE_PRIVATE).edit()

            if(data == null) {
                editor.remove(key)
            } else {
                editor.putString(key, data).apply()
            }
        }

        fun loadGlobalObject(context: Context, keySuffix: String): String? {
            val key: String = LOCALSTORAGE_PREFIX + "." + keySuffix
            val editor: SharedPreferences = context.getSharedPreferences(LOCALSTORAGE_PREFIX, MODE_PRIVATE)
            return editor.getString(key, "")
        }

        fun deleteGlobalObject(context: Context, keySuffix: String) {
            var key: String = LOCALSTORAGE_PREFIX + "." + keySuffix
            val editor: SharedPreferences.Editor = context.getSharedPreferences(LOCALSTORAGE_PREFIX, MODE_PRIVATE).edit()
            editor.remove(key).apply()
        }
    }
}