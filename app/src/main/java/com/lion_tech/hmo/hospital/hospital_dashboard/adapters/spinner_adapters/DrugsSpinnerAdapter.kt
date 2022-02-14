package com.lion_tech.hmo.hospital.hospital_dashboard.adapters.spinner_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.hospital.hospital_dashboard.models.DrugsModel

class DrugsSpinnerAdapter(var ctx: Context) : BaseAdapter() {

    private var drugsList: List<DrugsModel> = emptyList()

    fun refreshAdapter(newDrugList: List<DrugsModel>) {
        this.drugsList = newDrugList
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mView: View = LayoutInflater.from(ctx).inflate(R.layout.single_text_view, null, false)
        val tvDrugName = mView.findViewById(R.id.textView) as TextView
        tvDrugName.text = drugsList[position].drugName
        return mView
    }

    override fun getItem(position: Int): Any {
        return drugsList[position]
    }

    override fun getItemId(position: Int): Long {
        return drugsList[position].drugId.toLong()
    }

    override fun getCount(): Int {
        return drugsList.size
    }
}