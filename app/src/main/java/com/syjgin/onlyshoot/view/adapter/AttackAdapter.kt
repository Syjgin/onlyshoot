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
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.navigation.BundleKeys
import kotlinx.android.synthetic.main.item_attack_header.view.*
import kotlinx.android.synthetic.main.item_single_attack.view.*
import java.util.*
import kotlin.random.Random

class AttackAdapter(private val listener: AttackDirectionListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val AttackerType: Int = 0
        const val AttackType: Int = 1
    }

    data class ItemType(val isHeader: Boolean, val index: Int)

    private val attackers = mutableListOf<SquadUnit>()
    private val attacks = mutableListOf<Attack>()
    private val itemTypes = mutableListOf<ItemType>()
    private val freeAttacks = mutableMapOf<Long, Int>()
    private val colorsOfDefenders = mutableMapOf<Long, Int>()

    fun addAttackers(attackers: List<SquadUnit>) {
        this.attackers.clear()
        this.attackers.addAll(attackers)
        recreateTypeTable()
        notifyDataSetChanged()
    }

    fun getFreeAttacksCount() : Int {
        var result = 0
        for (squadUnit in attackers) {
            result += squadUnit.attackCount
        }
        return result
    }

    fun addAttack(attack: Attack, color: Int) {
        for(currentAttack in attacks) {
            if(currentAttack.attackerId == attack.attackerId &&
                    currentAttack.defenderId == attack.defenderId) {
                val replacement = Attack(currentAttack.attackerId, currentAttack.defenderId, currentAttack.count + attack.count)
                attacks.remove(currentAttack)
                attacks.add(replacement)
                recreateTypeTable()
                notifyDataSetChanged()
                return
            }
        }
        attacks.add(attack)
        colorsOfDefenders[attack.defenderId] = color
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
                if(freeAttacks[attacker.id] == 0)
                    return@setOnLongClickListener false
                val bundle = Bundle()
                bundle.putLong(BundleKeys.Unit.name, attacker.id)
                bundle.putInt(BundleKeys.AttackCount.name, freeAttacks[attacker.id]!!)
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
            holder.itemView.unit_name.text = attackers[itemType.index].name
            holder.itemView.attack_count.text = freeAttacks[attackers[itemType.index].id].toString()
        } else {
            val itemType = itemTypes[position]
            holder.itemView.defender_color.setBackgroundColor(colorsOfDefenders[attacks[itemType.index].defenderId]!!)
            holder.itemView.attacks.text = attacks[itemType.index].count.toString()
        }
    }

    fun getAttacks(defenders: List<SquadUnit>): List<Attack> {
        val result = mutableListOf<Attack>()
        result.addAll(attacks)
        if(freeAttacks.isEmpty())
            return result
        val random = Random(System.currentTimeMillis())
        val freeAttackers = freeAttacks.keys
        val notAttackedDefenders = mutableListOf<SquadUnit>()
        for(defender in defenders) {
            val attacksWithThisDefender = attacks.find { it.defenderId == defender.id }
            if(attacksWithThisDefender == null) {
                notAttackedDefenders.add(defender)
            }
        }
        val attacksCopy = mutableListOf<Attack>()
        attacksCopy.addAll(attacks)
        while (freeAttacks.isNotEmpty()) {
            val randomDefender : SquadUnit
            if(notAttackedDefenders.isNotEmpty()) {
                randomDefender = defenders[random.nextInt(defenders.size)]
            } else {
                randomDefender = notAttackedDefenders[random.nextInt(notAttackedDefenders.size)]
                notAttackedDefenders.remove(randomDefender)
            }
            val randomAttacker = freeAttackers.elementAt(random.nextInt(freeAttackers.size))
            val attack = Attack(randomAttacker, randomDefender.id, 1)
            val existingAttack = attacksCopy.find { it.attackerId == attack.attackerId && it.defenderId == attack.defenderId }
            if(existingAttack == null) {
                attacksCopy.add(attack)
            } else {
                val newAttack = Attack(attack.attackerId, attack.defenderId, existingAttack.count+1)
                attacksCopy.remove(existingAttack)
                attacksCopy.add(newAttack)
            }
            val nextCount = freeAttacks[randomAttacker]!!-1
            if(nextCount == 0) {
                freeAttacks.remove(randomAttacker)
            } else {
                freeAttacks[randomAttacker] = nextCount
            }
        }
        return attacksCopy
    }

    private fun recreateTypeTable() {
        itemTypes.clear()
        freeAttacks.clear()
        for (i in attackers.indices) {
            var currentFreeAttacks = attackers[i].attackCount
            itemTypes.add(ItemType(true, i))
            for (j in attacks.indices) {
                if (attacks[j].attackerId == attackers[i].id) {
                    currentFreeAttacks -= attacks[j].count
                    itemTypes.add(ItemType(false, j))
                }
            }
            freeAttacks[attackers[i].id] = currentFreeAttacks
        }
    }

    class AttackerViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class AttackViewHolder(view: View) : RecyclerView.ViewHolder(view)
}