package com.thirteendollars.singlerent.data.repository.remote

import com.thirteendollars.singlerent.BuildConfig
import com.thirteendollars.singlerent.data.repository.SessionRepository
import com.thirteendollars.singlerent.injection.application.AppScope
import dagger.Lazy
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 18/03/2018.
 * mail: thirteendollars.com@gmail.com
 */

@AppScope
class HttpClientFactory @Inject constructor(private val sessionRepository: Lazy<SessionRepository>) {

    companion object Header {
        const val TOKEN = "access-token"
        const val CONTENT_TYPE = "Content-Type"
    }

    fun create(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logger())
            .addInterceptor(headerAppender())
            .addInterceptor(sessionValidator())
            .build()

    private fun sessionValidator(): Interceptor = Interceptor {
        val response = it.proceed(it.request())
        val requestUrl = response.request().url().toString()
        if (response.code() == 401
                && !requestUrl.contains(UserService.LOGIN_URL)
                && !requestUrl.contains(SessionService.VALIDATE_URL)
                && sessionRepository.get().isRefreshTokenAvailable() ) {
                // try to refresh access token
                sessionRepository.get().validateSession().subscribe(
                                { sessionRepository.get().saveNewAccessToken(it.accessToken) },
                                { it.printStackTrace() }
                )
                val newRequest = it.request().newBuilder().header(Header.TOKEN, sessionRepository.get().getAccessToken().orEmpty())
            it.proceed(newRequest.build())
        }
        else {
            response
        }
    }

    private fun logger():Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        }
        else {
            HttpLoggingInterceptor.Level.NONE
        }
        return interceptor
    }

    private fun headerAppender(): Interceptor = Interceptor {
        val request = it
                .request()
                .newBuilder()
                .addHeader(Header.CONTENT_TYPE,"application/json")
                .addHeader(Header.TOKEN, sessionRepository.get().getAccessToken().orEmpty() )
                .build()
        it.proceed(request)
    }
}
