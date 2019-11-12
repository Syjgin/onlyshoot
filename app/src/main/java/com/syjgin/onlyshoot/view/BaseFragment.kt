package com.syjgin.onlyshoot.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.syjgin.onlyshoot.viewmodel.BaseViewModel

abstract class BaseFragment<B: BaseViewModel>(private val viewModelType: Class<B>) : Fragment() {
    @StringRes abstract fun fragmentTitle() : Int
    abstract fun parseArguments(args: Bundle?)
    @LayoutRes abstract fun fragmentLayout() : Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parseArguments(arguments)
        activity?.title = getString(fragmentTitle())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(fragmentLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProviders.of(this).get(viewModelType)
        viewModel.onCreate()
    }
}