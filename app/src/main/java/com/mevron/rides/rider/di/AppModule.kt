package com.mevron.rides.rider.di

import android.content.Context
import com.mevron.rides.rider.App
import com.mevron.rides.rider.remote.MevronAPI
import com.mevron.rides.rider.remote.MevronRepo
import com.mevron.rides.rider.util.Constants.BASE_URL
import com.mevron.rides.rider.util.Constants.SHARED_PREF_KEY
import com.mevron.rides.rider.util.Constants.TOKEN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Singleton
    @Provides
    fun provideApi(): MevronAPI {

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder().followRedirects(true)
            .retryOnConnectionFailure(true)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                  val sPref= App.ApplicationContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
                   val token = sPref.getString(TOKEN, null)
                val newRequest: Request = chain.request().newBuilder()
                     .addHeader("Authorization", "Bearer $token")
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(newRequest)
                // return response
            }).readTimeout(180, TimeUnit.SECONDS)
            .connectTimeout(180, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(BASE_URL)
            .build()
            .create(MevronAPI::class.java)
    }


    @Singleton
    @Provides
    fun mainReop(api: MevronAPI): MevronRepo {
        return MevronRepo(api = api)
    }

}
