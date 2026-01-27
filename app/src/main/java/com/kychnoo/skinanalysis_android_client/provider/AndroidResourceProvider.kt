package com.kychnoo.skinanalysis_android_client.provider

import android.content.Context
import com.kychnoo.skinanalysis_android_client.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class AndroidResourceProvider(private val context: Context) : ResourceProvider {
    override fun getString(resId: Int): String = context.getString(resId)
    override fun getString(resId: Int, vararg args: Any): String = context.getString(resId, *args)

    override fun getErrorMessage(exception: Exception): String {
        return when (exception) {
            is SocketTimeoutException -> getString(R.string.error_timeout)
            is IOException -> getString(R.string.error_network)
            is UnknownHostException -> getString(R.string.error_no_internet)
            is SSLHandshakeException -> getString(R.string.error_ssl)
            else -> getString(R.string.error_unknown, exception.message ?: "")
        }
    }

    override fun getHttpErrorMessage(code: Int): String {
        return when (code) {
            in 400..499 -> when (code) {
                400 -> getString(R.string.error_bad_request)
                401 -> getString(R.string.error_unauthorized)
                403 -> getString(R.string.error_forbidden)
                404 -> getString(R.string.error_not_found)
                429 -> getString(R.string.error_too_many_requests)
                else -> getString(R.string.error_client, code.toString())
            }
            in 500..599 -> getString(R.string.error_server, code.toString())
            else -> getString(R.string.error_http_unknown, code.toString())
        }
    }
}