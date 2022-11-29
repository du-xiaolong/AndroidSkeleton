package com.ello.androidskeleton.displayCuts

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.blankj.utilcode.util.BarUtils
import com.ello.androidskeleton.databinding.ActivityDisplayCutsBinding

/**
 * 处理刘海屏的适配
 * @author dxl
 * @date 2022-11-29  周二
 */
class DisplayCutsActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityDisplayCutsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //在高版本上，必须加这句，否则横屏时，刘海区域还是白色
        WindowCompat.setDecorFitsSystemWindows(window, false)


        BarUtils.getStatusBarHeight()

//      可以用主题设置，也可以用以下代码设置。当前使用的主题设置
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val layoutParams = window.attributes
//            layoutParams.layoutInDisplayCutoutMode =
//                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//            window.attributes = layoutParams
//        }


        viewBinding =
            ActivityDisplayCutsBinding.inflate(layoutInflater).also { setContentView(it.root) }

        //因为设置了FitsSystemWindows = false， 所以需要空出来状态栏高度
        viewBinding.root.layoutParams =
            (viewBinding.root.layoutParams as MarginLayoutParams).apply {
                setMargins(0, BarUtils.getStatusBarHeight(), 0, 0)
            }

        //获取安全区域
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.decorView.post {
                window.decorView.rootWindowInsets?.displayCutout?.let { displayCutout ->
                    val safeInsetLeft = displayCutout.safeInsetLeft
                    val safeInsetRight = displayCutout.safeInsetRight
                    val safeInsetTop = displayCutout.safeInsetTop
                    val safeInsetBottom = displayCutout.safeInsetBottom
                    Log.d(
                        "displayCuts",
                        "onCreate: left = $safeInsetLeft, right = $safeInsetRight, top = $safeInsetTop, bottom = $safeInsetBottom"
                    )

                    if (safeInsetLeft > 0 ){
                        //左边有刘海
                        viewBinding.btnCenterLeft.layoutParams = (viewBinding.btnCenterLeft.layoutParams as MarginLayoutParams).apply {
                            leftMargin = safeInsetLeft
                        }
                    }
                    if (safeInsetRight > 0 ){
                        //右边有刘海
                        viewBinding.btnCenterRight.layoutParams = (viewBinding.btnCenterRight.layoutParams as MarginLayoutParams).apply {
                            rightMargin = safeInsetRight
                        }
                    }

                }




            }

        }


    }

}