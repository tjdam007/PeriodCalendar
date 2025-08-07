package com.mjandroiddev.periodcalendar.billing

import android.content.Context
import com.mjandroiddev.periodcalendar.firebase.AnalyticsLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {
    
    @Provides
    @Singleton
    fun provideBillingManager(
        @ApplicationContext context: Context,
        analyticsLogger: AnalyticsLogger
    ): BillingManager {
        return BillingManager(context, analyticsLogger)
    }
}