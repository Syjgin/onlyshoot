package com.syjgin.onlyshoot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.AttackResult
import kotlinx.android.synthetic.main.item_attack_result.view.*

class AttackResultAdapter : RecyclerView.Adapter<AttackResultAdapter.AttackResultViewHolder>() {
    private val data = mutableListOf<AttackResult>()

    fun addData(data: List<AttackResult>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttackResultViewHolder {
        return AttackResultViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_attack_result,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: AttackResultViewHolder, position: Int) {
        val currentItem = data[position]
        holder.itemView.from_unit.text = currentItem.attacker
        holder.itemView.to_unit.text = currentItem.defender
        holder.itemView.damage.text = String.format(
            holder.itemView.context.getString(R.string.damage_text),
            currentItem.damage
        )
        holder.itemView.remain_hp.text = String.format(
            holder.itemView.context.getString(R.string.remain_hp),
            currentItem.remainHP
        )
        holder.itemView.attack_count.text = String.format(
            holder.itemView.context.getString(R.string.attack_amount_template),
            currentItem.attackCount
        )
        holder.itemView.description.text = currentItem.description
        val partsString = StringBuilder()
        for ((index, part) in currentItem.affectedParts.withIndex()) {
            partsString.append(
                holder.itemView.context.getString(
                    when (part) {
                        AttackResult.BodyPart.Head -> R.string.head
                        AttackResult.BodyPart.Torso -> R.string.torso
                        AttackResult.BodyPart.RightHand -> R.string.right_hand
                        AttackResult.BodyPart.LeftHand -> R.string.left_hand
                        AttackResult.BodyPart.RightLeg -> R.string.right_leg
                        AttackResult.BodyPart.LeftLeg -> R.string.left_leg
                    }
                )
            )
            if (index != currentItem.affectedParts.size - 1) {
                partsString.append(",")
            }
        }
        holder.itemView.parts.text = String.format(
            holder.itemView.context.getString(R.string.affected_parts),
            partsString.toString()
        )
        holder.itemView.result.text = holder.itemView.context.getString(
            when (currentItem.resultState) {
                AttackResult.ResultState.Hit -> R.string.hit
                AttackResult.ResultState.Misfire -> R.string.misfire
                AttackResult.ResultState.Miss -> R.string.miss
                AttackResult.ResultState.Evasion -> R.string.evasion
                AttackResult.ResultState.Death -> R.string.death
                AttackResult.ResultState.ArmorSave -> R.string.armor_save
            }
        )
    }

    class AttackResultViewHolder(view: View) : RecyclerView.ViewHolder(view)
}