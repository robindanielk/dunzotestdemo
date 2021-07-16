package com.example.dunzotest.util

import android.content.Context
import com.example.dunzotest.R
import retrofit2.HttpException
import java.net.ConnectException

object MainUtil {
    fun getErrorTye(t: Throwable, context: Context): String {
        when (t) {
            is HttpException -> {
                return when (t.code()) {
                    404 -> return getMessage(context, R.string.server_error)
                    500 -> return getMessage(context, R.string.server_error)
                    else -> return getMessage(context, R.string.error_label)
                }
            }
            is ConnectException -> return getMessage(context, R.string.connection_error)
            else -> return getMessage(context, R.string.connection_error)
        }
    }


    fun getMessage(context: Context, id: Int): String {
        return context.getString(id)
    }
}