package com.syjgin.onlyshoot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.SquadUnit
import kotlinx.android.synthetic.main.item_squad_unit.view.*

class SquadUnitListAdapter(private val listener: SquadListClickListener, private val isAttackers: Boolean) : RecyclerView.Adapter<SquadUnitListAdapter.SquadUnitViewHolder>() {
    private val data = ArrayList<SquadUnit>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadUnitViewHolder {
        val holder = SquadUnitViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_squad_unit, parent, false))
        holder.itemView.setOnClickListener {
            listener.selectUnit(data[holder.adapterPosition])
        }
        holder.itemView.add_unit.setOnClickListener {
            listener.addUnit(data[holder.adapterPosition], isAttackers)
        }
        return holder
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SquadUnitViewHolder, position: Int) {
        val currentUnit = data[position]
        holder.itemView.caption.text = currentUnit.name
        holder.itemView.hp.text = String.format(holder.itemView.context.getString(R.string.hp_template), currentUnit.hp)
        holder.itemView.attack.text = String.format(holder.itemView.context.getString(R.string.attack_template), currentUnit.attack)
        holder.itemView.damage.text = String.format(holder.itemView.context.getString(R.string.damage_template), currentUnit.damage)
        holder.itemView.armor.text = String.format(holder.itemView.context.getString(R.string.armor_template), currentUnit.usualArmor)
    }

    fun addData(newData: List<SquadUnit>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    interface SquadListClickListener {
        fun selectUnit(squadUnit : SquadUnit)
        fun addUnit(squadUnit : SquadUnit, isAttackers: Boolean)
    }

    class SquadUnitViewHolder(view: View) : RecyclerView.ViewHolder(view)
}