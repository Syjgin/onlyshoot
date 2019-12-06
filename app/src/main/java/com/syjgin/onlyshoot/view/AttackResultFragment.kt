package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Attack
import com.syjgin.onlyshoot.model.AttackResult
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import com.syjgin.onlyshoot.view.adapter.AttackResultAdapter
import com.syjgin.onlyshoot.viewmodel.AttackResultViewModel
import kotlinx.android.synthetic.main.fragment_attack_result.*

class AttackResultFragment : BaseFragment<AttackResultViewModel>(AttackResultViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AttackResultFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var attacks: List<Attack>? = null
    private var defendSquadId: Long = NO_DATA
    private val adapter = AttackResultAdapter()

    override fun fragmentTitle() = R.string.attack_result

    override fun fragmentLayout() = R.layout.fragment_attack_result

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.info_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.info) {
            if (log_container.visibility == View.GONE) {
                log_container.visibility = View.VISIBLE
                attack_result.visibility = View.GONE
            } else {
                log_container.visibility = View.GONE
                attack_result.visibility = View.VISIBLE
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun parseArguments(args: Bundle) {
        attacks = args.getParcelableArrayList(BundleKeys.Attacks.name)
        defendSquadId = args.getLong(BundleKeys.DefendSquadId.name)
    }

    override fun hasBackButton() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attack_result.adapter = adapter
        viewModel?.getResultLiveData()?.observe(this, Observer {
            handleAttackResults(it)
        })
        viewModel?.getLogLiveData()?.observe(this, Observer {
            log.text = it
        })
        if (attacks != null)
            viewModel?.load(attacks!!, defendSquadId)
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel?.goBack()
            }
        })
    }

    private fun handleAttackResults(results: List<AttackResult>) {
        progress_bar.visibility = View.GONE
        adapter.addData(results)
    }
}