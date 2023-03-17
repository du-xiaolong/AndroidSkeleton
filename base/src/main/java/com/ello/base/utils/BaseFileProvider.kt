package com.ello.base.utils

import androidx.core.content.FileProvider
import com.tencent.mmkv.MMKV

/**
 * 使用provider获取全局application
 * @author dxl
 * @date 2023/3/17
 */
class BaseFileProvider : FileProvider() {

    override fun onCreate(): Boolean {
        AppUtils.appContext = context?.applicationContext!!
        init()
        return super.onCreate()
    }

    private fun init() {
        //初始化MMKV
        MMKV.initialize(context)
    }

}