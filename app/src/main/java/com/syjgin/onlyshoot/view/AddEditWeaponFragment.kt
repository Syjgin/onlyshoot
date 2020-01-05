package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.BurstType
import com.syjgin.onlyshoot.model.DamageType
import com.syjgin.onlyshoot.model.Weapon
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.AddEditUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import com.syjgin.onlyshoot.viewmodel.AddEditUnitViewModel
import com.syjgin.onlyshoot.viewmodel.AddEditWeaponViewModel
import kotlinx.android.synthetic.main.fragment_add_edit_weapon.*

class AddEditWeaponFragment :
    BaseFragment<AddEditWeaponViewModel>(AddEditWeaponViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?): Fragment {
            val fragment = AddEditWeaponFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    var isRadioListenersActive = true
    var weaponId: Long = NO_DATA
    var unitId: Long = NO_DATA
    private var isEditMode = false
    private var isCopyMode = false
    private var isAlreadySaved = false

    override fun fragmentTitle(): Int {
        return AddEditUtils.getAddEditFragmentTitle(
            arguments,
            R.string.add_weapon,
            R.string.edit_weapon
        )
    }

    override fun fragmentLayout() = R.layout.fragment_add_edit_weapon

    override fun parseArguments(args: Bundle) {
        isEditMode = !args.getBoolean(BundleKeys.AddFlavor.name)
        isCopyMode = args.getBoolean(BundleKeys.CopyMode.name)
        if (isEditMode || isCopyMode) {
            weaponId = args.getLong(BundleKeys.WeaponId.name)
        }
        unitId = args.getLong(BundleKeys.Unit.name)
    }

    override fun hasBackButton() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (textField in listOf(
            title_text,
            attack_modifier,
            damage_amount,
            damage_mod,
            attack_count,
            miss_possibility,
            armor_penetration_attack,
            critical_modifier,
            anger_amount
        )) {
            textField.addTextChangedListener {
                changeButtonState()
            }
            for (checkbox in listOf(
                explosion,
                cut,
                strike,
                energy
            )) {
                checkbox.setOnCheckedChangeListener { checkView, isChecked ->
                    if (!isRadioListenersActive)
                        return@setOnCheckedChangeListener
                    isRadioListenersActive = false
                    if (isChecked) {
                        val others = when (checkView) {
                            explosion -> listOf(cut, strike, energy)
                            cut -> listOf(explosion, strike, energy)
                            strike -> listOf(cut, explosion, energy)
                            energy -> listOf(cut, strike, explosion)
                            else -> emptyList()
                        }
                        for (checkBox in others) {
                            checkBox.isChecked = false
                        }
                    }
                    isRadioListenersActive = true
                    changeButtonState()
                }
            }
            for (checkbox in listOf(
                single,
                short_burst,
                long_burst
            )) {
                checkbox.setOnCheckedChangeListener { checkView, isChecked ->
                    if (!isRadioListenersActive)
                        return@setOnCheckedChangeListener
                    isRadioListenersActive = false
                    if (isChecked) {
                        val others = when (checkView) {
                            single -> listOf(short_burst, long_burst)
                            short_burst -> listOf(single, long_burst)
                            long_burst -> listOf(single, short_burst)
                            else -> emptyList()
                        }
                        for (checkBox in others) {
                            checkBox.isChecked = false
                        }
                    }
                    isRadioListenersActive = true
                    changeButtonState()
                }
            }
        }
        save_weapon.setOnClickListener {
            saveWeapon()
        }
        if (isCopyMode) {
            viewModel?.setCopyMode()
        }
        if (isEditMode || isCopyMode) {
            viewModel?.getWeaponLiveData()?.observe(this, Observer { loadWeapon(it) })
            viewModel?.loadWeapon(weaponId)
        }
    }

    private fun saveWeapon() {
        val damageType: DamageType = when {
            explosion.isChecked -> DamageType.Explosion
            strike.isChecked -> DamageType.Strike
            cut.isChecked -> DamageType.Cut
            else -> DamageType.Energy
        }
        val burstType: BurstType = when {
            single.isChecked -> BurstType.Single
            short_burst.isChecked -> BurstType.Short
            long_burst.isChecked -> BurstType.Long
            else -> BurstType.Single
        }
        viewModel?.saveWeapon(
            title_text.text.toString(),
            attack_modifier.text.toString().toInt(),
            damage_amount.text.toString().toInt(),
            damage_mod.text.toString().toInt(),
            attack_count.text.toString().toInt(),
            miss_possibility.text.toString().toInt(),
            critical_modifier.text.toString().toInt(),
            anger_amount.text.toString().toInt(),
            damageType,
            armor_penetration_attack.text.toString().toInt(),
            burstType,
            unitId
        )
        isAlreadySaved = true
    }

    private fun changeButtonState() {
        save_weapon.isEnabled = title_text.text?.isNotEmpty() ?: false &&
                attack_modifier.text?.isNotEmpty() ?: false &&
                damage_amount.text?.isNotEmpty() ?: false &&
                damage_mod.text?.isNotEmpty() ?: false &&
                attack_count.text?.isNotEmpty() ?: false &&
                miss_possibility.text?.isNotEmpty() ?: false &&
                critical_modifier.text?.isNotEmpty() ?: false &&
                anger_amount.text?.isNotEmpty() ?: false &&
                armor_penetration_attack.text?.isNotEmpty() ?: false &&
                (explosion.isChecked || cut.isChecked || strike.isChecked || energy.isChecked)
    }

    private fun loadWeapon(weapon: Weapon) {
        title_text.setText(weapon.name)
        attack_modifier.setText(weapon.attackModifier.toString())
        damage_amount.setText(weapon.damage.toString())
        damage_mod.setText(weapon.damageModifier.toString())
        attack_count.setText(weapon.attackCount.toString())
        miss_possibility.setText(weapon.missPossibility.toString())
        critical_modifier.setText(weapon.criticalHitModifier.toString())
        anger_amount.setText(weapon.rage.toString())
        armor_penetration_attack.setText(weapon.armorPenetration.toString())
        when (weapon.damageType) {
            DamageType.Explosion -> explosion.isChecked = true
            DamageType.Cut -> cut.isChecked = true
            DamageType.Strike -> strike.isChecked = true
            DamageType.Energy -> energy.isChecked = true
        }
        when (weapon.burstType) {
            BurstType.Single -> single.isChecked = true
            BurstType.Short -> short_burst.isChecked = true
            BurstType.Long -> long_burst.isChecked = true
        }
        weaponId = weapon.id
        changeButtonState()
        if (isAlreadySaved) {
            val addEditUnitViewModel =
                ViewModelProviders.of(activity!!).get(AddEditUnitViewModel::class.java)
            addEditUnitViewModel.setWeapon(weaponId)
            viewModel?.exit()
        }
    }
}