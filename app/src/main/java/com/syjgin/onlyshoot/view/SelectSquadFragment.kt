package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment

class SelectSquadFragment : Fragment() {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = SelectSquadFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}