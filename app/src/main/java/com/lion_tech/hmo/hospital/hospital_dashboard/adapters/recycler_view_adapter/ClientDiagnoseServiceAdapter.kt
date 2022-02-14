package com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.hospital.hospital_dashboard.models.ServicessModel
import java.text.NumberFormat

class ClientDiagnoseServiceAdapter(var context: Context) :
    RecyclerView.Adapter<ClientDiagnoseServiceAdapter.ServiceViewModel>() {
    private var serviceList: List<ServicessModel> = emptyList()

    fun refreshAdapter(newServiceList: List<ServicessModel>) {
        this.serviceList = newServiceList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewModel {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_client_diagnose_services_drugs, parent, false)

        return ServiceViewModel(view)
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: ServiceViewModel, position: Int) {



        holder.tvServiceName.text = serviceList[position].serviceName
        holder.tvPrice.text = "₦${NumberFormat.getInstance().format(serviceList[position].servicePrice)}"

    }

    inner class ServiceViewModel(view: View) : RecyclerView.ViewHolder(view) {
        var tvServiceName: TextView = view.findViewById(R.id.tvName)
        var tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }


}