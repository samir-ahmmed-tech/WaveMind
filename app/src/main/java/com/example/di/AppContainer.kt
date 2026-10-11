package com.example.di

import android.content.Context
import com.example.data.local.AppDao
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager

/**
 * AppContainer provides dependency injection for the entire application.
 * Manages thread-safe singletons for Room Database and DataStore Preferences
 * to prevent duplicate instances, SQLite database locks, and startup hangs.
 */
interface AppContainer {
    val database: AppDatabase
    val appDao: AppDao
    val preferencesManager: PreferencesManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    // Lazy application-scoped singleton for Room Database
    override val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    // Cached DAO instance derived from the database singleton
    override val appDao: AppDao by lazy {
        database.appDao()
    }

    // Lazy application-scoped singleton for DataStore Preferences
    override val preferencesManager: PreferencesManager by lazy {
        PreferencesManager.getInstance(context)
    }
}
