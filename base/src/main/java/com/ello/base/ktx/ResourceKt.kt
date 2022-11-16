package com.ello.base.ktx

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat


@ColorInt
fun Context.getColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Context.getColorStateList(@ColorRes id: Int): ColorStateList? =
    ContextCompat.getColorStateList(this, id)

fun Context.getDrawable(@DrawableRes id: Int): Drawable? =
    ContextCompat.getDrawable(this, id)

fun Context.getColor(@ColorRes id: Int, theme: Resources.Theme) =
    ResourcesCompat.getColor(resources, id, theme)

fun Context.getColorStateList(
    @ColorRes id: Int,
    theme: Resources.Theme
): ColorStateList? =
    ResourcesCompat.getColorStateList(resources, id, theme)

fun Context.getFont(@FontRes id: Int): Typeface? = ResourcesCompat.getFont(this, id)

fun Context.getCachedFont(@FontRes id: Int): Typeface? =
    ResourcesCompat.getCachedFont(this, id)


fun Context.getDimension(@DimenRes id: Int) = resources.getDimension(id)

fun Context.getInt(@IntegerRes id: Int) = resources.getInteger(id)

fun Context.getIntArray(@ArrayRes id: Int) = resources.getIntArray(id)

fun Context.getBoolean(@BoolRes id: Int) = resources.getBoolean(id)

fun Context.getAnimation(@AnimatorRes @AnimRes id: Int) = resources.getAnimation(id)

fun Context.getText(@StringRes id: Int, defValue: CharSequence? = null): CharSequence? =
    resources.getText(id, defValue)

fun Context.getStringArray(@ArrayRes id: Int): Array<String> = resources.getStringArray(id)

fun Context.getTextArray(@ArrayRes id: Int): Array<CharSequence> = resources.getTextArray(id)

fun Context.getFloat(@DimenRes id: Int) = ResourcesCompat.getFloat(resources, id)

fun Context.getXml(@XmlRes id: Int) = resources.getXml(id)



