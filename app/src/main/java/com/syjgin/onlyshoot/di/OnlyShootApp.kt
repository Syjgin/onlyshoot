package com.syjgin.onlyshoot.di

import android.app.Application
import androidx.room.Room
import com.syjgin.onlyshoot.model.Database
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

class OnlyShootApp : Application() {
    companion object {
        const val DB_NAME : String = "ShootDatabase"
        private lateinit var instance: OnlyShootApp
        fun getInstance() : OnlyShootApp {
            return instance
        }
    }

    private lateinit var component : AppComponent
    private lateinit var cicerone: Cicerone<Router>

    fun getAppComponent() : AppComponent {
        return component
    }

    fun getNavigatorHolder() : NavigatorHolder {
        return cicerone.navigatorHolder
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val database = Room.databaseBuilder(this, Database::class.java, DB_NAME).build()
        cicerone = Cicerone.create()
        component = DaggerAppComponent.builder()
            .databaseModule(DatabaseModule(database))
            .navigationModule(NavigationModule(cicerone.router)
        ).build()
    }
}