package com.thirteendollars.singlerent.injection.application

import android.content.Context
import android.location.LocationManager
import android.os.Handler
import com.securepreferences.SecurePreferences
import com.thirteendollars.singlerent.App
import com.thirteendollars.singlerent.BuildConfig
import com.thirteendollars.singlerent.bus.EventBus
import com.thirteendollars.singlerent.data.repository.remote.*
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * Created by Damian Nowakowski on 17/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@Module
class AppModule(private val app: App) {

    @Provides
    @AppScope
    fun provideApp(): App = app

    @Provides
    @AppScope
    fun provideHandler(): Handler = Handler()

    @Provides
    @AppScope
    fun provideBus(): EventBus = EventBus()

    @Provides
    @AppScope
    fun provideLocationManager(app: App): LocationManager = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    @AppScope
    fun provideSecurePreferences(): SecurePreferences = SecurePreferences(app)

    @Provides
    @AppScope
    fun provideRetrofit(httpClientFactory: HttpClientFactory, gsonFactory: GsonFactory): Retrofit = Retrofit.Builder()
            .client(httpClientFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(gsonFactory.create())
            .baseUrl(BuildConfig.ENDPOINT)
            .build()

    @Provides
    @AppScope
    fun provideUserService(retrofit: Retrofit): UserService = retrofit.create(UserService::class.java)

    @Provides
    @AppScope
    fun provideSessionService(retrofit: Retrofit): SessionService = retrofit.create(SessionService::class.java)

    @Provides
    @AppScope
    fun providePoiService(retrofit: Retrofit): PoiService = retrofit.create(PoiService::class.java)

    @Provides
    @AppScope
    fun provideDispositionService(retrofit: Retrofit): DispositionService = retrofit.create(DispositionService::class.java)

}