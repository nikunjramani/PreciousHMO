package com.lion_tech.hmo.client.activities.fragments.changeProviderRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.DashboardClient
import com.lion_tech.hmo.client.activities.adpaters.recyclerViewAdapters.ChangeProviderRvAdapter
import com.lion_tech.hmo.client.activities.fragments.enrolleeProfile.EnrolleeViewModel
import com.lion_tech.hmo.client.activities.models.ProviderModel
import java.util.*

class ChangeProviderRequest : Fragment() {

    private lateinit var tvSelectedProviderLocation: TextView
    private lateinit var tvSelectedProviderName: TextView
    private lateinit var tvSelectedAddress: TextView
    private lateinit var enrolleeViewModel: EnrolleeViewModel
    private lateinit var changeProviderRequestViewModel: ChangeProviderRequestViewModel
    private lateinit var rvAdapter: ChangeProviderRvAdapter
    private var providerList: List<ProviderModel>? = null
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changeProviderRequestViewModel =
            ViewModelProvider(this).get(ChangeProviderRequestViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_change_provider_request, container, false)
        (activity as DashboardClient).hideTextView(
            true,
            activity!!.getString(R.string.change_provider)
        )
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mView: View = view.findViewById(R.id.row_layout)

        val btnCurrentProvider = mView.findViewById<TextView>(R.id.tvChangeProvider)
        btnCurrentProvider.text = getString(R.string.current_provider)
        tvSelectedProviderName = mView.findViewById<TextView>(R.id.tvProviderName)
        tvSelectedProviderLocation = mView.findViewById<TextView>(R.id.tvLocationName)
        tvSelectedAddress = mView.findViewById<TextView>(R.id.tvAddress)

        progressBar = view.findViewById(R.id.progressBar)

        val rvChangeProvider: RecyclerView = view.findViewById(R.id.rvChangeProvider)
        rvChangeProvider.layoutManager = LinearLayoutManager(activity as DashboardClient)

        rvAdapter =
            ChangeProviderRvAdapter(activity as DashboardClient, changeProviderRequestViewModel)
        rvChangeProvider.adapter = rvAdapter

        enrolleeViewModel =
            ViewModelProvider(this).get(EnrolleeViewModel::class.java)

        if (!AppLevelData.verifyAvailableNetwork(context as DashboardClient)) {
            Toast.makeText(
                context,
                context!!.getString(R.string.conection_problem),
                Toast.LENGTH_SHORT
            ).show()

        } else {
            enrolleeViewModel.getAllProviders()
        }


        enrolleeViewModel.providersData.observe(viewLifecycleOwner, Observer {
            val clientJason = AppLevelData.clientDetailJson
            if (clientJason!!.getString("hospital_id").isNotEmpty() && !clientJason.getString("hospital_id").equals(
                    "null"
                )
            ) {
                enrolleeViewModel.getClientHospital(clientJason.getString("hospital_id"))
            } else {
                tvSelectedProviderName.text = "No Provider Assigned"
                progressBar.visibility = View.GONE

            }
            providerList = it
            AppLevelData.clientSubscriptionCount = it.size
            rvAdapter.refreshAdapter(it)

        })

        enrolleeViewModel.clientHospitalData.observe(viewLifecycleOwner, Observer {
            changeProviderRequestViewModel.selectedProvider.value = it
            progressBar.visibility = View.GONE
        })


        changeProviderRequestViewModel.selectedProvider.observe(viewLifecycleOwner, Observer {
            tvSelectedProviderLocation.text = it.hospitalLocation
            tvSelectedProviderName.text = it.hospitalName
            tvSelectedAddress.text = it.hospitalAddress
            progressBar.visibility = View.GONE

        })

        changeProviderRequestViewModel.isProviderChanged.observe(viewLifecycleOwner, Observer {
            if (it) {
                rvAdapter.dismissDialog()
            }
        })

        (activity as DashboardClient).searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filterList = providerList!!.filter { providerModel ->
                    providerModel.hospitalName.toLowerCase(Locale.US)
                        .contains(newText.toString().toLowerCase(Locale.US))
                }
                rvAdapter.refreshAdapter(filterList)
                return true
            }

        })


    }

//    private fun setCurrentProvider() {
//        val providerList = AppLevelData.providerList
//        if (providerList != null) {
//            val currentProvider = providerList.filter { p -> p.hospitalId == 6 }
//            if (currentProvider.isNotEmpty()) {
//                changeProviderRequestViewModel.selectedProvider.value = currentProvider[0]
//            }
//        }
//    }
}