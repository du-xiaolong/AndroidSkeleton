package com.ello.base.ktx

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.request.RequestOptions
import com.ello.base.R

@JvmOverloads
fun ImageView.loadImage(
    fragment: Fragment? = null,
    activity: Activity? = null,
    context: Context? = null,
    url: String?,
    imageOptions: ImageOptions? = getDefaultImageOptions()
) {
    when {
        fragment != null -> Glide.with(fragment)
        activity != null -> Glide.with(activity)
        context != null -> Glide.with(context)
        else -> Glide.with(this)
    }.load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(requestOptions(imageOptions))
        .into(this)
}

@JvmOverloads
fun ImageView.loadImage(
    fragment: Fragment? = null,
    activity: Activity? = null,
    context: Context? = null,
    uri: Uri?,
    imageOptions: ImageOptions? = getDefaultImageOptions()
) {
    when {
        fragment != null -> Glide.with(fragment)
        activity != null -> Glide.with(activity)
        context != null -> Glide.with(context)
        else -> Glide.with(this)
    }.load(uri)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(requestOptions(imageOptions))
        .into(this)
}

@JvmOverloads
fun ImageView.loadImage(
    fragment: Fragment? = null,
    activity: Activity? = null,
    context: Context? = null,
    @DrawableRes resource: Int,
    imageOptions: ImageOptions? = getDefaultImageOptions()
) {
    when {
        fragment != null -> Glide.with(fragment)
        activity != null -> Glide.with(activity)
        context != null -> Glide.with(context)
        else -> Glide.with(this)
    }.load(resource)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(requestOptions(imageOptions))
        .into(this)
}


fun getDefaultImageOptions() = ImageOptions().apply {
    placeholder = R.drawable.image_holder
    error = R.drawable.image_holder
//    fallback = R.drawable.holder_fallback_square
    centerCrop = true
}


@SuppressLint("CheckResult")
private fun requestOptions(imageOptions: ImageOptions?) = RequestOptions().apply {
    imageOptions?.let {
        if (it.placeholderDrawable != null) {
            placeholder(it.placeholderDrawable)
        } else {
            placeholder(it.placeholder)
        }
        error(it.error)
        fallback(it.fallback)

        val transforms = mutableListOf<Transformation<Bitmap>>()

        if (it.circleCrop) {
            transforms.add(CircleCrop())
        }
        if (it.centerCrop) {
            transforms.add(CenterCrop())
        }
        if (it.centerInside) {
            transforms.add(CenterInside())
        }
        if (it.fitCenter) {
            transforms.add(FitCenter())
        }
        if (it.cornersRadius > 0) {
            transforms.add(RoundedCorners(it.cornersRadius))
        }
        transform(*transforms.toTypedArray())

        imageOptions.size?.let { size -> override(size.width, size.height) }
    }
}

class ImageOptions {
    //    var placeholder = R.drawable.view_image_loading         // 加载占位图
//    var error = R.drawable.view_image_loading_fail               // 错误占位图
    var placeholder = 0         // 加载占位图
    var placeholderDrawable: Drawable? = null
    var error = 0               // 错误占位图
    var fallback = 0            // null占位图
    var cornersRadius = 0       // 圆角半径
    var circleCrop = false      // 是否裁剪为圆形
    var size: Size? = null
    var centerCrop = false
    var centerInside = false
    var fitCenter = false
    var cache: Boolean = true    //允许缓存
    var blurRadius = 0 //高斯模糊

    data class Size(var width: Int, var height: Int)
}