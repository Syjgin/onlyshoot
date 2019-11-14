package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.viewmodel.SettingsViewModel

class SettingsFragment : BaseFragment<SettingsViewModel>(SettingsViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = SettingsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun fragmentTitle() = R.string.settings

    override fun fragmentLayout() = R.layout.fragment_settings

    override fun hasBackButton() = true

    override fun parseArguments(args: Bundle?) {}
}