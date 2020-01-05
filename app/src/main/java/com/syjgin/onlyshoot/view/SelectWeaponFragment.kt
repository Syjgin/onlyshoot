package com.syjgin.onlyshoot.view

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Weapon
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import com.syjgin.onlyshoot.utils.DialogUtils
import com.syjgin.onlyshoot.view.adapter.WeaponSelectAdapter
import com.syjgin.onlyshoot.viewmodel.AddEditFightViewModel
import com.syjgin.onlyshoot.viewmodel.AddEditUnitViewModel
import com.syjgin.onlyshoot.viewmodel.SelectWeaponViewModel
import kotlinx.android.synthetic.main.fragment_select_squad.*

class SelectWeaponFragment : BaseFragment<SelectWeaponViewModel>(SelectWeaponViewModel::class.java),
    WeaponSelectAdapter.WeaponSelectListener {

    companion object {
        fun createFragment(bundle: Bundle?): Fragment {
            val fragment = SelectWeaponFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var isCopyMode = false
    private var isListMode = false
    private var currentWeaponId = NO_DATA
    private var previousWeaponId = NO_DATA
    private var squadId = NO_DATA
    private var squadGroupName = ""
    private lateinit var adapter: WeaponSelectAdapter
    private var isDisplayingDialog = false


    override fun fragmentTitle() = if (isListMode) R.string.arsenal else R.string.select_weapon

    override fun fragmentLayout() = R.layout.fragment_select_squad

    override fun parseArguments(args: Bundle) {
        if (args.containsKey(BundleKeys.WeaponId.name)) {
            currentWeaponId = args.getLong(BundleKeys.WeaponId.name)
        }
        isCopyMode = args.getBoolean(BundleKeys.CopyMode.name)
        isListMode = args.getBoolean(BundleKeys.ListMode.name)
        squadId = args.getLong(BundleKeys.SquadId.name, NO_DATA)
        squadGroupName = args.getString(BundleKeys.GroupName.name, "")
        if (squadGroupName.isNotEmpty()) {
            previousWeaponId = currentWeaponId
        }
    }

    override fun hasBackButton() = true

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            if (adapter.itemCount == 0) {
                viewModel?.addWeapon()
            } else {
                if (isDisplayingDialog)
                    return false
                isDisplayingDialog = true
                var dialog: Dialog? = null
                dialog = DialogUtils.createTwoOptionsDialog(
                    context, object :
                        DialogUtils.TwoOptionsDialogListener {
                        override fun onFirstOptionSelected() {
                            dialog?.dismiss()
                            isDisplayingDialog = false
                            viewModel?.addWeapon()
                        }

                        override fun onSecondOptionSelected() {
                            dialog?.dismiss()
                            isDisplayingDialog = false
                            if (currentWeaponId != NO_DATA) {
                                viewModel?.copyWeapon(currentWeaponId, NO_DATA)
                            } else {
                                viewModel?.startCopyWeapon()
                            }
                        }

                        override fun onCancel() {
                            dialog?.dismiss()
                            isDisplayingDialog = false
                        }
                    },
                    R.layout.dialog_add_weapon
                )
                dialog?.show()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = WeaponSelectAdapter(this, isListMode)
        if (isListMode) {
            select_squad.visibility = View.GONE
        } else {
            select_squad.setOnClickListener {
                if (currentWeaponId != NO_DATA) {
                    if (squadGroupName.isNotEmpty() && squadId != NO_DATA) {
                        val addEditFightViewModel =
                            ViewModelProviders.of(activity!!).get(AddEditFightViewModel::class.java)
                        addEditFightViewModel.finishChangeWeapon(
                            squadGroupName,
                            previousWeaponId,
                            currentWeaponId,
                            squadId
                        )
                        viewModel?.exit()
                    } else {
                        if (isCopyMode) {
                            val addEditUnitViewModel =
                                ViewModelProviders.of(activity!!)
                                    .get(AddEditUnitViewModel::class.java)
                            viewModel?.copyWeapon(currentWeaponId, addEditUnitViewModel.getUnitId())
                        } else {
                            val addEditUnitViewModel =
                                ViewModelProviders.of(activity!!)
                                    .get(AddEditUnitViewModel::class.java)
                            addEditUnitViewModel.setWeapon(currentWeaponId)
                            viewModel?.exit()
                        }
                    }
                }
            }
        }
        existing_squads.adapter = adapter
        viewModel?.getWeaponsLiveData()?.observe(this, Observer { showWeapons(it) })
    }

    private fun showWeapons(weapons: List<Weapon>) {
        adapter.addData(weapons)
        if (currentWeaponId != NO_DATA) {
            adapter.setSelectedWeapon(currentWeaponId)
        }
    }

    override fun weaponSelected(weapon: Weapon) {
        select_squad.isEnabled = true
        currentWeaponId = weapon.id
    }

    override fun weaponEditClick(weapon: Weapon) {
        viewModel?.startEditWeapon(weapon)
    }
}