package com.ello.base.net

import java.lang.RuntimeException

/**
 * 接口通用异常
 * @author dxl
 * @date 2022-03-21
 */
class ApiException(val code: Int? = -1, val msg: String? = "", val status: Boolean = false) :
    RuntimeException() {
    constructor(result: IApi<*>) : this(
        code = result.code ?: -1,
        msg = if (result.msg.isNullOrBlank()) "数据异常" else result.msg,
        status = false
    )

    override fun toString(): String {
        return "ApiException(code=$code, msg=$msg, status=$status)"
    }


}