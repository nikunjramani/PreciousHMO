package com.lion_tech.hmo.hospital.hospital_dashboard.adapters.autocompleteAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.hospital.hospital_dashboard.models.EnrolleeModel

class EnrolleeAutoCompleteAdapter(var ctx: Context) : BaseAdapter(), Filterable {

    private var clientList: ArrayList<EnrolleeModel> = ArrayList()
    private var clientFullList: List<EnrolleeModel> = emptyList()

    fun refreshAdapter(newCLientList: List<EnrolleeModel>) {
        this.clientList = newCLientList as ArrayList
        this.clientFullList = newCLientList
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mView: View = LayoutInflater.from(ctx).inflate(R.layout.single_text_view, null, false)
        val tvProviderName = mView.findViewById(R.id.textView) as TextView

        var nameString: String = "${clientList[position].enrolleeName} "
        val createdDate: String = clientList[position].createdAt
        val splitDate = createdDate.split("-")

        if(clientList[position].lastName!="null"){
            nameString = "$nameString ${clientList[position].lastName}"
        }

        if(clientList[position].otherName!="null"){
            nameString = "$nameString ${clientList[position].otherName}"
        }
        nameString = "$nameString (LTM-${splitDate[0]}"

        if (clientList[position].isCapitation) {
            nameString = "$nameString-00${clientList[position].capitationId})"
            if (clientList[position].capitationType == "p") {
                nameString = "$nameString(Principal-NHIS)"
            } else {
                nameString = "$nameString(${clientList[position].relation}-NHIS)"
            }
        } else {
            if (clientList[position].capitationType == "D") {
                nameString =
                    "$nameString-00${clientList[position].dependentClientId}-00${clientList[position].enrolleeId})(${clientList[position].relation})"
            } else {
                nameString = "$nameString-00${clientList[position].enrolleeId})(Principal)"
            }
        }


        tvProviderName.text = nameString
        return mView
    }

    override fun getItem(position: Int): Any {
        return clientList[position]
    }

    override fun getItemId(position: Int): Long {
        return clientList[position].enrolleeId.toLong()
    }

    override fun getCount(): Int {
        return clientList.size
    }

    override fun getFilter(): Filter {
        return CustomFilter()
    }


    private inner class CustomFilter : Filter() {
        var filterResults = FilterResults()
        var sugestions: ArrayList<EnrolleeModel> = ArrayList()

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            if (constraint == null || constraint.isEmpty()) {
                sugestions.addAll(clientFullList)
            } else {
                var filterPattern = constraint.toString().toLowerCase().trim()

                var filterList: List<EnrolleeModel> = clientFullList.filter { clientModel ->
                    clientModel.enrolleeName.toLowerCase().contains(filterPattern)
                }

                sugestions = filterList as ArrayList<EnrolleeModel>
            }

            filterResults.values = sugestions
            filterResults.count = sugestions.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clientList = ArrayList()
            clientList.addAll((results!!.values) as List<EnrolleeModel>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as EnrolleeModel).enrolleeName
        }

    }

}