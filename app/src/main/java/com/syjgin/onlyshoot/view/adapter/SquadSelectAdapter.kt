package com.syjgin.onlyshoot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.SquadDescription
import kotlinx.android.synthetic.main.item_squad_list.view.*

class SquadSelectAdapter(
    private val listener: SquadSelectListener,
    private val isListMode: Boolean
) : RecyclerView.Adapter<SquadSelectAdapter.SquadSelectViewHolder>() {
    private val data = mutableListOf<SquadDescription>()
    private var selectedSquad : SquadDescription? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadSelectViewHolder {
        val holder = SquadSelectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_squad_list, parent, false))
        holder.itemView.select_radio_button.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                val newSquad = data[holder.adapterPosition]
                if(newSquad.id != selectedSquad?.id) {
                    val previousSquadId = selectedSquad?.id
                    selectedSquad = newSquad
                    if(previousSquadId != null) {
                        for((i,squad) in data.withIndex()) {
                            if(squad.id == previousSquadId) {
                                notifyItemChanged(i)
                                break
                            }
                        }
                    }
                    listener.squadSelected(selectedSquad!!)
                }
            }
        }
        holder.itemView.edit_button.setOnClickListener {
            listener.squadEditClick(data[holder.adapterPosition])
        }
        return holder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SquadSelectViewHolder, position: Int) {
        holder.itemView.squad_name.text = data[position].name
        if (isListMode) {
            holder.itemView.select_radio_button.visibility = View.GONE
        } else {
            holder.itemView.select_radio_button.isChecked = data[position].id == selectedSquad?.id
        }
    }

    fun addData(list: List<SquadDescription>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    interface SquadSelectListener {
        fun squadSelected(squad: SquadDescription)
        fun squadEditClick(squad: SquadDescription)
    }

    class SquadSelectViewHolder(view: View) : RecyclerView.ViewHolder(view)
}