package com.syjgin.onlyshoot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.utils.StringUtils
import kotlinx.android.synthetic.main.item_squad_list.view.*

class SquadSelectAdapter(private val listener : SquadSelectListener) : RecyclerView.Adapter<SquadSelectAdapter.SquadSelectViewHolder>() {
    private val data = ArrayList<Squad>()
    private var selectedSquad : Squad? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadSelectViewHolder {
        val holder = SquadSelectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_squad_list, parent, false))
        holder.itemView.select_radio_button.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                val newSquad = data[holder.adapterPosition]
                if(newSquad.getId() != selectedSquad?.getId()) {
                    val previousSquadId = selectedSquad?.getId()
                    selectedSquad = newSquad
                    if(previousSquadId != null) {
                        for((i,squad) in data.withIndex()) {
                            if(squad.getId() == previousSquadId) {
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
        holder.itemView.squad_name.text = StringUtils.createDescription(data[position].list)
        holder.itemView.select_radio_button.isChecked = data[position].getId() == selectedSquad?.getId()
    }

    fun addData(list: List<Squad>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    interface SquadSelectListener {
        fun squadSelected(squad: Squad)
        fun squadEditClick(squad: Squad)
    }

    class SquadSelectViewHolder(view: View) : RecyclerView.ViewHolder(view)
}