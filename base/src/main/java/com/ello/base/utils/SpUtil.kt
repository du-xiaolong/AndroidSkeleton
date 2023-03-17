@file:Suppress("UNCHECKED_CAST")

package com.ello.base.utils

import android.os.Parcelable
import com.alibaba.fastjson.JSON
import com.ello.base.ktx.llloge
import com.tencent.mmkv.MMKV


const val SP_NAME = "user"

object SpUtil {


    inline fun <reified T : Parcelable> getParcelable(key: String, filename: String = SP_NAME): T? {
        val mmkv = MMKV.mmkvWithID(filename) ?: return null
        return mmkv.decodeParcelable(key, T::class.java)
    }



    @JvmOverloads
    @JvmStatic
    fun getString(key: String, default: String? = null, filename: String = SP_NAME): String? {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null || !mmkv.containsKey(key)) {
            return default
        }
        return mmkv.decodeString(key, default)
    }

    @JvmOverloads
    @JvmStatic
    fun getBoolean(key: String, default: Boolean = false, filename: String = SP_NAME): Boolean {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null || !mmkv.containsKey(key)) {
            return default
        }
        return mmkv.decodeBool(key, default)
    }


    @JvmOverloads
    @JvmStatic
    fun getInt(key: String, default: Int = 0, filename: String = SP_NAME): Int {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null || !mmkv.containsKey(key)) {
            return default
        }
        return mmkv.decodeInt(key, default)
    }

    @JvmOverloads
    @JvmStatic
    fun getLong(key: String, default: Long = 0L, filename: String = SP_NAME): Long {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null || !mmkv.containsKey(key)) {
            return default
        }
        return mmkv.decodeLong(key, default)
    }

    @JvmOverloads
    @JvmStatic
    fun getDouble(key: String, default: Double = 0.0, filename: String = SP_NAME): Double {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null || !mmkv.containsKey(key)) {
            return default
        }
        return mmkv.decodeDouble(key, default)
    }

    @JvmOverloads
    @JvmStatic
    fun getFloat(key: String, default: Float = 0f, filename: String = SP_NAME): Float {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null || !mmkv.containsKey(key)) {
            return default
        }
        return mmkv.decodeFloat(key, default)
    }


    @JvmOverloads
    @JvmStatic
    inline fun <reified T> getList(key: String, filename: String = SP_NAME): List<T> {
        val string = getString(key, filename = filename)
        return JSON.parseArray(string, T::class.java) ?: emptyList()
    }


    @JvmOverloads
    @JvmStatic
    fun putString(key: String, value: String?, filename: String = SP_NAME) {
        val mmkv = MMKV.mmkvWithID(filename) ?: return
        if (value == null) {
            mmkv.remove(key)
            return
        }
        mmkv.encode(key, value)
    }

    @JvmOverloads
    @JvmStatic
    fun putBoolean(key: String, value: Boolean, filename: String = SP_NAME) {
        MMKV.mmkvWithID(filename)?.encode(key, value)
    }


    @JvmOverloads
    @JvmStatic
    fun putInt(key: String, value: Int, filename: String = SP_NAME) {
        MMKV.mmkvWithID(filename)?.encode(key, value)
    }

    @JvmOverloads
    @JvmStatic
    fun putLong(key: String, value: Long, filename: String = SP_NAME) {
        MMKV.mmkvWithID(filename)?.encode(key, value)
    }

    @JvmOverloads
    @JvmStatic
    fun putDouble(key: String, value: Double, filename: String = SP_NAME) {
        MMKV.mmkvWithID(filename)?.encode(key, value)
    }

    @JvmOverloads
    @JvmStatic
    fun putFloat(key: String, value: Float, filename: String = SP_NAME) {
        MMKV.mmkvWithID(filename)?.encode(key, value)
    }


    @JvmOverloads
    @JvmStatic
    inline fun <reified T : Parcelable> putParcelable(
        key: String,
        value: T?,
        filename: String = SP_NAME
    ) {
        val mmkv = MMKV.mmkvWithID(filename) ?: return
        if (value == null) {
            mmkv.remove(key)
            return
        }
        mmkv.encode(key, value)
    }


    @JvmOverloads
    @JvmStatic
    fun putList(key: String, value: List<*>, filename: String = SP_NAME) {
        putString(key, JSON.toJSONString(value), filename)
    }

    @JvmStatic
    fun containsKey(key: String, filename: String = SP_NAME): Boolean {
        val mmkv = MMKV.mmkvWithID(filename) ?: return false
        return mmkv.containsKey(key)
    }

    @JvmOverloads
    @JvmStatic
    fun remove(key: String, filename: String = SP_NAME) {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null) {
            llloge("mmkv = null!!!")
            return
        }
        mmkv.removeValueForKey(key)
    }

    @JvmOverloads
    @JvmStatic
    fun clearAll(filename: String = SP_NAME) {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null) {
            llloge("mmkv = null!!!")
            return
        }
        mmkv.clearAll()
    }


}
