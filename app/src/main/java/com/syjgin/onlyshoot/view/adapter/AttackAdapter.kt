package com.syjgin.onlyshoot.view.adapter

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Attack
import com.syjgin.onlyshoot.model.UnitGroup
import com.syjgin.onlyshoot.navigation.BundleKeys
import kotlinx.android.synthetic.main.item_attack_header.view.*
import kotlinx.android.synthetic.main.item_single_attack.view.*

class AttackAdapter(private val listener: AttackDirectionListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val AttackerType: Int = 0
        const val AttackType: Int = 1
    }

    data class ItemType(val isHeader: Boolean, val index: Int)

    private val attackers = mutableListOf<UnitGroup>()
    private val attacks = mutableListOf<Attack>()
    private val itemTypes = mutableListOf<ItemType>()
    private val freeAttacks = mutableMapOf<String, Int>()
    private val colorsOfDefenders = mutableMapOf<String, Int>()

    fun addAttackers(attackers: List<UnitGroup>) {
        this.attackers.clear()
        this.attackers.addAll(attackers)
        recreateTypeTable()
        notifyDataSetChanged()
    }

    fun getFreeAttacksCount() : Int {
        var result = 0
        for (unitGroup in attackers) {
            result += unitGroup.attackCount
        }
        for (attack in attacks) {
            result -= attack.count
        }
        return result
    }

    fun addAttack(attack: Attack, color: Int) {
        for(currentAttack in attacks) {
            if (currentAttack.attackersGroupName == attack.attackersGroupName &&
                currentAttack.defendersGroupName == attack.defendersGroupName &&
                currentAttack.isRandom == attack.isRandom &&
                currentAttack.attackersWeaponName == attack.attackersWeaponName
            ) {
                val replacement = Attack(
                    attack.attackersGroupName,
                    attack.attackersWeaponName,
                    attack.defendersGroupName,
                    listOf(),
                    listOf(),
                    attack.isRandom,
                    currentAttack.count + attack.count
                )
                attacks.remove(currentAttack)
                attacks.add(replacement)
                recreateTypeTable()
                notifyDataSetChanged()
                return
            }
        }
        attacks.add(attack)
        colorsOfDefenders[attack.defendersGroupName] = color
        recreateTypeTable()
        notifyDataSetChanged()
    }

    @Suppress("DEPRECATION")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == AttackerType) {
            val holder = AttackerViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_attack_header,
                    parent,
                    false
                )
            )
            holder.itemView.setOnLongClickListener {
                val itemType = itemTypes[holder.adapterPosition]
                val attacker = attackers[itemType.index]
                if (freeAttacks[attacker.name + attacker.weaponName] == 0)
                    return@setOnLongClickListener false
                val bundle = Bundle()
                bundle.putString(BundleKeys.GroupName.name, attacker.name)
                bundle.putString(BundleKeys.WeaponName.name, attacker.weaponName)
                bundle.putInt(
                    BundleKeys.AttackCount.name,
                    freeAttacks[attacker.name + attacker.weaponName]!!
                )
                val intent = Intent()
                intent.putExtras(bundle)
                val item = ClipData.Item(intent)
                val dragData = ClipData(
                    ClipDescription(
                        attacker.name,
                        arrayOf(ClipDescription.MIMETYPE_TEXT_INTENT)
                    ), item
                )
                val dragShadowBuilder = View.DragShadowBuilder(holder.itemView)
                holder.itemView.startDrag(dragData, dragShadowBuilder, null, 0)
                return@setOnLongClickListener true
            }
            return holder
        } else {
            val holder = AttackViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_single_attack,
                    parent,
                    false
                )
            )
            holder.itemView.remove_button.setOnClickListener {
                val itemType = itemTypes[holder.adapterPosition]
                val attack = attacks[itemType.index]
                attacks.remove(attack)
                listener.onRandomAttackCountIncreased(attack.count)
                recreateTypeTable()
                notifyDataSetChanged()
            }
            return holder
        }
    }

    override fun getItemCount() = attackers.size + attacks.size

    override fun getItemViewType(position: Int): Int {
        return if (itemTypes[position].isHeader) AttackerType else AttackType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AttackerViewHolder) {
            holder.itemView.border.visibility = if (position == 0) View.GONE else View.VISIBLE
            val itemType = itemTypes[position]
            holder.itemView.unit_name.text = String.format(
                "%s (%s)",
                attackers[itemType.index].name,
                attackers[itemType.index].weaponName
            )
            holder.itemView.attack_count.text =
                freeAttacks[attackers[itemType.index].name + attackers[itemType.index].weaponName].toString()
        } else {
            val itemType = itemTypes[position]
            holder.itemView.random_indicator.visibility =
                if (attacks[itemType.index].isRandom) View.VISIBLE else View.GONE
            holder.itemView.defender_color.setBackgroundColor(colorsOfDefenders[attacks[itemType.index].defendersGroupName]!!)
            holder.itemView.attacks.text = attacks[itemType.index].count.toString()
        }
    }

    fun getAttacks(): List<Attack> {
        return attacks
    }

    private fun recreateTypeTable() {
        itemTypes.clear()
        freeAttacks.clear()
        for (i in attackers.indices) {
            var currentFreeAttacks = attackers[i].attackCount
            itemTypes.add(ItemType(true, i))
            for (j in attacks.indices) {
                if (attacks[j].attackersGroupName == attackers[i].name && attacks[j].attackersWeaponName == attackers[i].weaponName) {
                    currentFreeAttacks -= attacks[j].count
                    itemTypes.add(ItemType(false, j))
                }
            }
            freeAttacks[attackers[i].name + attackers[i].weaponName] = currentFreeAttacks
        }
    }

    class AttackerViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class AttackViewHolder(view: View) : RecyclerView.ViewHolder(view)
}