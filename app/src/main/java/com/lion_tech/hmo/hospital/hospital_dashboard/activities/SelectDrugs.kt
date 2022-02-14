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
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter.HospitalRequestDrugsAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.models.DrugsModel
import java.util.*
import kotlin.collections.ArrayList

class SelectDrugs : AppCompatActivity() {

    private var selectedList: ArrayList<DrugsModel> = ArrayList()
    private var selectionList: ArrayList<DrugsModel> = ArrayList()
    lateinit var btnDone: Button
    lateinit var rvSelection: RecyclerView
    lateinit var rvSelected: RecyclerView
    lateinit var tvSectionTitle: TextView
    lateinit var tvSelectedTitle: TextView
    lateinit var searchView: SearchView

    private lateinit var rvSelectionAdapter: HospitalRequestDrugsAdapter
    private lateinit var rvSelectedAdapter:HospitalRequestDrugsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_drugs)

        this.setFinishOnTouchOutside(false)
        val params = window.attributes
        params.height = (params.height * 0.8).toInt()
        params.width = (params.width * 0.8).toInt()
        this.window.attributes = params

        rvSelectionAdapter=HospitalRequestDrugsAdapter(this,true)
        rvSelectedAdapter=HospitalRequestDrugsAdapter(this,false)

        btnDone = findViewById(R.id.btnDone)
        rvSelection = findViewById(R.id.rvDrugsAnServices)
        rvSelected = findViewById(R.id.rvSelectedDrugsAnServices)
        tvSectionTitle = findViewById(R.id.tvSelectionTitle)
        tvSelectedTitle = findViewById(R.id.tvSelectionTitle)
        searchView = findViewById(R.id.searchView)


        rvSelection.layoutManager= LinearLayoutManager(this)
        rvSelection.adapter=rvSelectionAdapter
        rvSelected.layoutManager= LinearLayoutManager(this)
        rvSelected.adapter=rvSelectedAdapter

        selectionList=AppLevelData.serverDrugList
        rvSelectionAdapter.refreshAdapter(selectionList)

        if(AppLevelData.selectedDrugList.isNotEmpty()){
            selectedList=AppLevelData.selectedDrugList
            rvSelectedAdapter.refreshAdapter(AppLevelData.selectedDrugList)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filterList = selectionList.filter { serviceModel ->
                    serviceModel.drugName.toLowerCase(Locale.US)
                        .contains(newText.toString().toLowerCase(Locale.US))
                }
                rvSelectionAdapter.refreshAdapter(filterList)
                return true
            }

        })


        btnDone.setOnClickListener {

            var totalPrice:Float= 0f
            for (model in selectedList){
                totalPrice+=model.drugPrice
            }
            AppLevelData.drugTotalPrice=totalPrice
            AppLevelData.selectedDrugList=selectedList
            AppLevelData.hospitalRequestFrag!!.setPrescribeDrugs()
            finish()
        }
    }


    fun addDrugFromSelectionList(model:DrugsModel){
        selectedList.add(model)
        rvSelectedAdapter.refreshAdapter(selectedList)
    }

    fun removeDrugFromSelectionList(model:DrugsModel){
        selectedList.remove(model)
        rvSelectedAdapter.refreshAdapter(selectedList)
    }

}
