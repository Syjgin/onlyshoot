package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.model.Weapon
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.AddEditUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import com.syjgin.onlyshoot.viewmodel.AddEditUnitViewModel
import kotlinx.android.synthetic.main.fragment_add_edit_unit.*

class AddEditUnitFragment : BaseFragment<AddEditUnitViewModel>(AddEditUnitViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditUnitFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    var unitId : Long = 0
    var squadId: Long = NO_DATA
    var isEditMode = false
    var isArchetypeEditMode = false
    var weaponId: Long = NO_DATA

    override fun fragmentTitle(): Int {
        return if (isArchetypeEditMode) AddEditUtils.getAddEditFragmentTitle(
            arguments,
            R.string.add_archetype,
            R.string.edit_archetype
        ) else AddEditUtils.getAddEditFragmentTitle(
            arguments,
            R.string.add_unit,
            R.string.edit_unit
        )
    }

    override fun getProviderFromFragment(): Boolean {
        return false
    }

    override fun fragmentLayout() = R.layout.fragment_add_edit_unit

    override fun parseArguments(args: Bundle) {
        isEditMode = !args.getBoolean(BundleKeys.AddFlavor.name)
        if(isEditMode) {
            unitId = args.getLong(BundleKeys.Unit.name)
        }
        squadId = args.getLong(BundleKeys.SquadId.name, NO_DATA)
        isArchetypeEditMode = args.getBoolean(BundleKeys.EditArchetype.name, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isEditMode && !isArchetypeEditMode)
            inflater.inflate(R.menu.archetype_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.archetype) {
            viewModel?.getArchetypeValues()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun hasBackButton() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for(textField in listOf(
            attack_skill,
            attack_modifier,
            normal_armor_head,
            proof_armor_head,
            normal_armor_torso,
            proof_armor_torso,
            normal_armor_left_hand,
            proof_armor_left_hand,
            normal_armor_right_hand,
            proof_armor_right_hand,
            normal_armor_left_leg,
            proof_armor_left_leg,
            normal_armor_right_leg,
            proof_armor_right_leg,
            evasion_skill,
            critical_ignorance,
            anger_amount)) {
            textField.addTextChangedListener {
                changeButtonState()
            }
            load_weapon.setOnClickListener {
                viewModel?.selectWeapon()
            }
        }
        save_unit.setOnClickListener {
            saveUnit()
        }
        if (isArchetypeEditMode) {
            if (isEditMode) {
                viewModel?.getUnitLiveData()?.observe(this, Observer { loadUnit(it) })
                viewModel?.loadArchetypeData(unitId)
            }
        } else if (isEditMode) {
            viewModel?.getUnitLiveData()?.observe(this, Observer { loadUnit(it) })
            viewModel?.loadUnitData(unitId, squadId)
        }
        viewModel?.getWeaponLiveData()?.observe(this, Observer { loadWeapon(it) })
    }

    private fun loadWeapon(weapon: Weapon?) {
        if (weapon == null)
            return
        weaponId = weapon.id
        load_weapon.text = weapon.name
        changeButtonState()
    }

    private fun saveUnit() {
        viewModel?.saveUnit(
            title_text.text.toString(),
            attack_skill.text.toString().toInt(),
            attack_modifier.text.toString().toInt(),
            normal_armor_head.text.toString().toInt(),
            proof_armor_head.text.toString().toInt(),
            normal_armor_torso.text.toString().toInt(),
            proof_armor_torso.text.toString().toInt(),
            normal_armor_left_hand.text.toString().toInt(),
            proof_armor_left_hand.text.toString().toInt(),
            normal_armor_right_hand.text.toString().toInt(),
            proof_armor_right_hand.text.toString().toInt(),
            normal_armor_left_leg.text.toString().toInt(),
            proof_armor_left_leg.text.toString().toInt(),
            normal_armor_right_leg.text.toString().toInt(),
            proof_armor_right_leg.text.toString().toInt(),
            constant_damage_modifier.text.toString().toInt(),
            temp_damage_modifier.text.toString().toInt(),
            hp.text.toString().toInt(),
            evasion_skill.text.toString().toInt(),
            evasion_amount.text.toString().toInt(),
            critical_ignorance.text.toString().toInt(),
            can_use_anger.isChecked,
            can_die_from_anger.isChecked,
            squadId,
            weaponId,
            load_weapon.text.toString(),
            enemy_attack_skill.toString().toInt(),
            enemy_attack_skill_temp.toString().toInt(),
            anger_amount.text.toString().toInt(),
            isEditMode
        )
    }

    private fun changeButtonState() {
        save_unit.isEnabled = title_text.text?.isNotEmpty() ?: false &&
                attack_skill.text?.isNotEmpty() ?: false &&
                attack_modifier.text?.isNotEmpty() ?: false &&
                normal_armor_head.text?.isNotEmpty() ?: false &&
                proof_armor_head.text?.isNotEmpty() ?: false &&
                normal_armor_torso.text?.isNotEmpty() ?: false &&
                proof_armor_torso.text?.isNotEmpty() ?: false &&
                normal_armor_left_hand.text?.isNotEmpty() ?: false &&
                proof_armor_left_hand.text?.isNotEmpty() ?: false &&
                normal_armor_right_hand.text?.isNotEmpty() ?: false &&
                proof_armor_right_hand.text?.isNotEmpty() ?: false &&
                normal_armor_left_leg.text?.isNotEmpty() ?: false &&
                proof_armor_left_leg.text?.isNotEmpty() ?: false &&
                normal_armor_right_leg.text?.isNotEmpty() ?: false &&
                proof_armor_right_leg.text?.isNotEmpty() ?: false &&
                evasion_skill.text?.isNotEmpty() ?: false &&
                critical_ignorance.text?.isNotEmpty() ?: false &&
                anger_amount.text?.isNotEmpty() ?: false &&
                hp.text?.isNotEmpty() ?: false &&
                enemy_attack_skill.text?.isNotEmpty() ?: false &&
                enemy_attack_skill_temp.text?.isNotEmpty() ?: false &&
                weaponId != NO_DATA
    }

    private fun loadUnit(squadUnit: SquadUnit) {
        title_text.setText(squadUnit.name)
        attack_skill.setText(squadUnit.attack.toString())
        attack_modifier.setText(squadUnit.attackModifier.toString())
        normal_armor_head.setText(squadUnit.usualArmorHead.toString())
        proof_armor_head.setText(squadUnit.proofArmorHead.toString())
        normal_armor_torso.setText(squadUnit.usualArmorTorso.toString())
        proof_armor_torso.setText(squadUnit.proofArmorTorso.toString())
        normal_armor_left_hand.setText(squadUnit.usualArmorLeftHand.toString())
        proof_armor_left_hand.setText(squadUnit.proofArmorLeftHand.toString())
        normal_armor_right_hand.setText(squadUnit.usualArmorRightHand.toString())
        proof_armor_right_hand.setText(squadUnit.proofArmorRightHand.toString())
        normal_armor_left_leg.setText(squadUnit.usualArmorLeftLeg.toString())
        proof_armor_left_leg.setText(squadUnit.proofArmorLeftLeg.toString())
        normal_armor_right_leg.setText(squadUnit.usualArmorRightLeg.toString())
        proof_armor_right_leg.setText(squadUnit.proofArmorRightLeg.toString())
        constant_damage_modifier.setText(squadUnit.constantDamageModifier.toString())
        temp_damage_modifier.setText(squadUnit.tempDamageModifier.toString())
        evasion_skill.setText(squadUnit.evasion.toString())
        evasion_amount.setText(squadUnit.evasionCount.toString())
        critical_ignorance.setText(squadUnit.criticalHitAvoidance.toString())
        anger_amount.setText(squadUnit.rage.toString())
        can_use_anger.isChecked = squadUnit.canUseRage
        can_die_from_anger.isChecked = squadUnit.deathFromRage
        hp.setText(squadUnit.hp.toString())
        squadId = squadUnit.squadId
        weaponId = squadUnit.weaponId
        enemy_attack_skill.setText(squadUnit.constantEnemyAttackModifier)
        enemy_attack_skill_temp.setText(squadUnit.tempEnemyAttackModifier)
        changeButtonState()
    }
}