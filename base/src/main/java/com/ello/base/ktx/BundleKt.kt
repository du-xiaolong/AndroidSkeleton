package com.ello.base.ktx

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * @author dxl
 * @date 2022-11-15  周二
 */

fun Bundle.put(key: String, value: Any?) {
    when (value) {
        null -> remove(key)

        // Scalars
        is Boolean -> putBoolean(key, value)
        is Byte -> putByte(key, value)
        is Char -> putChar(key, value)
        is Double -> putDouble(key, value)
        is Float -> putFloat(key, value)
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is Short -> putShort(key, value)

        // References
        is Bundle -> putBundle(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Parcelable -> putParcelable(key, value)

        // Scalar arrays
        is BooleanArray -> putBooleanArray(key, value)
        is ByteArray -> putByteArray(key, value)
        is CharArray -> putCharArray(key, value)
        is DoubleArray -> putDoubleArray(key, value)
        is FloatArray -> putFloatArray(key, value)
        is IntArray -> putIntArray(key, value)
        is LongArray -> putLongArray(key, value)
        is ShortArray -> putShortArray(key, value)

        // Reference arrays
        is Array<*> -> {
            val componentType = value::class.java.componentType!!
            @Suppress("UNCHECKED_CAST") // Checked by reflection.
            when {
                Parcelable::class.java.isAssignableFrom(componentType) -> {
                    putParcelableArray(key, value as Array<Parcelable>)
                }
                String::class.java.isAssignableFrom(componentType) -> {
                    putStringArray(key, value as Array<String>)
                }
                CharSequence::class.java.isAssignableFrom(componentType) -> {
                    putCharSequenceArray(key, value as Array<CharSequence>)
                }
                else -> {
                    val valueType = componentType.canonicalName
                    throw IllegalArgumentException(
                        "Illegal value array type $valueType for key \"$key\""
                    )
                }
            }
        }

        // Last resort. Also we must check this after Array<*> as all arrays are serializable.
        is Serializable -> putSerializable(key, value)

        else -> {
            val valueType = value.javaClass.canonicalName
            throw IllegalArgumentException("Illegal value type $valueType for key \"$key\"")
        }
    }
}