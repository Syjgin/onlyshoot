package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.R

class FightListFragment : Fragment() {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = FightListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fight_list, container, false)
    }
}