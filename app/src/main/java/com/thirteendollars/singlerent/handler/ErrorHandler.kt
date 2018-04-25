package com.thirteendollars.singlerent.handler

import com.google.gson.Gson
import com.thirteendollars.singlerent.data.response.ErrorResponse
import com.thirteendollars.singlerent.injection.application.AppScope
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 05/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

@AppScope
class ErrorHandler @Inject constructor() {

    fun getError(exception: Throwable?): Error = Error( getUserMessage(exception), is401(exception) )

    private fun getUserMessage(exception: Throwable?): String {

        if( exception is HttpException) {
            try {
                val body = exception.response().errorBody()?.string()
                val adapter = Gson().getAdapter(ErrorResponse::class.java)
                val errorResponse = adapter.fromJson( body )
                return errorResponse.userMessage
            }
            catch (e: Exception) {
                e.printStackTrace()
                return exception.message()
            }

        }
        else {
            return exception?.message.orEmpty()
        }
    }

    private fun is401(exception: Throwable?): Boolean {
        return if( exception is HttpException) {
            exception.code()==401
        }
        else false
    }

    class Error(val message:String?, val is401: Boolean)
}