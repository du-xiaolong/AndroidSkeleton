package com.ello.base.ktx

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author dxl
 * @date 2022-11-15  周二
 */

//属性委托
fun <T> intentExtra(key: String? = null) = ActivityIntentExtraNullableProperty<T>(key)

fun <T> intentExtra(key: String? = null, defaultValue: T) =
    ActivityIntentExtraProperty(key, defaultValue)

inline fun <reified T : Activity> Activity.startActivity(vararg pairs: Pair<String, Any?>) {
    startActivity(Intent(this, T::class.java).apply {
        putExtras(bundleOf(*pairs))
    })
}

fun Activity.finishWithResult(vararg pairs: Pair<String, Any?>) {
    setResult(Activity.RESULT_OK, Intent().putExtras(bundleOf(*pairs)))
    finish()
}

class ActivityIntentExtraProperty<V>(
    private val key: String?,
    private val defaultValue: V
) : ReadWriteProperty<Activity, V> {

    override fun getValue(thisRef: Activity, property: KProperty<*>): V {
        return thisRef.intent?.get(key ?: property.name, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Activity, property: KProperty<*>, value: V) {
        thisRef.intent.put(key ?: property.name, value)
    }

}

class ActivityIntentExtraNullableProperty<V>(
    private val key: String?
) : ReadWriteProperty<Activity, V?> {

    override fun getValue(thisRef: Activity, property: KProperty<*>): V? {
        return thisRef.intent?.get(key ?: property.name)
    }

    override fun setValue(thisRef: Activity, property: KProperty<*>, value: V?) {
        thisRef.intent.put(key ?: property.name, value)
    }
}




