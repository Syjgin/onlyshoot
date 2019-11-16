package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Fight
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.AddEditUtils
import com.syjgin.onlyshoot.view.adapter.SquadUnitListAdapter
import com.syjgin.onlyshoot.viewmodel.AddEditFightViewModel
import kotlinx.android.synthetic.main.fragment_add_edit_fight.*

class AddEditFightFragment : BaseFragment<AddEditFightViewModel>(AddEditFightViewModel::class.java),
    SquadUnitListAdapter.SquadListClickListener {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditFightFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var fight: Fight? = null
    private val attackersAdapter = SquadUnitListAdapter(this, true)
    private val defendersAdapter = SquadUnitListAdapter(this, false)

    override fun fragmentTitle(): Int {
        return AddEditUtils.getAddEditFragmentTitle(arguments, R.string.add_fight,R.string.edit_fight)
    }

    override fun fragmentLayout() = R.layout.fragment_add_edit_fight

    override fun parseArguments(args: Bundle?) {
        if(args != null && !args.getBoolean(BundleKeys.AddFlavor.name)) {
            fight = args.getSerializable(BundleKeys.Fight.name) as Fight
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.getAttackFightData()?.observe(this, Observer{ displaySquad(it, true) })
        viewModel?.getDefendFightData()?.observe(this, Observer{ displaySquad(it, false) })
        defenders.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        attackers.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        defenders.adapter = defendersAdapter
        attackers.adapter = attackersAdapter
        if(fight != null) {
            setupExisting()
            viewModel?.renderFight(fight!!)
        } else {
            setupLoading()
        }
        attack.setOnClickListener {
            if(attackersAdapter.itemCount == 0 || defendersAdapter.itemCount == 0) {
                Toast.makeText(activity?.applicationContext, getString(R.string.no_data_for_attack), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel?.startAttack()
        }
        swap.setOnClickListener {
            viewModel?.swap()
        }
    }

    override fun selectUnit(squadUnit : SquadUnit) {
        viewModel?.openUnit(squadUnit)
    }

    override fun addUnit(squadUnit: SquadUnit, isAttackers: Boolean) {
        viewModel?.duplicateUnit(squadUnit, isAttackers)
    }

    private fun setupExisting() {
        load_attackers.visibility = View.GONE
        load_defenders.visibility = View.GONE
        attackers_description.visibility = View.VISIBLE
        defenders_description.visibility = View.VISIBLE
        load_attackers.text = getString(R.string.add_unit)
        load_defenders.text = getString(R.string.add_unit)
        load_attackers.setOnClickListener {viewModel?.loadUnit(true)}
        load_defenders.setOnClickListener {viewModel?.loadUnit(false)}
    }

    private fun setupLoading() {
        load_attackers.visibility = View.VISIBLE
        load_defenders.visibility = View.VISIBLE
        attackers_description.visibility = View.GONE
        defenders_description.visibility = View.GONE
        load_attackers.text = getString(R.string.load_attackers)
        load_defenders.text = getString(R.string.load_defenders)
        load_attackers.setOnClickListener {viewModel?.loadSquad(true)}
        load_defenders.setOnClickListener {viewModel?.loadSquad(false)}
    }

    private fun displaySquad(squad: Squad, isAttackers: Boolean) {
        if(isAttackers) {
            attackersAdapter.addData(squad.list)
            attackers_description.text = createDescription(squad.list)
        } else {
            defendersAdapter.addData(squad.list)
            defenders_description.text = createDescription(squad.list)
        }
    }

    private fun createDescription(units: List<SquadUnit>) : String {
        val result = StringBuilder()
        val map = ArrayMap<String, Int>()
        for(squadUnit in units) {
            if(map.contains(squadUnit.name)) {
                map[squadUnit.name] = map[squadUnit.name]!!+1
            } else {
                map[squadUnit.name] = 1
            }
        }
        for (keyValuePair in map.entries) {
            result.append(String.format("%s: %d", keyValuePair.key, keyValuePair.value))
        }
        return result.toString()
    }

    override fun hasBackButton() = true
}