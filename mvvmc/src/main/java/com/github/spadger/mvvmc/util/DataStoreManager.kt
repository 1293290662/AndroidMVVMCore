/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * DataStore 封装类，提供类型安全的键值存储
 * 使用 Jetpack DataStore Preferences 实现异步、安全的数据持久化
 */
package com.github.spadger.mvvmc.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * DataStore 管理器
 * 提供类型安全的键值存储操作
 */
class DataStoreManager(context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mvvmc_preferences")
    private val dataStore = context.dataStore
    
    // ==================== 写入操作 ====================
    
    suspend fun putString(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }
    
    suspend fun putInt(key: String, value: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }
    
    suspend fun putLong(key: String, value: Long) {
        dataStore.edit { preferences ->
            preferences[longPreferencesKey(key)] = value
        }
    }
    
    suspend fun putFloat(key: String, value: Float) {
        dataStore.edit { preferences ->
            preferences[floatPreferencesKey(key)] = value
        }
    }
    
    suspend fun putDouble(key: String, value: Double) {
        dataStore.edit { preferences ->
            preferences[doublePreferencesKey(key)] = value
        }
    }
    
    suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }
    
    suspend fun putStringSet(key: String, value: Set<String>) {
        dataStore.edit { preferences ->
            preferences[stringSetPreferencesKey(key)] = value
        }
    }
    
    // ==================== 读取操作（返回 Flow） ====================
    
    fun getStringFlow(key: String, defaultValue: String = ""): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    LogUtil.e("DataStore", "Error reading preferences", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[stringPreferencesKey(key)] ?: defaultValue
            }
    }
    
    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    LogUtil.e("DataStore", "Error reading preferences", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[intPreferencesKey(key)] ?: defaultValue
            }
    }
    
    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    LogUtil.e("DataStore", "Error reading preferences", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[longPreferencesKey(key)] ?: defaultValue
            }
    }
    
    fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    LogUtil.e("DataStore", "Error reading preferences", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[floatPreferencesKey(key)] ?: defaultValue
            }
    }
    
    fun getDoubleFlow(key: String, defaultValue: Double = 0.0): Flow<Double> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    LogUtil.e("DataStore", "Error reading preferences", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[doublePreferencesKey(key)] ?: defaultValue
            }
    }
    
    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    LogUtil.e("DataStore", "Error reading preferences", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[booleanPreferencesKey(key)] ?: defaultValue
            }
    }
    
    fun getStringSetFlow(key: String, defaultValue: Set<String> = emptySet()): Flow<Set<String>> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    LogUtil.e("DataStore", "Error reading preferences", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[stringSetPreferencesKey(key)] ?: defaultValue
            }
    }
    
    // ==================== 同步读取操作（挂起函数） ====================
    
    suspend fun getString(key: String, defaultValue: String = ""): String {
        return getStringFlow(key, defaultValue).first()
    }
    
    suspend fun getInt(key: String, defaultValue: Int = 0): Int {
        return getIntFlow(key, defaultValue).first()
    }
    
    suspend fun getLong(key: String, defaultValue: Long = 0L): Long {
        return getLongFlow(key, defaultValue).first()
    }
    
    suspend fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return getFloatFlow(key, defaultValue).first()
    }
    
    suspend fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        return getDoubleFlow(key, defaultValue).first()
    }
    
    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return getBooleanFlow(key, defaultValue).first()
    }
    
    suspend fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String> {
        return getStringSetFlow(key, defaultValue).first()
    }
    
    // ==================== 删除操作 ====================
    
    suspend fun remove(key: String) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
            preferences.remove(intPreferencesKey(key))
            preferences.remove(longPreferencesKey(key))
            preferences.remove(floatPreferencesKey(key))
            preferences.remove(doublePreferencesKey(key))
            preferences.remove(booleanPreferencesKey(key))
            preferences.remove(stringSetPreferencesKey(key))
        }
    }
    
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    // ==================== 辅助方法 ====================
    
    suspend fun contains(key: String): Boolean {
        return dataStore.data.first().contains(stringPreferencesKey(key)) ||
               dataStore.data.first().contains(intPreferencesKey(key)) ||
               dataStore.data.first().contains(longPreferencesKey(key)) ||
               dataStore.data.first().contains(floatPreferencesKey(key)) ||
               dataStore.data.first().contains(doublePreferencesKey(key)) ||
               dataStore.data.first().contains(booleanPreferencesKey(key)) ||
               dataStore.data.first().contains(stringSetPreferencesKey(key))
    }
    
    private fun emptyPreferences(): Preferences {
        return emptyPreferences()
    }
}

/**
 * DataStore 单例管理类
 * 提供全局访问入口
 */
object DataStoreHolder {
    private var instance: DataStoreManager? = null
    
    /**
     * 初始化 DataStoreManager
     * @param context Application Context
     */
    fun init(context: Context) {
        if (instance == null) {
            instance = DataStoreManager(context.applicationContext)
        }
    }
    
    /**
     * 获取 DataStoreManager 实例
     * @throws IllegalStateException 如果未初始化
     */
    fun getInstance(): DataStoreManager {
        return instance ?: throw IllegalStateException(
            "DataStoreHolder has not been initialized. Call init(context) first."
        )
    }
    
    /**
     * 安全获取实例（如果未初始化返回 null）
     */
    fun getInstanceOrNull(): DataStoreManager? {
        return instance
    }
}

/**
 * 常用存储键常量
 */
object DataStoreKeys {
    const val USER_TOKEN = "user_token"
    const val USER_ID = "user_id"
    const val USER_NAME = "user_name"
    const val USER_EMAIL = "user_email"
    const val IS_LOGGED_IN = "is_logged_in"
    const val AUTO_LOGIN = "auto_login"
    const val THEME_MODE = "theme_mode"
    const val LANGUAGE = "language"
    const val LAST_LOGIN_TIME = "last_login_time"
    const val NOTIFICATION_ENABLED = "notification_enabled"
}