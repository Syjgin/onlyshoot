package com.syjgin.onlyshoot.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

    private val migration12 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE SquadUnit ADD COLUMN evasionModifier INTEGER default 0 NOT NULL")
            database.execSQL("ALTER TABLE UnitArchetype ADD COLUMN evasionModifier INTEGER default 0 NOT NULL")
        }
    }

    fun getAppComponent() : AppComponent {
        return component
    }

    fun getNavigatorHolder() : NavigatorHolder {
        return cicerone.navigatorHolder
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val database =
            Room.databaseBuilder(this, Database::class.java, DB_NAME).addMigrations(migration12)
                .build()
        cicerone = Cicerone.create()
        component = DaggerAppComponent.builder()
            .databaseModule(DatabaseModule(database))
            .navigationModule(NavigationModule(cicerone.router)
        ).build()
    }
}