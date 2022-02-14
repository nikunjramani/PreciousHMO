package com.lion_tech.hmo.hospital.hospital_dashboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter.HospitalRequestServiceAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.models.ServicessModel
import java.util.*
import kotlin.collections.ArrayList

class SelectServices : AppCompatActivity() {

    private var selectedList: ArrayList<ServicessModel> = ArrayList()
    private var selectionList: ArrayList<ServicessModel> = ArrayList()
    lateinit var btnDone: Button
    lateinit var rvSelection: RecyclerView
    lateinit var rvSelected: RecyclerView
    lateinit var tvSectionTitle: TextView
    lateinit var tvSelectedTitle: TextView
    lateinit var searchView: SearchView

    private lateinit var rvSelectionAdapter: HospitalRequestServiceAdapter
    private lateinit var rvSelectedAdapter: HospitalRequestServiceAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_services)

        this.setFinishOnTouchOutside(false)
        val params = window.attributes
        params.height = (params.height * 0.8).toInt()
        params.width = (params.width * 0.8).toInt()
        this.window.attributes = params

        rvSelectionAdapter = HospitalRequestServiceAdapter(this, true)
        rvSelectedAdapter = HospitalRequestServiceAdapter(this, false)

        btnDone = findViewById(R.id.btnDone)
        rvSelection = findViewById(R.id.rvDrugsAnServices)
        rvSelected = findViewById(R.id.rvSelectedDrugsAnServices)
        tvSectionTitle = findViewById(R.id.tvSelectionTitle)
        tvSelectedTitle = findViewById(R.id.tvSelectionTitle)
        searchView = findViewById(R.id.searchView)


        rvSelection.layoutManager = LinearLayoutManager(this)
        rvSelection.adapter = rvSelectionAdapter
        rvSelected.layoutManager = LinearLayoutManager(this)
        rvSelected.adapter = rvSelectedAdapter

        selectionList = AppLevelData.serverServiceList
        rvSelectionAdapter.refreshAdapter(selectionList)

        if (AppLevelData.selectedServiceList.isNotEmpty()) {
            selectedList = AppLevelData.selectedServiceList
            rvSelectedAdapter.refreshAdapter(AppLevelData.selectedServiceList)
        }



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filterList = selectionList.filter { serviceModel ->
                    serviceModel.serviceName.toLowerCase(Locale.US)
                        .contains(newText.toString().toLowerCase(Locale.US))
                }
                rvSelectionAdapter.refreshAdapter(filterList)
                return true
            }

        })

        btnDone.setOnClickListener {

            var totalPrice: Float = 0f
            for (model in selectedList) {
                totalPrice += model.servicePrice
            }
            AppLevelData.serviceTotalPrice = totalPrice
            AppLevelData.selectedServiceList = selectedList
            AppLevelData.hospitalRequestFrag!!.setPrescribeServices()
            finish()
        }
    }


    fun addServiceFromSelectionList(model: ServicessModel) {
        selectedList.add(model)
        rvSelectedAdapter.refreshAdapter(selectedList)
    }

    fun removeServiceFromSelectionList(model: ServicessModel) {
        selectedList.remove(model)
        rvSelectedAdapter.refreshAdapter(selectedList)
    }

}
