package com.example.remembrall.login.userinfo
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

// preferenceHelper object 만듦 -> user 정보 저장
object PreferenceHelper {

    fun defaultPrefs(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }
    
    // 관리할 때 쓸 정보 저장 set
    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is String? -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value) }
            is Boolean -> edit{ it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            else -> throw UnsupportedOperationException("Error")
        }
    }

    // 정보 불러오기
    @Suppress("UNCHECKED_CAST")
    operator fun <T> SharedPreferences.get(key: String, defaultValue: T? = null): T {
        return when (defaultValue) {
            is String, null -> getString(key, defaultValue as? String) as T
            is Int -> getInt(key, defaultValue as? Int ?: -1) as T
            is Boolean -> getBoolean(key, defaultValue as? Boolean ?: false) as T
            is Float -> getFloat(key, defaultValue as? Float ?: -1f) as T
            is Long -> getLong(key, defaultValue as? Long ?: -1) as T
            else -> throw UnsupportedOperationException("Error")
        }
    }

}