package com.ello.base.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ello.base.utils.dp
import kotlin.math.roundToInt

/**
 * recyclerView分割线
 * @author dxl
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

class LinearItemDecoration(
    span: Int,
    leftPadding: Int,
    rightPadding: Int,
    color: Int,
    show: Boolean
) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable
    private val mShowLastLine: Boolean
    private var mSpanSpace = 2
    private val mLeftPadding: Int
    private val mRightPadding: Int

    init {
        mSpanSpace = span
        mShowLastLine = show
        mLeftPadding = leftPadding
        mRightPadding = rightPadding
        mDivider = ColorDrawable(color)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val count = if (mShowLastLine) parent.adapter!!
            .itemCount else parent.adapter!!.itemCount - 1
        if (isVertical(parent)) {
            if (parent.getChildAdapterPosition(view) < count) {
                outRect[0, 0, 0] = mSpanSpace
            } else {
                outRect[0, 0, 0] = 0
            }
        } else {
            if (parent.getChildAdapterPosition(view) < count) {
                outRect[0, 0, mSpanSpace] = 0
            } else {
                outRect[0, 0, 0] = 0
            }
        }
    }

    private fun isVertical(parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val orientation = layoutManager.orientation
            return orientation == LinearLayoutManager.VERTICAL
        }
        return false
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (isVertical(parent)) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft + mLeftPadding
        val right = parent.width - parent.paddingRight - mRightPadding
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top =
                child.bottom + params.bottomMargin + child.translationY.roundToInt()
            val bottom = top + mSpanSpace
            val count = if (mShowLastLine) parent.adapter!!
                .itemCount else parent.adapter!!.itemCount - 1
            if (i < count) {
                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            } else {
                mDivider.setBounds(left, top, right, top)
                mDivider.draw(c)
            }
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left =
                child.right + params.rightMargin + Math.round(ViewCompat.getTranslationX(child))
            val right = left + mSpanSpace
            val count = if (mShowLastLine) parent.adapter!!
                .itemCount else parent.adapter!!.itemCount - 1
            if (i < count) {
                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }

    /**
     * Builder模式
     */
    class Builder(private val mContext: Context) {
        private var mSpanSpace: Int
        private var mShowLastLine: Boolean
        private var mLeftPadding: Int
        private var mRightPadding: Int
        private var mColor: Int

        init {
            mSpanSpace = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                1f,
                mContext.resources.displayMetrics
            ).toInt()
            mLeftPadding = 0
            mRightPadding = 0
            mShowLastLine = false
            mColor = Color.BLACK
        }

        /**
         * 设置分割线宽（高）度
         */
        fun setSpan(pixels: Int): Builder {
            mSpanSpace = pixels
            return this
        }

        /**
         * 设置左右间距
         */
        fun setPadding(pixels: Int): Builder {
            setLeftPadding(pixels)
            setRightPadding(pixels)
            return this
        }

        /**
         * 设置左间距
         */
        fun setLeftPadding(pixelPadding: Int): Builder {
            mLeftPadding = pixelPadding
            return this
        }

        /**
         * 设置右间距
         */
        fun setRightPadding(pixelPadding: Int): Builder {
            mRightPadding = pixelPadding
            return this
        }

        /**
         * 通过资源id设置颜色
         */
        fun setColorResource(@ColorRes resource: Int): Builder {
            setColor(ContextCompat.getColor(mContext, resource))
            return this
        }

        /**
         * 设置颜色
         */
        fun setColor(@ColorInt color: Int): Builder {
            mColor = color
            return this
        }

        /**
         * 是否最后一条显示分割线
         */
        fun setShowLastLine(show: Boolean): Builder {
            mShowLastLine = show
            return this
        }

        /**
         * Instantiates a LinearItemDecoration with the specified parameters.
         *
         * @return a properly initialized LinearItemDecoration instance
         */
        fun build(): LinearItemDecoration {
            return LinearItemDecoration(
                mSpanSpace,
                mLeftPadding,
                mRightPadding,
                mColor,
                mShowLastLine
            )
        }
    }
}