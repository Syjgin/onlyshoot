package com.syjgin.onlyshoot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.SquadUnit
import kotlinx.android.synthetic.main.item_squad_horizontal.view.*
import java.util.*

class SquadUnitListAdapter(
    private val listener: SquadListClickListener,
    private val isAttackers: Boolean
) : RecyclerView.Adapter<SquadUnitListAdapter.SquadUnitViewHolder>() {
    private val data = mutableListOf<SquadUnit>()
    private val filteredData = mutableListOf<SquadUnit>()
    private var filter = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadUnitViewHolder {
        val holder = SquadUnitViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_squad_horizontal,
                parent,
                false
            )
        )
        holder.itemView.setOnClickListener {
            listener.selectUnit(data[holder.adapterPosition])
        }
        holder.itemView.add_unit.setOnClickListener {
            listener.addUnit(data[holder.adapterPosition], isAttackers)
        }
        holder.itemView.remove_unit.setOnClickListener {
            listener.removeUnit(data[holder.adapterPosition], isAttackers)
        }
        return holder
    }

    override fun getItemCount() = if (filter.isEmpty()) data.size else filteredData.size

    override fun onBindViewHolder(holder: SquadUnitViewHolder, position: Int) {
        val currentUnit = if (filter.isEmpty()) data[position] else filteredData[position]
        holder.itemView.caption.text = currentUnit.name
        holder.itemView.hp.text = String.format(
            holder.itemView.context.getString(R.string.hp_template),
            currentUnit.hp
        )
        holder.itemView.attack.text = String.format(
            holder.itemView.context.getString(R.string.attack_template),
            currentUnit.attack
        )
        /*holder.itemView.damage.text = String.format(
            holder.itemView.context.getString(R.string.damage_template),
            currentUnit.damage
        )*/
    }

    fun setFilter(filter: String) {
        if (this.filter == "" && filter == "")
            return
        this.filter = filter
        filteredData.clear()
        for (squadUnit in data) {
            if (squadUnit.name.toLowerCase(Locale.getDefault()).contains(filter.toLowerCase(Locale.getDefault()))) {
                filteredData.add(squadUnit)
            }
        }
        notifyDataSetChanged()
    }

    fun addData(newData: List<SquadUnit>) {
        data.clear()
        data.addAll(newData)
        setFilter(filter)
        notifyDataSetChanged()
    }

    interface SquadListClickListener {
        fun selectUnit(squadUnit : SquadUnit)
        fun addUnit(squadUnit : SquadUnit, isAttackers: Boolean)
        fun removeUnit(squadUnit : SquadUnit, isAttackers: Boolean)
    }

    class SquadUnitViewHolder(view: View) : RecyclerView.ViewHolder(view)
}