package com.ello.androidskeleton.displayCuts

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.ello.androidskeleton.databinding.ActivityDisplayCutsBinding
import com.ello.base.ktx.fitDisplayCuts

/**
 * 处理刘海屏的适配
 * @author dxl
 * @date 2022-11-29  周二
 */
class DisplayCuts2Activity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityDisplayCutsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding =
            ActivityDisplayCutsBinding.inflate(layoutInflater).also { setContentView(it.root) }

        fitDisplayCuts {
            if (it.safeInsetLeft > 0 ){
                //左边有刘海
                viewBinding.btnCenterLeft.layoutParams = (viewBinding.btnCenterLeft.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    leftMargin = it.safeInsetLeft
                }
            }
            if (it.safeInsetRight > 0 ){
                //右边有刘海
                viewBinding.btnCenterRight.layoutParams = (viewBinding.btnCenterRight.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    rightMargin = it.safeInsetRight
                }
            }
        }

    }

}