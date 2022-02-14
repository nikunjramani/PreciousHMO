package com.lion_tech.hmo.client.activities.adpaters.autocompleteAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.models.ProviderModel

class ProvidersAutoCompleteAdapter(var ctx: Context) : BaseAdapter(), Filterable {

    private var providerList: ArrayList<ProviderModel> = ArrayList()
    private var providerFullList: List<ProviderModel> = emptyList()

    fun refreshAdapter(newProviderList: List<ProviderModel>) {
        this.providerList = newProviderList as ArrayList
        this.providerFullList = newProviderList
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mView: View = LayoutInflater.from(ctx).inflate(R.layout.single_text_view, null, false)
        val tvProviderName = mView.findViewById(R.id.textView) as TextView
        tvProviderName.text = "${providerList[position].hospitalName} (${providerList[position].hospitalLocation})"
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

    override fun getFilter(): Filter {
         return  CustomFilter()
    }


    private inner class CustomFilter : Filter() {
        var filterResults = FilterResults()
        var sugestions: ArrayList<ProviderModel> = ArrayList()

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            if (constraint == null || constraint.isEmpty()) {
                sugestions.addAll(providerFullList)
            } else {
                var filterPattern = constraint.toString().toLowerCase().trim()

                var filterList: List<ProviderModel> = providerFullList.filter { providerModel ->
                    providerModel.hospitalName.toLowerCase().contains(filterPattern)
                }

                sugestions= filterList as ArrayList<ProviderModel>
            }

            filterResults.values = sugestions
            filterResults.count = sugestions.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            providerList = ArrayList()
            providerList.addAll((results!!.values) as List<ProviderModel>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as ProviderModel).hospitalName
        }

    }
}