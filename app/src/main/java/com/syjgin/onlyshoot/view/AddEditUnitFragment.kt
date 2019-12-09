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
import com.syjgin.onlyshoot.model.DamageType
import com.syjgin.onlyshoot.model.SquadUnit
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
    var isRadioListenersActive = true
    var isArchetypeEditMode = false

    override fun fragmentTitle(): Int {
        return if (isArchetypeEditMode) R.string.edit_archetype else AddEditUtils.getAddEditFragmentTitle(
            arguments,
            R.string.add_unit,
            R.string.edit_unit
        )
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
            damage_amount,
            armor_penetration_attack,
            attack_amount,
            evasion_skill,
            attack_amount,
            critical_ignorance,
            critical_modifier,
            anger_amount)) {
            textField.addTextChangedListener {
                changeButtonState()
            }
            for(checkbox in listOf(
                explosion,
                cut,
                strike,
                energy
            )) {
                checkbox.setOnCheckedChangeListener { checkView, isChecked ->
                    if(!isRadioListenersActive)
                        return@setOnCheckedChangeListener
                    isRadioListenersActive = false
                    if(isChecked) {
                        val others = when(checkView) {
                            explosion -> listOf(cut, strike, energy)
                            cut -> listOf(explosion, strike, energy)
                            strike -> listOf(cut, explosion, energy)
                            energy -> listOf(cut, strike, explosion)
                            else -> emptyList()
                        }
                        for(checkBox in others) {
                            checkBox.isChecked = false
                        }
                    }
                    isRadioListenersActive = true
                    changeButtonState()
                }
            }
        }
        save_unit.setOnClickListener {
            saveUnit()
        }
        if (isArchetypeEditMode) {
            viewModel?.getUnitLiveData()?.observe(this, Observer { loadUnit(it) })
            viewModel?.loadArchetypeData(unitId)
        } else if (isEditMode) {
            viewModel?.getUnitLiveData()?.observe(this, Observer { loadUnit(it) })
            viewModel?.loadUnitData(unitId, squadId)
        }
    }

    private fun saveUnit() {
        val damageType : DamageType = when {
            explosion.isChecked -> DamageType.Explosion
            strike.isChecked -> DamageType.Strike
            cut.isChecked -> DamageType.Cut
            else -> DamageType.Energy
        }
        viewModel?.saveUnit(
            title_text.text.toString(),
            attack_skill.text.toString().toInt(),
            attack_modifier.text.toString().toInt(),
            armor_penetration_attack.text.toString().toInt(),
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
            damage_amount.text.toString().toInt(),
            damage_modifier.text.toString().toInt(),
            damageType,
            attack_amount.text.toString().toInt(),
            hp.text.toString().toInt(),
            evasion_skill.text.toString().toInt(),
            evasion_amount.text.toString().toInt(),
            misfire.text.toString().toInt(),
            critical_ignorance.text.toString().toInt(),
            critical_modifier.text.toString().toInt(),
            can_use_anger.isChecked,
            can_die_from_anger.isChecked,
            squadId,
            anger_amount.text.toString().toInt(),
            isEditMode)
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
                damage_amount.text?.isNotEmpty() ?: false &&
                (explosion.isChecked || cut.isChecked || strike.isChecked || energy.isChecked) &&
                armor_penetration_attack.text?.isNotEmpty() ?: false &&
                attack_amount.text?.isNotEmpty() ?: false &&
                evasion_skill.text?.isNotEmpty() ?: false &&
                attack_amount.text?.isNotEmpty() ?: false &&
                misfire.text?.isNotEmpty() ?: false &&
                critical_ignorance.text?.isNotEmpty() ?: false &&
                critical_modifier.text?.isNotEmpty() ?: false &&
                anger_amount.text?.isNotEmpty() ?: false &&
                hp.text?.isNotEmpty() ?: false
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
        damage_amount.setText(squadUnit.damage.toString())
        damage_modifier.setText(squadUnit.damageModifier.toString())
        when(squadUnit.damageType) {
            DamageType.Explosion -> explosion.isChecked = true
            DamageType.Cut -> cut.isChecked = true
            DamageType.Strike -> strike.isChecked = true
            DamageType.Energy -> energy.isChecked = true
        }
        armor_penetration_attack.setText(squadUnit.armorPenetration.toString())
        attack_amount.setText(squadUnit.attackCount.toString())
        evasion_skill.setText(squadUnit.evasion.toString())
        evasion_amount.setText(squadUnit.evasionCount.toString())
        misfire.setText(squadUnit.missPossibility.toString())
        critical_ignorance.setText(squadUnit.criticalHitAvoidance.toString())
        critical_modifier.setText(squadUnit.criticalHitModifier.toString())
        anger_amount.setText(squadUnit.rage.toString())
        can_use_anger.isChecked = squadUnit.canUseRage
        can_die_from_anger.isChecked = squadUnit.deathFromRage
        hp.setText(squadUnit.hp.toString())
        squadId = squadUnit.squadId
        changeButtonState()
    }
}