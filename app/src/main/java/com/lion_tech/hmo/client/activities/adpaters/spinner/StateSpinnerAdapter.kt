package com.lion_tech.hmo.client.activities.adpaters.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.models.StateModel

class StateSpinnerAdapter(var ctx: Context) : BaseAdapter() {

    private var stateList: List<StateModel> = emptyList()

    fun refreshAdapter(newStateList: List<StateModel>) {
        this.stateList = newStateList
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mView: View = LayoutInflater.from(ctx).inflate(R.layout.single_text_view, null, false)
        val tvLocationName = mView.findViewById(R.id.textView) as TextView
        tvLocationName.text = stateList[position].locationName
        return mView
    }

    override fun getItem(position: Int): Any {
        return stateList[position]
    }

    override fun getItemId(position: Int): Long {
        return stateList[position].locationId.toLong()
    }

    override fun getCount(): Int {
        return stateList.size
    }
}