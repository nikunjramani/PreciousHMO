package com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.hospital.hospital_dashboard.models.DrugsModel
import java.text.NumberFormat

class ClientDiagnoseDrugsAdapter(var context: Context) :
    RecyclerView.Adapter<ClientDiagnoseDrugsAdapter.DrugViewModel>() {
    private var drugList: List<DrugsModel> = emptyList()

    fun refreshAdapter(newdrugList: List<DrugsModel>) {
        this.drugList = newdrugList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugViewModel {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_client_diagnose_services_drugs, parent, false)

        return DrugViewModel(view)
    }

    override fun getItemCount(): Int {
        return drugList.size
    }

    override fun onBindViewHolder(holder: DrugViewModel, position: Int) {



        holder.tvdrugName.text = drugList[position].drugName
        holder.tvPrice.text ="₦${NumberFormat.getInstance().format(drugList[position].drugPrice)}"


    }

    inner class DrugViewModel(view: View) : RecyclerView.ViewHolder(view) {
        var tvdrugName: TextView = view.findViewById(R.id.tvName)
        var tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }


}