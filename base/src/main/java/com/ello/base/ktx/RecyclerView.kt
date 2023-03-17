package com.ello.base.ktx

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.ello.base.utils.dp
import com.ello.base.view.LinearItemDecoration

/**
 *
 * @author dxl
 * @date 2023-02-06
 */

/**
 * RecyclerView添加分割线
 */
fun RecyclerView.addDivider(
    paddingLeftDp: Float = 0f,
    paddingRightDp: Float = 0f,
    dividerHeightPx: Int = 1,
    @ColorInt dividerColor: Int = Color.parseColor("#28979797"),
    showLastLine: Boolean = false
) {
    this.addItemDecoration(
        LinearItemDecoration.Builder(context)
            .setSpan(dividerHeightPx)
            .setLeftPadding(paddingLeftDp.dp)
            .setRightPadding(paddingRightDp.dp)
            .setColor(dividerColor)
            .setShowLastLine(showLastLine)
            .build()
    )
}