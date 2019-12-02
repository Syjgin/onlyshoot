package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Fight
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.AddEditUtils
import com.syjgin.onlyshoot.utils.DialogUtils
import com.syjgin.onlyshoot.view.adapter.SquadUnitListAdapter
import com.syjgin.onlyshoot.viewmodel.AddEditFightViewModel
import kotlinx.android.synthetic.main.fragment_add_edit_fight.*

class AddEditFightFragment : BaseFragment<AddEditFightViewModel>(AddEditFightViewModel::class.java),
    SquadUnitListAdapter.SquadListClickListener {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditFightFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var fight: Fight? = null
    private val attackersAdapter = SquadUnitListAdapter(this, true, isHorizontal = false)
    private val defendersAdapter = SquadUnitListAdapter(this, false, isHorizontal = false)
    private var isDisplayingDialog = false

    override fun fragmentTitle(): Int {
        return AddEditUtils.getAddEditFragmentTitle(arguments, R.string.add_fight,R.string.edit_fight)
    }

    override fun getProviderFromFragment(): Boolean {
        return false
    }

    override fun fragmentLayout() = R.layout.fragment_add_edit_fight

    override fun parseArguments(args: Bundle) {
        if(!args.getBoolean(BundleKeys.AddFlavor.name)) {
            fight = args.getSerializable(BundleKeys.Fight.name) as Fight
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.getAttackFightData()?.observe(activity!!, Observer { displaySquad(it, true) })
        viewModel?.getDefendFightData()?.observe(activity!!, Observer { displaySquad(it, false) })
        viewModel?.getSaveDialogLiveData()?.observe(this, Observer { displaySaveDialog(it) })
        defenders.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        attackers.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        defenders.adapter = defendersAdapter
        attackers.adapter = attackersAdapter
        load_attackers.setOnClickListener { viewModel?.loadSquad(true) }
        load_defenders.setOnClickListener { viewModel?.loadSquad(false) }
        load_attack_archetype.setOnClickListener { viewModel?.loadUnitFromArchetype(true) }
        load_defence_archetype.setOnClickListener { viewModel?.loadUnitFromArchetype(false) }
        load_attack_unit.setOnClickListener { viewModel?.loadUnit(true) }
        load_defence_unit.setOnClickListener { viewModel?.loadUnit(false) }
        if (attackersAdapter.itemCount == 0 || defendersAdapter.itemCount == 0) {
            setupLoading()
        }
        attack.setOnClickListener {
            if(attackersAdapter.itemCount == 0 || defendersAdapter.itemCount == 0) {
                Toast.makeText(activity?.applicationContext, getString(R.string.no_data_for_attack), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel?.startAttack()
        }
        swap.setOnClickListener {
            viewModel?.swap()
        }
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel?.goBack()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if(fight != null) {
            setupExisting()
            viewModel?.renderFight(fight!!)
        } else {
            viewModel?.updateSquads()
        }
    }

    override fun selectUnit(squadUnit : SquadUnit) {
        viewModel?.openUnit(squadUnit)
    }

    override fun addUnit(squadUnit: SquadUnit, isAttackers: Boolean) {
        viewModel?.duplicateUnit(squadUnit, isAttackers)
    }

    override fun removeUnit(squadUnit: SquadUnit, isAttackers: Boolean) {
        viewModel?.removeUnit(squadUnit, isAttackers)
    }

    private fun setupExisting() {
        if (load_attackers == null)
            return
        load_attackers.visibility = View.GONE
        load_defenders.visibility = View.GONE
        load_attack_archetype.visibility = View.VISIBLE
        load_attack_unit.visibility = View.VISIBLE
        load_defence_archetype.visibility = View.VISIBLE
        load_defence_unit.visibility = View.VISIBLE
    }

    private fun setupLoading() {
        if (load_attackers == null)
            return
        load_attackers.visibility = View.VISIBLE
        load_defenders.visibility = View.VISIBLE
        load_attack_archetype.visibility = View.GONE
        load_attack_unit.visibility = View.GONE
        load_defence_archetype.visibility = View.GONE
        load_defence_unit.visibility = View.GONE
    }

    private fun displaySaveDialog(boolean: Boolean) {
        if (!boolean)
            return
        if (isDisplayingDialog)
            return
        isDisplayingDialog = true
        var dialog: AlertDialog? = null
        dialog = DialogUtils.createInputDialog(context, getString(R.string.save_fight), object :
            DialogUtils.InputFieldDialogListener {
            override fun onValueSelected(value: String) {
                isDisplayingDialog = false
                dialog?.dismiss()
                viewModel?.saveFight(value)
            }

            override fun onCancel() {
                isDisplayingDialog = false
                dialog?.dismiss()
                viewModel?.exitWithoutSaving()
            }
        })
        dialog?.show()
    }

    private fun displaySquad(squad: Squad, isAttackers: Boolean) {
        if(isAttackers) {
            attackersAdapter.addData(squad.list)
            attackers_description?.text = squad.name
        } else {
            defendersAdapter.addData(squad.list)
            defenders_description?.text = squad.name
        }
        if (attackersAdapter.itemCount > 0 && defendersAdapter.itemCount > 0) {
            setupExisting()
        }
    }

    override fun hasBackButton() = true
}