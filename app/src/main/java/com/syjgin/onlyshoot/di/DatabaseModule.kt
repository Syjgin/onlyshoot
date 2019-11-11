package com.syjgin.onlyshoot.di

import com.syjgin.onlyshoot.model.Database
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(private val database: Database) {
    @Provides
    @Singleton
    fun providesDatabase() : Database = database
}