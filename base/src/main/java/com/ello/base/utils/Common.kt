package com.ello.base.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.*
import com.ello.base.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


//数据不能为空
fun <T> T?.requireNotNull(lazyMessage: (() -> Any)? = null): T {
    if (this == null) {
        val message = lazyMessage?.let { it().toString() } ?: "数据获取失败！"
        throw IllegalArgumentException(message)
    } else {
        return this
    }
}

//字符串不能为空
fun String?.requireNotEmpty(lazyMessage: (() -> Any)? = null): String {
    if (this.isNullOrEmpty()) {
        val message = lazyMessage?.let { it().toString() } ?: "数据异常！错误码778"
        throw IllegalArgumentException(message)
    } else {
        return this
    }
}

private const val THROTTLE_WINDOW = 600

/**
 * 防抖点击
 * 添加了点击效果
 */
fun <T : View> T.clickAnim(action: (value: T) -> Unit) {
    setOnClickListener {
        val key = R.id.video_view_throttle_first_id
        val windowStartTime = getTag(key) as? Long ?: 0
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - windowStartTime
        if (delta >= THROTTLE_WINDOW) {
            setTag(key, currentTime)
            val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 0.3f, 1f)

            objectAnimator.interpolator = DecelerateInterpolator()
            objectAnimator.duration = 300
            objectAnimator.start()

            @Suppress("UNCHECKED_CAST")
            action.invoke(it as T)
        }
    }
}

/**
 * 防抖点击
 * 没有点击效果
 */
fun View.click(action: (value: View) -> Unit) {
    setOnClickListener {
        val key = R.id.video_view_throttle_first_id
        val windowStartTime = getTag(key) as? Long ?: 0
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - windowStartTime
        if (delta >= THROTTLE_WINDOW) {
            setTag(key, currentTime)
            action.invoke(it)
        }
    }
}




@SuppressLint("ClickableViewAccessibility")
fun View?.setPressState() {
    this ?: return
    isClickable = true
    val onPressTouchListener = View.OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.alpha = 0.3f
            }
            MotionEvent.ACTION_CANCEL -> {
                v.alpha = 1f
            }
            MotionEvent.ACTION_UP -> {
                v.alpha = 1f
            }
        }
        false
    }
    this.setOnTouchListener(onPressTouchListener);
}

//一个全局的ViewModel
@MainThread
public inline fun <reified VM : ViewModel> ComponentActivity.applicationViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }
    return ViewModelLazy(VM::class, { viewModelStore }, factoryPromise)
}

val viewModelStore: ViewModelStore by lazy {
    ViewModelStore()
}

/**
 * onStart开始时回调
 *
 * @param invoke
 * @receiver
 */
fun LifecycleOwner.repeatOnStart(invoke: suspend () -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            invoke.invoke()
        }
    }
}

/**
 * onResume开始时回调
 *
 * @param invoke
 * @receiver
 */
fun LifecycleOwner.repeatOnResume(invoke: suspend () -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            invoke.invoke()
        }
    }
}

/**
 * 设置黑白模式
 */
fun View.setUiBlackMode(isBlackMode: Boolean) {
    if (isBlackMode) {
        //设置了黑白模式
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(cm)
        setLayerType(View.LAYER_TYPE_HARDWARE, paint)
    } else {
        //清除黑白模式
        setLayerType(View.LAYER_TYPE_NONE, null)
    }
}


/**
 * 沉浸状态栏
 */
fun Activity.immerseStatus() {
    window.statusBarColor = Color.TRANSPARENT
    WindowCompat.setDecorFitsSystemWindows(window, false)
}

/**
 * 全屏，状态栏也没有
 */
fun Activity.fullScreen() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowCompat.getInsetsController(window, window.decorView)
        .apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
}

val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

/**
 * 延迟执行
 */
fun LifecycleOwner.delayDo(timeMills: Long, block: (() -> Unit)? = null) {
    lifecycleScope.launch {
        delay(timeMills)
        runCatching {
            block?.invoke()
        }
    }
}



