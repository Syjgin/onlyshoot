package com.syjgin.onlyshoot.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.syjgin.onlyshoot.viewmodel.BaseViewModel

abstract class BaseFragment<B: BaseViewModel>(private val viewModelType: Class<B>) : Fragment() {
    @StringRes abstract fun fragmentTitle() : Int
    abstract fun parseArguments(args: Bundle)
    @LayoutRes abstract fun fragmentLayout() : Int
    abstract fun hasBackButton() : Boolean
    open fun getProviderFromFragment(): Boolean {
        return true
    }

    protected var viewModel : B? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(arguments == null)
            return
        parseArguments(arguments!!)
    }

    override fun onResume() {
        super.onResume()
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
        viewModel = if (getProviderFromFragment()) {
            ViewModelProviders.of(this).get(viewModelType)
        } else {
            ViewModelProviders.of(activity!!).get(viewModelType)
        }
        viewModel?.onCreate()
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(hasBackButton())
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(hasBackButton())
        setHasOptionsMenu(true)
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        })
    }

    open fun onBackPressed() {
        viewModel?.goBack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            viewModel?.goBack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}