package com.syjgin.onlyshoot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.UnitGroup
import kotlinx.android.synthetic.main.item_squad_horizontal.view.add_unit
import kotlinx.android.synthetic.main.item_squad_horizontal.view.caption
import kotlinx.android.synthetic.main.item_squad_horizontal.view.remove_unit
import kotlinx.android.synthetic.main.item_squad_unit.view.*

class SquadGroupListAdapter(
    private val listener: SquadListClickListener,
    private val isAttackers: Boolean
) : RecyclerView.Adapter<SquadGroupListAdapter.SquadUnitViewHolder>() {
    private val data = mutableListOf<UnitGroup>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadUnitViewHolder {
        val holder = SquadUnitViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_squad_unit,
                parent,
                false
            )
        )
        holder.itemView.setOnClickListener {
            listener.selectGroup(
                data[holder.adapterPosition].name,
                data[holder.adapterPosition].weaponId,
                isAttackers
            )
        }
        holder.itemView.add_unit.setOnClickListener {
            listener.addUnit(data[holder.adapterPosition].archetypeId, isAttackers)
        }
        holder.itemView.remove_unit.setOnClickListener {
            listener.removeUnit(data[holder.adapterPosition].name, isAttackers)
        }
        holder.itemView.change_weapon.setOnClickListener {
            listener.changeWeapon(
                data[holder.adapterPosition].name,
                data[holder.adapterPosition].weaponId,
                isAttackers
            )
        }
        return holder
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SquadUnitViewHolder, position: Int) {
        val currentUnit = data[position]
        holder.itemView.caption.text =
            String.format("%s (%s)", currentUnit.name, currentUnit.weaponName)
        holder.itemView.amount.text = currentUnit.count.toString()
    }

    fun addData(newData: List<UnitGroup>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    interface SquadListClickListener {
        fun selectGroup(unitName: String, weaponId: Long, isAttackers: Boolean)
        fun addUnit(archetypeId: Long, isAttackers: Boolean)
        fun removeUnit(groupName: String, isAttackers: Boolean)
        fun changeWeapon(groupName: String, weaponId: Long, isAttackers: Boolean)
    }

    class SquadUnitViewHolder(view: View) : RecyclerView.ViewHolder(view)
}