package com.ello.base.view

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 带下划线的textView
 * @author dxl
 */
class UnderLinedTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    init {
        paint.flags = Paint.UNDERLINE_TEXT_FLAG
        paint.isAntiAlias = true
    }
}