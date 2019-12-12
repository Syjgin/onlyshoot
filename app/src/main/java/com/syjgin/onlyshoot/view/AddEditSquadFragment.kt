package com.syjgin.onlyshoot.view

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.AddEditUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import com.syjgin.onlyshoot.utils.DialogUtils
import com.syjgin.onlyshoot.view.adapter.SquadUnitListAdapter
import com.syjgin.onlyshoot.viewmodel.AddEditSquadViewModel
import kotlinx.android.synthetic.main.fragment_add_edit_squad.*

class AddEditSquadFragment : BaseFragment<AddEditSquadViewModel>(AddEditSquadViewModel::class.java),
    SquadUnitListAdapter.SquadListClickListener {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditSquadFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var squadId : Long = NO_DATA
    private var isEditMode = false
    private var filter = ""
    private var isDisplayingDialog = false
    private val adapter = SquadUnitListAdapter(this, false)

    override fun fragmentTitle(): Int {
        return AddEditUtils.getAddEditFragmentTitle(arguments, R.string.add_squad, R.string.edit_squad)
    }

    override fun fragmentLayout() = R.layout.fragment_add_edit_squad

    override fun parseArguments(args: Bundle) {
        isEditMode = !args.getBoolean(BundleKeys.AddFlavor.name)
        if(isEditMode)
            squadId = args.getLong(BundleKeys.SquadId.name)
        filter = args.getString(BundleKeys.GroupName.name, "")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            if (isDisplayingDialog)
                return false
            isDisplayingDialog = true
            var dialog: Dialog? = null
            dialog = DialogUtils.createAddUnitDialog(context, object :
                DialogUtils.LoadUnitDialogListener {
                override fun onFromUnitSelected() {
                    dialog?.dismiss()
                    isDisplayingDialog = false
                    viewModel?.addUnit()
                }

                override fun onFromArchetypeSelected() {
                    dialog?.dismiss()
                    isDisplayingDialog = false
                    viewModel?.addArchetype()
                }

                override fun onCancel() {
                    dialog?.dismiss()
                    isDisplayingDialog = false
                }
            })
            dialog?.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun hasBackButton() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        save_squad.setOnClickListener {
            viewModel?.saveSquad(title_text.text.toString())
        }
        if (isEditMode) {
            filter_text.addTextChangedListener {
                filter = it.toString()
                adapter.setFilter(filter)
            }
            filter_text.setText(filter)
        } else {
            filter_text.visibility = View.GONE
        }
        squad_recycler_view.adapter = adapter
        title_text.addTextChangedListener {
            refreshButtonState()
        }
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                refreshButtonState()
            }
        })
        if (isEditMode) {
            viewModel?.loadSquad(squadId)
        } else {
            viewModel?.startObserveSquad()
        }
        viewModel?.getSquadLiveData()?.observe(this, Observer { showUnitsList(it) })
        viewModel?.getNameLiveData()?.observe(this, Observer { title_text.setText(it) })
    }

    private fun refreshButtonState() {
        save_squad.isEnabled = title_text.text.isNotEmpty() && adapter.itemCount > 0
    }

    private fun showUnitsList(units: List<SquadUnit>) {
        adapter.addData(units)
        refreshButtonState()
    }

    override fun selectUnit(squadUnit: SquadUnit) {
        viewModel?.openUnit(squadUnit)
    }

    override fun addUnit(squadUnit: SquadUnit, isAttackers: Boolean) {
        viewModel?.duplicateUnit(squadUnit)
    }

    override fun removeUnit(squadUnit: SquadUnit, isAttackers: Boolean) {
        viewModel?.removeUnit(squadUnit)
    }
}