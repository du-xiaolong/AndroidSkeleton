package com.ello.base.ktx

import com.alibaba.fastjson.JSONException
import com.google.gson.JsonSyntaxException
import com.ello.base.net.ApiException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

/**
 * @author dxl
 * @date 2023-02-06
 */
fun Throwable?.format() = (when {
    this is ApiException -> {
        if (msg?.contains("无token") == true) "请先登录" else this.msg
    }
    this is SocketTimeoutException || this?.message?.contains("timeout") == true || this is TimeoutException -> {
        "请求超时"
    }
    this is HttpException && this.code() == 500 -> {
        "服务器开小差了，稍后再试吧~"
    }
    // 网络请求失败
    this is ConnectException
            || this is UnknownHostException
            || this is HttpException
            || this is SSLHandshakeException
            || this is InterruptedIOException
            || this is SSLException ->
        "当前网络不稳定，请刷新重试～"
    // 数据解析错误
    this is JSONException || this is JsonSyntaxException || this?.cause is JSONException -> "数据解析异常，请刷新重试～"
    // 其他错误
    else -> this?.message
} ?: "请求失败").also {
    llloge(this?.message)
}


fun Throwable.isNetError() =
    // 网络请求失败
    this is ApiException
            || this is ConnectException
            || this is SocketTimeoutException
            || this is UnknownHostException
            || this is HttpException
            || this is SSLHandshakeException
            || this is SSLException
            || (this is InterruptedIOException && this.message?.contains("timeout") == true)

