package com.lion_tech.hmo.hospital.hospital_dashboard.adapters.spinner_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.hospital.hospital_dashboard.models.ServicessModel

class ServicesSpinnerAdapter(var ctx: Context) : BaseAdapter() {
    private var servicesList: List<ServicessModel> = emptyList()

    fun refreshAdapter(newServicesList: List<ServicessModel>) {
        this.servicesList = newServicesList
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mView: View = LayoutInflater.from(ctx).inflate(R.layout.single_text_view, null, false)
        val tvServiceName = mView.findViewById(R.id.textView) as TextView
        tvServiceName.text = servicesList[position].serviceName
        return mView
    }

    override fun getItem(position: Int): Any {
        return servicesList[position]
    }

    override fun getItemId(position: Int): Long {
        return servicesList[position].serviceId.toLong()
    }

    override fun getCount(): Int {
        return servicesList.size
    }
}