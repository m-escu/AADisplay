package io.github.nitsuya.aa.display.util

import android.content.SharedPreferences

/**
 * Read-only SharedPreferences view over a map delivered via binder,
 * so system_server sees the module app's real (MODE_PRIVATE) prefs.
 */
class MapSharedPreferences(private val values: Map<String, String?>) : SharedPreferences {
    override fun getAll(): Map<String, *> = values
    override fun getString(key: String, defValue: String?): String? = values[key] ?: defValue
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? = defValues
    override fun getInt(key: String, defValue: Int): Int = values[key]?.toInt() ?: defValue
    override fun getLong(key: String, defValue: Long): Long = values[key]?.toLong() ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = values[key]?.toFloat() ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean = values[key]?.toBoolean() ?: defValue
    override fun contains(key: String): Boolean = values.containsKey(key)
    override fun edit(): SharedPreferences.Editor = throw UnsupportedOperationException()
    override fun registerOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
}
