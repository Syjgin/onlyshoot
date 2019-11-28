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
import com.syjgin.onlyshoot.utils.ColorUtils
import kotlinx.android.synthetic.main.item_attack_header.view.*
import kotlinx.android.synthetic.main.item_single_attack.view.*

class AttackAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val Attacker: Int = 0
        const val Attack: Int = 1
    }

    data class ItemType(val isHeader: Boolean, val index: Int)

    private val attackers = mutableListOf<SquadUnit>()
    private val attacks = mutableListOf<Attack>()
    private val itemTypes = mutableListOf<ItemType>()
    private val freeAttacks = mutableMapOf<Long, Int>()
    private val colorsOfDefenders = mutableMapOf<Long, Int>()

    fun addAttackers(attackers: List<SquadUnit>) {
        this.attackers.addAll(attackers)
        recreateTypeTable()
        notifyDataSetChanged()
    }

    fun addAttack(attack: Attack) {
        attacks.add(attack)
        colorsOfDefenders[attack.defenderId] = ColorUtils.randomColor()
        recreateTypeTable()
        notifyDataSetChanged()
    }

    @Suppress("DEPRECATION")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == Attacker) {
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
                val bundle = Bundle()
                bundle.putSerializable(BundleKeys.Unit.name, attacker)
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
                recreateTypeTable()
                notifyDataSetChanged()
            }
            return holder
        }
    }

    override fun getItemCount() = attackers.size + attacks.size

    override fun getItemViewType(position: Int): Int {
        return if (itemTypes[position].isHeader) Attacker else Attack
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