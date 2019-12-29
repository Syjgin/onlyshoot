package com.syjgin.onlyshoot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Weapon
import kotlinx.android.synthetic.main.item_squad_list.view.*

class WeaponSelectAdapter(
    private val listener: WeaponSelectListener,
    private val isListMode: Boolean
) : RecyclerView.Adapter<WeaponSelectAdapter.SquadSelectViewHolder>() {
    private val data = mutableListOf<Weapon>()
    private var selectedWeapon: Weapon? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadSelectViewHolder {
        val holder = SquadSelectViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_squad_list,
                parent,
                false
            )
        )
        holder.itemView.select_radio_button.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val newWeapon = data[holder.adapterPosition]
                if (newWeapon.id != selectedWeapon?.id) {
                    val previousSquadId = selectedWeapon?.id
                    selectedWeapon = newWeapon
                    if (previousSquadId != null) {
                        for ((i, squad) in data.withIndex()) {
                            if (squad.id == previousSquadId) {
                                notifyItemChanged(i)
                                break
                            }
                        }
                    }
                    listener.weaponSelected(selectedWeapon!!)
                }
            }
        }
        holder.itemView.edit_button.setOnClickListener {
            listener.weaponEditClick(data[holder.adapterPosition])
        }
        return holder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SquadSelectViewHolder, position: Int) {
        holder.itemView.squad_name.text = data[position].name
        if (isListMode) {
            holder.itemView.select_radio_button.visibility = View.GONE
        } else {
            holder.itemView.select_radio_button.isChecked = data[position].id == selectedWeapon?.id
        }
    }

    fun addData(list: List<Weapon>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun setSelectedWeapon(weaponId: Long) {
        for ((index, weapon) in data.withIndex()) {
            if (weapon.id == weaponId) {
                selectedWeapon = weapon
                notifyItemChanged(index)
                return
            }
        }
    }

    interface WeaponSelectListener {
        fun weaponSelected(weapon: Weapon)
        fun weaponEditClick(weapon: Weapon)
    }

    class SquadSelectViewHolder(view: View) : RecyclerView.ViewHolder(view)
}