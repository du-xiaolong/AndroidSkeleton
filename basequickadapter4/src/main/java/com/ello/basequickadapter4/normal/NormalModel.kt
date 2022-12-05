package com.ello.basequickadapter4.normal

import android.graphics.Color

/**
 * @author dxl
 * @date 2022-12-05  周一
 */
data class NormalModel(val type: Int, val text: String) {
    val color: Int
        get() {
            return when (type) {
                0 -> Color.RED
                1 -> Color.GREEN
                2 -> Color.BLUE
                3 -> Color.LTGRAY
                else -> Color.YELLOW
            }
        }
}