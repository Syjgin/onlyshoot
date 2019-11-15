package com.syjgin.onlyshoot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Fight
import kotlinx.android.synthetic.main.item_fight_list.view.*

class FightListAdapter(private val removeListener: RemoveClickListener, private var data: List<Fight>) : RecyclerView.Adapter<FightListAdapter.FightListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FightListViewHolder {
        val holder = FightListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_fight_list, parent, false))
        holder.itemView.remove_button.setOnClickListener {
            removeListener.removeClicked(data[holder.adapterPosition])
        }
        return holder
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FightListViewHolder, position: Int) {
        holder.itemView.fight_name.text = data[position].name
    }

    fun updateData(newData: List<Fight>) {
        data = ArrayList(newData)
        notifyDataSetChanged()
    }

    class FightListViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface RemoveClickListener {
        fun removeClicked(fight: Fight)
    }
}