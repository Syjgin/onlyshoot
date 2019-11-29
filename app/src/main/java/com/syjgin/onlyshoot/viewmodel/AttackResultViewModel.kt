package com.syjgin.onlyshoot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Attack
import com.syjgin.onlyshoot.model.AttackResult

class AttackResultViewModel : BaseViewModel() {
    private val resultData = MutableLiveData<List<AttackResult>>()

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    fun load(attacks: List<Attack>) {
        Log.d("AttackResult", attacks.toString())
    }

    fun getResultLiveData(): LiveData<List<AttackResult>> {
        return resultData
    }
}