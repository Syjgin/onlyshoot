package com.syjgin.onlyshoot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.UnitArchetype
import kotlinx.android.synthetic.main.item_archetype_horizontal.view.*

class ArchetypeUnitListAdapter(private val listener: ArchetypeListClickListener) :
    RecyclerView.Adapter<ArchetypeUnitListAdapter.SquadUnitViewHolder>() {
    private val data = ArrayList<UnitArchetype>()
    private val countMap = mutableMapOf<UnitArchetype, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadUnitViewHolder {
        val holder = SquadUnitViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_archetype_horizontal,
                parent,
                false
            )
        )
        holder.itemView.setOnClickListener {
            listener.selectUnit(data[holder.adapterPosition])
        }
        holder.itemView.add_unit.setOnClickListener {
            val currentArchetype = data[holder.adapterPosition]
            var currentCount = countMap[currentArchetype]
            if (currentCount == null)
                currentCount = 0
            countMap[currentArchetype] = ++currentCount
            notifyItemChanged(holder.adapterPosition)
            listener.archetypeCountChanged()
        }
        holder.itemView.remove_unit.setOnClickListener {
            val currentArchetype = data[holder.adapterPosition]
            var currentCount2 = countMap[currentArchetype] ?: return@setOnClickListener
            currentCount2 -= 1
            if (currentCount2 == 0) {
                countMap.remove(currentArchetype)
            } else {
                countMap[currentArchetype] = currentCount2
            }
            notifyItemChanged(holder.adapterPosition)
            listener.archetypeCountChanged()
        }
        return holder
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SquadUnitViewHolder, position: Int) {
        val currentUnit = data[position]
        holder.itemView.caption.text = currentUnit.name
        holder.itemView.hp.text =
            String.format(holder.itemView.context.getString(R.string.hp_template), currentUnit.hp)
        holder.itemView.attack.text = String.format(
            holder.itemView.context.getString(R.string.attack_template),
            currentUnit.attack
        )
        holder.itemView.damage.text = String.format(
            holder.itemView.context.getString(R.string.damage_template),
            currentUnit.damage
        )
        holder.itemView.armor.text = String.format(
            holder.itemView.context.getString(R.string.armor_template),
            currentUnit.usualArmor
        )
        val count = countMap[data[position]]
        holder.itemView.count.text = count?.toString() ?: "0"
    }

    fun addData(newData: List<UnitArchetype>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun getCountMap(): Map<UnitArchetype, Int> {
        return countMap
    }

    interface ArchetypeListClickListener {
        fun selectUnit(archetype: UnitArchetype)
        fun archetypeCountChanged()
    }

    class SquadUnitViewHolder(view: View) : RecyclerView.ViewHolder(view)
}