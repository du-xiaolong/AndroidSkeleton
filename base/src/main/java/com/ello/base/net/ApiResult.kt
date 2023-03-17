package com.ello.base.net

/**
 * 接口通用返回结果
 * @author dxl
 * @date 2023-02-06
 */
interface IApi<T> {
    val msg: String?
    val code: Int?
    val data: T?
    val isSuccess: Boolean
        get() = code == 200 || code == 0
    val isFail: Boolean
        get() = isSuccess.not()
}

inline fun <reified T : IApi<*>> T.requireSuccess(): T {
    if (isFail) throw ApiException(this)
    return this
}
