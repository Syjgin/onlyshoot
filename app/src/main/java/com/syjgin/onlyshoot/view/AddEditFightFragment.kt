package com.syjgin.onlyshoot.view

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Fight
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.AddEditUtils
import com.syjgin.onlyshoot.utils.DialogUtils
import com.syjgin.onlyshoot.view.adapter.SquadGroupListAdapter
import com.syjgin.onlyshoot.viewmodel.AddEditFightViewModel
import kotlinx.android.synthetic.main.fragment_add_edit_fight.*

class AddEditFightFragment : BaseFragment<AddEditFightViewModel>(AddEditFightViewModel::class.java),
    SquadGroupListAdapter.SquadListClickListener {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditFightFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var fight: Fight? = null
    private val attackersAdapter = SquadGroupListAdapter(this, true)
    private val defendersAdapter = SquadGroupListAdapter(this, false)
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

    override fun selectGroup(unitName: String, weaponId: Long, isAttackers: Boolean) {
        viewModel?.openGroup(unitName, weaponId, isAttackers)
    }

    override fun addUnit(archetypeId: Long, isAttackers: Boolean, weaponId: Long) {
        viewModel?.duplicateUnit(archetypeId, isAttackers, weaponId)
    }

    override fun removeUnit(groupName: String, isAttackers: Boolean) {
        viewModel?.removeUnit(groupName, isAttackers)
    }

    override fun changeWeapon(groupName: String, weaponId: Long, isAttackers: Boolean) {
        viewModel?.changeWeapon(groupName, weaponId, isAttackers)
    }

    private fun setupExisting() {
        if (load_attackers == null)
            return
        load_attackers.text = getString(R.string.add_unit)
        load_defenders.text = getString(R.string.add_unit)
        load_attackers.setOnClickListener { displayLoadUnitDialog(true) }
        load_defenders.setOnClickListener { displayLoadUnitDialog(false) }
    }

    private fun setupLoading() {
        if (load_attackers == null)
            return
        load_attackers.text = getString(R.string.load_attackers)
        load_defenders.text = getString(R.string.load_defenders)
        load_attackers.setOnClickListener {
            DialogUtils.createTwoOptionsDialog(
                context,
                object : DialogUtils.TwoOptionsDialogListener {
                    override fun onFirstOptionSelected() {
                        viewModel?.loadSquad(true, isArchetype = false)
                    }

                    override fun onSecondOptionSelected() {
                        viewModel?.loadSquad(true, isArchetype = true)
                    }

                    override fun onCancel() {}

                },
                R.layout.dialog_add_squad
            )
        }
        load_defenders.setOnClickListener {
            DialogUtils.createTwoOptionsDialog(
                context,
                object : DialogUtils.TwoOptionsDialogListener {
                    override fun onFirstOptionSelected() {
                        viewModel?.loadSquad(false, isArchetype = false)
                    }

                    override fun onSecondOptionSelected() {
                        viewModel?.loadSquad(false, isArchetype = true)
                    }

                    override fun onCancel() {}

                },
                R.layout.dialog_add_squad
            )
        }
    }

    private fun displayLoadUnitDialog(isAttackers: Boolean) {
        if (isDisplayingDialog)
            return
        isDisplayingDialog = true
        var dialog: Dialog? = null
        dialog = DialogUtils.createTwoOptionsDialog(
            context, object :
                DialogUtils.TwoOptionsDialogListener {
                override fun onFirstOptionSelected() {
                dialog?.dismiss()
                isDisplayingDialog = false
                viewModel?.loadUnit(isAttackers)
            }

                override fun onSecondOptionSelected() {
                dialog?.dismiss()
                isDisplayingDialog = false
                viewModel?.loadUnitFromArchetype(isAttackers)
            }

            override fun onCancel() {
                dialog?.dismiss()
                isDisplayingDialog = false
            }
            },
            R.layout.dialog_add_unit
        )
        dialog?.show()
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