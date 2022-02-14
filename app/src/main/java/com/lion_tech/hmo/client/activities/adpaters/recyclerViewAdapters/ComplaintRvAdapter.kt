package com.lion_tech.hmo.client.activities.adpaters.recyclerViewAdapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.models.ComplaintModel

class ComplaintRvAdapter(var context: Context) :
    RecyclerView.Adapter<ComplaintRvAdapter.ComplaintViewModel>() {

    private var complaintList: List<ComplaintModel> = emptyList()

    fun refreshAdapter(complaintList: List<ComplaintModel>) {
        this.complaintList = complaintList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewModel {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_complaint, parent, false)

        return ComplaintViewModel(view)
    }

    override fun getItemCount(): Int {
        return complaintList.size
    }

    override fun onBindViewHolder(holder: ComplaintViewModel, position: Int) {

        holder.tvComplaintCode.text = complaintList[position].complaintCode
        holder.tvComplaintSubject.text = complaintList[position].complaintSubject
        holder.tvCompliantDate.text = complaintList[position].ticketDate
        holder.tvViewDescrption.setOnClickListener {
                        showDepDetail(complaintList[position])

        }
    }

    inner class ComplaintViewModel(view: View) : RecyclerView.ViewHolder(view) {
        var tvComplaintCode: TextView = view.findViewById(R.id.tvCompCode)
        var tvComplaintSubject: TextView = view.findViewById(R.id.tvSubject)
        var tvCompliantDate: TextView = view.findViewById(R.id.tvDate)
        var tvViewDescrption: TextView = view.findViewById(R.id.tvViewDescription)
    }

    private fun showDepDetail(model: ComplaintModel) {

        val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_complaint_description, null, false)

        val tvFirstName: TextView = view.findViewById(R.id.tvDescription)
        tvFirstName.text = model.complaintDescription

        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setView(view)

        val dialog = alertDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()


    }

}