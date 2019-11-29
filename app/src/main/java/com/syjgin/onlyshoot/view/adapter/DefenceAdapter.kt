package com.syjgin.onlyshoot.view.adapter

import android.view.DragEvent.ACTION_DROP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.ColorUtils
import kotlinx.android.synthetic.main.item_single_defend.view.*

class DefenceAdapter(private val attackDirectionListener: AttackDirectionListener) :
    RecyclerView.Adapter<DefenceAdapter.DefenceViewHolder>() {
    private val data = mutableListOf<SquadUnit>()
    private val colorsOfDefenders = mutableListOf<Int>()

    fun addDefenders(list: List<SquadUnit>) {
        data.clear()
        data.addAll(list)
        for (i in data.indices) {
            colorsOfDefenders.add(ColorUtils.randomColor())
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefenceViewHolder {
        val holder = DefenceViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_single_defend,
                parent,
                false
            )
        )
        holder.itemView.setOnDragListener { v, event ->
            if (event.action == ACTION_DROP) {
                val clipdata = event.clipData
                val intent = clipdata.getItemAt(0).intent
                val attackUnitId = intent.getLongExtra(BundleKeys.Unit.name, 0L)
                val attackCount = intent.getIntExtra(BundleKeys.AttackCount.name, 0)
                val defender = data[holder.adapterPosition]
                attackDirectionListener.onAttackDirectionFinished(
                    attackUnitId,
                    defender.id,
                    attackCount,
                    colorsOfDefenders[holder.adapterPosition]
                )
            }
            return@setOnDragListener true
        }
        return holder
    }

    fun getData() : List<SquadUnit> {
        return data
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: DefenceViewHolder, position: Int) {
        holder.itemView.item_background.setBackgroundColor(colorsOfDefenders[position])
        holder.itemView.unit_name.text = data[position].name
    }

    class DefenceViewHolder(view: View) : RecyclerView.ViewHolder(view)
}