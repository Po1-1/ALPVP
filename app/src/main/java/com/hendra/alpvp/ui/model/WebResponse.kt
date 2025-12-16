package com.hendra.alpvp.ui.model

data class WebResponse<T>(
    val data: T,
    val message: String? = null,
    val errors: Any? = null
)