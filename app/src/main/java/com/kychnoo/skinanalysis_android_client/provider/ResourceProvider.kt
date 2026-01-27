package com.kychnoo.skinanalysis_android_client.provider

import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg args: Any): String
    fun getHttpErrorMessage(code: Int): String
    fun getErrorMessage(exception: Exception): String
}