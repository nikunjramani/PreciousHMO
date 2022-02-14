package com.lion_tech.hmo.client.activities.fragments.dependants

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.DashboardClient
import com.lion_tech.hmo.client.activities.adpaters.recyclerViewAdapters.DependentRvAdapter
import com.lion_tech.hmo.client.activities.fragments.enrolleeProfile.EnrolleeViewModel

class DependantFragment : Fragment() {

    private lateinit var dependantViewModel: DependantViewModel
    private lateinit var enrolleeViewModel: EnrolleeViewModel
    private lateinit var rvAdapter: DependentRvAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dependantViewModel =
            ViewModelProvider(this).get(DependantViewModel::class.java)
        enrolleeViewModel = ViewModelProvider(this).get(EnrolleeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_total_dependants, container, false)
        (activity as DashboardClient).hideTextView(
            true,
            activity!!.getString(R.string.my_dependant)
        )

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!AppLevelData.verifyAvailableNetwork(context as DashboardClient)) {
            Toast.makeText(
                context,
                context!!.getString(R.string.conection_problem),
                Toast.LENGTH_SHORT
            ).show()

        } else {
            dependantViewModel.getClientDependant(activity as Context)
        }


        AppLevelData.dependentViewModel = dependantViewModel
        rvAdapter = DependentRvAdapter(activity as Context)

        if (AppLevelData.clientDetailJson!!.getString("ind_family") != "family") {
            (activity as DashboardClient).hideAddDependantButton()
        }

        progressBar = view.findViewById(R.id.progressBar)

        AppLevelData.dependentProgressBar = progressBar

        val rvDependent = view.findViewById(R.id.rvDependent) as RecyclerView
        rvDependent.layoutManager = LinearLayoutManager(activity)
        rvDependent.adapter = rvAdapter

        dependantViewModel.dependentList.observe(viewLifecycleOwner, Observer {
            if (it.size > 4) {
                (activity as DashboardClient).hideAddDependantButton()
            }
            AppLevelData.clientDependentCount=it.size

            rvAdapter.refreshAdapter(it)
            if (AppLevelData.providerList == null) {
                enrolleeViewModel.getAllProviders()
            } else {
                AppLevelData.dependentProgressBar!!.visibility = View.GONE
                if (AppLevelData.dependentDialog != null) {
                    AppLevelData.dependentDialog!!.dismiss()

                    Toast.makeText(
                        (activity as DashboardClient),
                        getString(R.string.dependednt_add_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        enrolleeViewModel.providersData.observe(viewLifecycleOwner, Observer {
            AppLevelData.providerList = it
            AppLevelData.dependentProgressBar!!.visibility = View.GONE
            if (AppLevelData.dependentDialog != null) {
                AppLevelData.dependentDialog!!.dismiss()
                Toast.makeText(
                    (activity as DashboardClient),
                    getString(R.string.dependednt_add_successfully),
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }
}