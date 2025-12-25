package com.kito.data.remote

import com.kito.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class SupabaseAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader(
                "Authorization",
                "Bearer ${BuildConfig.SUPABASE_ANON_KEY}"
            )
            .build()

        val response = chain.proceed(request)

        return response
    }
}
