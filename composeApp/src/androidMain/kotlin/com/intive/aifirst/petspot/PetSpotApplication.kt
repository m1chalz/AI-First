package com.intive.aifirst.petspot

import android.app.Application
import com.intive.aifirst.petspot.di.dataModule
import com.intive.aifirst.petspot.di.domainModule
import com.intive.aifirst.petspot.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Custom Application class for PetSpot Android app.
 *
 * Initializes Koin dependency injection framework at application startup.
 * All dependencies must be configured before any screen/activity is created.
 *
 * This class is referenced in AndroidManifest.xml via android:name=".PetSpotApplication"
 */
class PetSpotApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin dependency injection
        startKoin {
            // Enable Android-specific logging (visible in Logcat)
            androidLogger(Level.ERROR)

            // Provide Android context to Koin
            androidContext(this@PetSpotApplication)

            // Register all modules
            modules(
                // Shared domain dependencies (use cases, business logic)
                domainModule,
                // Android data layer (repositories, APIs, databases)
                dataModule,
                // Android ViewModels for UI
                viewModelModule,
            )
        }
    }
}
