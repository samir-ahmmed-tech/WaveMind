package com.example

import android.app.Application
import com.example.di.AppContainer
import com.example.di.DefaultAppContainer

/**
 * MainApplication initializes the dependency injection container
 * and configures application-wide singletons for Room Database and DataStore.
 */
class MainApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
