package com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.hospital.hospital_dashboard.models.HospitalBillModel
import com.lion_tech.hmo.hospital.hospital_dashboard.ui.bills.HospitalBillsFrag
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class HospitalBillAdapter(var context: Context,var hospitalBillsFrag: HospitalBillsFrag) :
    RecyclerView.Adapter<HospitalBillAdapter.BillViewModel>() {
    private val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US) as DateFormat;
    private val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private var billList: List<HospitalBillModel> = emptyList()

    fun refreshAdapter(newBillList: List<HospitalBillModel>) {
        this.billList = newBillList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewModel {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_hospital_bills, parent, false)

        return BillViewModel(view)
    }

    override fun getItemCount(): Int {
        return billList.size
    }

    override fun onBindViewHolder(holder: BillViewModel, position: Int) {

        val inputText = billList[position].date
        val date: Date = inputFormat.parse(inputText)!!


        var requestCode: String = billList[position].requestCode
        if (requestCode == "null") {
            requestCode = "........."
        }

//        var clientName = billList[position].enrolleeName
//        if (billList[position].otherName != "null") {
//            clientName = "$clientName ${billList[position].otherName}"
//        }
//        if (billList[position].lastName != "null") {
//            clientName = "$clientName ${billList[position].lastName}"
//        }
        holder.tvEnrolleeName.text = billList[position].enrolleeName

        holder.tvTotalBill.text ="₦${NumberFormat.getInstance().format(billList[position].totalBill)}"
        holder.tvBillDate.text = outputFormat.format(date)
        holder.btnViewDescrption.setOnClickListener {
//            AppLevelData.hospitalRequestFrag!!.showDepDetail(requestList[position], outputFormat.format(date))
           hospitalBillsFrag.sendRequestForDrugsAndService(billList[position], outputFormat.format(date))


        }
    }

    inner class BillViewModel(view: View) : RecyclerView.ViewHolder(view) {
        var tvEnrolleeName: TextView = view.findViewById(R.id.tvName)
        var tvBillDate: TextView = view.findViewById(R.id.tvDate)
        var tvTotalBill: TextView = view.findViewById(R.id.tvBill)
        var btnViewDescrption: Button = view.findViewById(R.id.tvViewDescription)
    }


}