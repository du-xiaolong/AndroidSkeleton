package com.ello.base.ktx

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.DisplayCutout
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import com.blankj.utilcode.util.BarUtils
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author dxl
 * @date 2022-11-15  周二
 */

//属性委托，可空
fun <T> intentExtra(key: String? = null) = ActivityIntentExtraNullableProperty<T>(key)

//属性委托，非空
fun <T> intentExtra(key: String? = null, defaultValue: T) = ActivityIntentExtraProperty(key, defaultValue)

//懒加载，可空
inline fun <reified T> Activity.lazyIntentExtra(key: String) = lazy {
    intent.extras?.get(key) as T?
}
//懒加载，不可空
fun <T> Activity.lazyIntentExtra(key: String, defaultValue: T) = lazy {
    intent.extras?.get(key) ?: defaultValue
}

//启动activity
inline fun <reified T : Activity> Activity.startActivity(vararg pairs: Pair<String, Any?>) {
    startActivity(Intent(this, T::class.java).apply {
        putExtras(bundleOf(*pairs))
    })
}

//结束activity，回传结果
fun Activity.finishWithResult(vararg pairs: Pair<String, Any?>) {
    setResult(Activity.RESULT_OK, Intent().putExtras(bundleOf(*pairs)))
    finish()
}

//属性委托
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

//属性委托
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


/**
 * 适配刘海屏（让刘海区域也显示内容，不要白条，主要针对横屏，会回调一个安全区域，用于根据业务处理控件显示位置）
 */
fun Activity.fitDisplayCuts(onSuccess: ((DisplayCutout) -> Unit)? = null) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val layoutParams = window.attributes
        layoutParams.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = layoutParams
        window.decorView.post {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.decorView.rootWindowInsets?.displayCutout?.let {
                    onSuccess?.invoke(it)
                }
            }
        }
    }
    //因为设置了FitsSystemWindows = false， 所以需要空出来状态栏高度
    val root =
        (window.decorView.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
    root.layoutParams =
        (root.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            setMargins(0, BarUtils.getStatusBarHeight(), 0, 0)
        }

}



