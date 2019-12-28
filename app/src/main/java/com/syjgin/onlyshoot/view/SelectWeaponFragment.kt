package com.syjgin.onlyshoot.view

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
import com.syjgin.onlyshoot.view.adapter.WeaponSelectAdapter
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

    private var isListMode = false
    private var currentWeaponId = NO_DATA
    private lateinit var adapter: WeaponSelectAdapter

    override fun fragmentTitle() = if (isListMode) R.string.arsenal else R.string.select_weapon

    override fun fragmentLayout() = R.layout.fragment_select_squad

    override fun parseArguments(args: Bundle) {
        if (args.containsKey(BundleKeys.WeaponId.name)) {
            currentWeaponId = args.getLong(BundleKeys.WeaponId.name)
        }
        isListMode = args.getBoolean(BundleKeys.ListMode.name)
    }

    override fun hasBackButton() = true

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            viewModel?.addWeapon()
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
                    val addEditUnitViewModel =
                        ViewModelProviders.of(activity!!).get(AddEditUnitViewModel::class.java)
                    addEditUnitViewModel.setWeapon(currentWeaponId)
                }
            }
        }
        existing_squads.adapter = adapter
        viewModel?.getWeaponsLiveData()?.observe(this, Observer { showWeapons(it) })
    }

    private fun showWeapons(weapons: List<Weapon>) {
        adapter.addData(weapons)
    }

    override fun weaponSelected(weapon: Weapon) {
        select_squad.isEnabled = true
    }

    override fun weaponEditClick(weapon: Weapon) {
        viewModel?.startEditWeapon(weapon)
    }
}