package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment

class AddEditFightFragment : Fragment() {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditFightFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}