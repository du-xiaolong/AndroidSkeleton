package com.ello.base.ktx

import android.graphics.Color
import android.view.Gravity
import com.blankj.utilcode.util.ToastUtils

/**
 * 吐司
 * @author dxl
 */
fun String.toast() {
    ToastUtils.make()
        .setBgColor(Color.parseColor("#999999"))
        .setTextColor(Color.WHITE)
        .setDurationIsLong(true)
        .show(this)
}

fun String.toast(yOffset: Int) {
    ToastUtils.make()
        .setBgColor(Color.parseColor("#999999"))
        .setTextColor(Color.WHITE)
        .setGravity(Gravity.BOTTOM, 0, yOffset)
        .setDurationIsLong(true)
        .show(this)
}

fun String.toastCenter() {
    ToastUtils.make()
        .setBgColor(Color.parseColor("#999999"))
        .setTextColor(Color.WHITE)
        .setGravity(Gravity.CENTER, 0, 0)
        .setDurationIsLong(true)
        .show(this)
}
