package com.syjgin.onlyshoot.view

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Attack
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import com.syjgin.onlyshoot.utils.DialogUtils
import com.syjgin.onlyshoot.view.adapter.AttackAdapter
import com.syjgin.onlyshoot.view.adapter.AttackDirectionListener
import com.syjgin.onlyshoot.view.adapter.DefenceAdapter
import com.syjgin.onlyshoot.viewmodel.AttackDirectionViewModel
import kotlinx.android.synthetic.main.fragment_attack_direction.*

class AttackDirectionFragment :
    BaseFragment<AttackDirectionViewModel>(AttackDirectionViewModel::class.java),
    AttackDirectionListener {

    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AttackDirectionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val attackAdapter = AttackAdapter(this)
    private val defenceAdapter = DefenceAdapter(this)
    private var attackersId: Long = NO_DATA
    private var defendersId: Long = NO_DATA
    private var remainAttacks = 0

    override fun fragmentTitle() = R.string.attack_direction

    override fun fragmentLayout() = R.layout.fragment_attack_direction

    override fun parseArguments(args: Bundle) {
        attackersId = args.getLong(BundleKeys.AttackSquadId.name)
        defendersId = args.getLong(BundleKeys.DefendSquadId.name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attack_recycler.adapter = attackAdapter
        defence_recycler.adapter = defenceAdapter
        start_attack.setOnClickListener {
            viewModel?.startAttack(attackAdapter.getAttacks())
        }
        viewModel?.getAttackersLiveData()?.observe(this, Observer {
            handleAttackers(it)
        })
        viewModel?.getDefendersLiveData()?.observe(this, Observer {
            handleDefenders(it)
        })
        viewModel?.loadData(attackersId, defendersId)
    }

    override fun hasBackButton() = true

    override fun onAttackDirectionFinished(
        attackerName: String,
        weaponName: String,
        defenderName: String,
        count: Int,
        color: Int
    ) {
        var countDialog: Dialog? = null
        countDialog = DialogUtils.createCountDialog(
            context,
            getString(R.string.enter_attack_amount),
            object : DialogUtils.CountDialogListener {
                override fun onValueSelected(value: Int, isRandom: Boolean) {
                    countDialog?.dismiss()
                    val attack =
                        Attack(
                            attackerName,
                            weaponName,
                            defenderName,
                            listOf(),
                            listOf(),
                            isRandom,
                            value
                        )
                    attackAdapter.addAttack(attack, color)
                    remainAttacks -= value
                    updateRandomAttacksCount()
                }

                override fun onCancel() {
                    countDialog?.dismiss()
                }
            },
            count
        )
        countDialog?.show()
    }

    override fun onRandomAttackCountIncreased(count: Int) {
        remainAttacks += count
        updateRandomAttacksCount()
    }

    private fun handleAttackers(squad: Squad) {
        attackAdapter.addAttackers(squad.list)
        remainAttacks = attackAdapter.getFreeAttacksCount()
        updateRandomAttacksCount()
    }

    private fun handleDefenders(squad: Squad) {
        defenceAdapter.addDefenders(squad.list)
    }

    private fun updateRandomAttacksCount() {
        remain_attacks.text = String.format(getString(R.string.random_attacks), remainAttacks)
    }
}