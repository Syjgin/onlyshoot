package com.syjgin.onlyshoot.utils

import android.os.Bundle
import androidx.annotation.StringRes
import com.syjgin.onlyshoot.navigation.BundleKeys

object AddEditUtils {
    @StringRes fun getAddEditFragmentTitle(bundle: Bundle?, @StringRes addTitle: Int, @StringRes editTItile: Int) : Int {
        if(bundle == null) {
            return addTitle
        }
        val isAdd = bundle.getBoolean(BundleKeys.AddFlavor.name, false)
        return if(isAdd) addTitle else editTItile
    }
}