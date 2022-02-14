package com.lion_tech.hmo.client.activities.adpaters.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.models.ProviderModel

class ProviderSpinnerAdapter(var ctx: Context) : BaseAdapter() {

    private var providerList: List<ProviderModel> = emptyList()

    fun refreshAdapter(newProviderList: List<ProviderModel>) {
        this.providerList = newProviderList
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mView: View = LayoutInflater.from(ctx).inflate(R.layout.single_text_view, null, false)
        val tvProviderName = mView.findViewById(R.id.textView) as TextView
        tvProviderName.text = providerList[position].hospitalName
        return mView
    }

    override fun getItem(position: Int): Any {
        return providerList[position]
    }

    override fun getItemId(position: Int): Long {
        return providerList[position].hospitalId.toLong()
    }

    override fun getCount(): Int {
        return providerList.size
    }
}