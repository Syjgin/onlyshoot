package com.syjgin.onlyshoot.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.viewmodel.MainViewModel
import ru.terrakok.cicerone.android.support.SupportAppNavigator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.onCreate()
    }

    override fun onResume() {
        super.onResume()
        OnlyShootApp.getInstance().getNavigatorHolder().setNavigator(SupportAppNavigator(this, R.id.container))
    }

    override fun onPause() {
        super.onPause()
        OnlyShootApp.getInstance().getNavigatorHolder().removeNavigator()
    }
}
