package com.ello.base.ktx

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author dxl
 * @date 2022-11-15  周二
 */

fun <T> argument(key: String? = null) = FragmentArgumentNullableProperty<T>(key)

fun <T> argument(key: String? = null, defaultValue: T) = FragmentArgumentProperty(key, defaultValue)

inline fun <reified T> Fragment.lazyArgument(key: String) = lazy {
    arguments?.get(key) as? T?
}

inline fun <reified T> Fragment.lazyArgument(key: String, defaultValue: T) = lazy {
    arguments?.get(key) as? T ?: defaultValue
}

inline fun <reified T : Activity> Fragment.startActivity(vararg pairs: Pair<String, Any?>) {
    startActivity(Intent(requireActivity(), T::class.java).apply {
        putExtras(bundleOf(*pairs))
    })
}


fun Fragment.startActivity(cls: Class<*>, vararg pairs: Pair<String, Any?>) {
    startActivity(Intent(requireActivity(), cls).apply {
        putExtras(bundleOf(*pairs))
    })
}


fun Fragment.finish() {
    requireActivity().finish()
}

fun Fragment.finishWithResult(vararg pairs: Pair<String, Any?>) {
    requireActivity().run {
        setResult(Activity.RESULT_OK, Intent().putExtras(bundleOf(*pairs)))
        finish()
    }
}



class FragmentArgumentProperty<V>(
    private val key: String?,
    private val defaultValue: V
) : ReadWriteProperty<Fragment, V> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
        return (thisRef.arguments?.get(key ?: property.name) as? V) ?: defaultValue
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: V) {
        val bundle = thisRef.arguments ?: Bundle().also { thisRef.arguments = it }
        bundle.put(key ?: property.name, value)
    }

}

class FragmentArgumentNullableProperty<V>(
    private val key: String?
) : ReadWriteProperty<Fragment, V?> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): V? {
        return thisRef.arguments?.get(key ?: property.name) as? V
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: V?) {
        val bundle = thisRef.arguments ?: Bundle().also { thisRef.arguments = it }
        val k = key ?: property.name
        bundle.put(key ?: property.name, value)
    }
}