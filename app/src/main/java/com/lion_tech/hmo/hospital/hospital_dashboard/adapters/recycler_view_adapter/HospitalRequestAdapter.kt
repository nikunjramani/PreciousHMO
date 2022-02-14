package com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.hospital.hospital_dashboard.models.HospitalRequestModel
import com.lion_tech.hmo.hospital.hospital_dashboard.ui.request.HospitalRequestFrag
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class HospitalRequestAdapter(var context: Context, var frag: HospitalRequestFrag) :
    RecyclerView.Adapter<HospitalRequestAdapter.RequestViewModel>() {
    private val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US) as DateFormat;
    private val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private var requestList: List<HospitalRequestModel> = emptyList()
    private var hospitalLocId = ""
    fun refreshAdapter(newRequestList: List<HospitalRequestModel>) {
        this.requestList = (newRequestList.sortedBy { it.date }).reversed()
        hospitalLocId = AppLevelData.hospitalDetailJson!!.getString("loc_id")

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewModel {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_hospital_request, parent, false)

        return RequestViewModel(view)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: RequestViewModel, position: Int) {

        val inputText = requestList[position].date
        val date: Date = inputFormat.parse(inputText)!!


        var requestCode: String = requestList[position].requestCode
        if (requestCode == "null") {
            requestCode = "........."
        } else {
//            P-lag001-20200127-19
            val reqData = requestList[position].date
            val removeDashes = reqData.replace("-", "")
            requestCode = "P-$hospitalLocId-$removeDashes-$requestCode"

        }

        holder.tvRequestCode.text = requestCode
        holder.tvTotalBill.text = "₦${NumberFormat.getInstance().format(requestList[position].totalBill)}"
        holder.tvRequestDate.text = outputFormat.format(date)
//        holder.lvReject.visibility = View.GONE
        holder.tvEnrolleeName.text = requestList[position].enrolleeName
        holder.editConstraintLayout.visibility = View.GONE

        val requestStatus = requestList[position].requestStatus
        val billStatus = requestList[position].requestBillStatus
        val authCode = requestList[position].requestCode

//        <!--  data-toggle="modal" data-target="#myModal" onclick="return loadModalView(<?php echo $value->diagnose_id; ?>);" -->
//        <a class="btn btn-default" data-toggle="modal" data-target="#myModal" onclick="return loadModalView(<?php echo $value->diagnose_id; ?>);"><i class="fa fa-eye"></i></a>
//        <?php if (($value->diagnose_status == '2') && ($value->diagnose_bill_status == '')){ ?>
//            <p class="btn btn-success">Code Generated</p>
//            <a href="<?php echo base_url(); ?>hospital/clients/index?client_id=<?php echo $value->diagnose_client_id; ?>&hospital_id=<?php echo $value->diagnose_hospital_id; ?>&bill_request=yes&id=<?php echo $value->diagnose_id; ?>"  class="btn btn-info">Send Bill Request</a>
//
//
//            <?php }elseif($value->diagnose_bill_status == '3') { ?>
//            <p class="btn btn-success">Code Generated</p>
//            <p class="btn btn-success"> Bill Approved</p>


//            <?php }elseif($value->diagnose_status == '3') { ?>
//            <p class="btn btn-danger"> Rejected</p>

//            <?php }elseif($value->diagnose_bill_status == '4') { ?>
//            <p class="btn btn-success">Code Generated</p>
//            <p class="btn btn-danger"> Bill Rejected</p>


//            <?php }elseif(($value->diagnose_status == '2') && (($value->diagnose_bill_status == '1') || ($value->diagnose_bill_status == '2'))) { ?>
//            <p class="btn btn-success">Code Generated</p>
//            <p class="btn btn-warning"> Bill Requested</p>
//            <?php }else { ?>
//            <p class="btn btn-warning"> Requested For Code</p>
//
//            <?php } ?>


        if (requestStatus == "1") {
            holder.btnCode.text = context.getString(R.string.code_requested)
            holder.btnCode.setBackgroundColor(context.resources.getColor(R.color.requestForCode))

        } else if (requestStatus == "2" || requestStatus == "3") {

            holder.btnCode.setBackgroundColor(context.resources.getColor(R.color.authorized))
            holder.btnCode.text = context.getString(R.string.authorized)

        } else {
            holder.btnCode.setBackgroundColor(context.resources.getColor(R.color.requestForCode))
            holder.btnCode.text = context.getString(R.string.request_for_code)

        }


        if (billStatus == "1" || billStatus == "2") {

            holder.btnBill.text = context.getString(R.string.bill_requested)
            holder.btnBill.setBackgroundColor(context.resources.getColor(R.color.requestForCode))

        } else if (billStatus == "3") {
            holder.btnBill.text = context.getString(R.string.bill_approved)
            holder.btnBill.setBackgroundColor(context.resources.getColor(R.color.authorized))

        } else if (billStatus == "4") {
            holder.btnBill.setBackgroundColor(context.resources.getColor(R.color.rejectedColor))
            holder.btnBill.text = context.getString(R.string.bill_rejected)
            holder.lvReject.visibility = View.VISIBLE
//            holder.btnBillRejectReason.text = requestList[position].billRejectReason
            holder.editConstraintLayout.visibility = View.VISIBLE
        } else {
            holder.btnBill.text = context.getString(R.string.send_bill_request)
            holder.btnBill.setBackgroundColor(context.resources.getColor(R.color.sendBillRequest))

        }

        holder.btnBill.visibility = View.VISIBLE

        if ((requestList[position].codeRejectReason.isEmpty() || requestList[position].codeRejectReason == "null")
            && (requestList[position].billRejectReason.isEmpty() || requestList[position].billRejectReason == "null")
        ) {
            holder.lvReject.visibility = View.GONE

        } else {
            holder.lvReject.visibility = View.VISIBLE
            if (requestList[position].codeRejectReason.isNotEmpty() && requestList[position].codeRejectReason != "null") {
                holder.cvCodeRejectReason.visibility = View.VISIBLE
            } else {
                holder.cvCodeRejectReason.visibility = View.INVISIBLE
            }

            if (requestList[position].billRejectReason.isNotEmpty() && requestList[position].billRejectReason != "null") {
                holder.cvBillRejectReason.visibility = View.VISIBLE
            } else {
                holder.cvBillRejectReason.visibility = View.INVISIBLE
            }
        }

        holder.btnBillRejectReason.setOnClickListener {
            showReasonsDialog(requestList[position].billRejectReason,"Bill Reject Reasons")
        }
        holder.btnCodeRejectReason.setOnClickListener {
            showReasonsDialog(requestList[position].codeRejectReason,"Code Reject Reasons")
        }


        holder.btnCode.setOnClickListener {
            if (holder.btnCode.text.equals(context.getString(R.string.request_for_code))) {
                frag.sendRequestForCode(requestList[position].requestId.toString())
            }
        }

        holder.btnBill.setOnClickListener {
            if (holder.btnBill.text == context.getString(R.string.send_bill_request)) {
                if (requestStatus == "2" || requestStatus == "3") {
                    frag.sendRequestForBill(requestList[position].requestId.toString())
                } else {
                    Toast.makeText(
                        context,
                        "You can not send Bill request unless Authorization code is approved",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        holder.tvViewDescrption.setOnClickListener {
            //            AppLevelData.hospitalRequestFrag!!.showDepDetail(requestList[position], outputFormat.format(date))
            AppLevelData.hospitalRequestFrag!!.sendRequestForDrugsAndService(
                requestList[position],
                outputFormat.format(date)
            )
        }

        holder.ivEdit.setOnClickListener {
            frag.updateRequestData(
                requestList[position], outputFormat.format(date)
            )
        }
    }

    inner class RequestViewModel(view: View) : RecyclerView.ViewHolder(view) {
        var tvRequestCode: TextView = view.findViewById(R.id.tvReqCode)
        var tvEnrolleeName: TextView = view.findViewById(R.id.tvName)
        var tvRequestDate: TextView = view.findViewById(R.id.tvDate)
        var tvTotalBill: TextView = view.findViewById(R.id.tvTotalBill)
        var tvViewDescrption: Button = view.findViewById(R.id.tvViewDescription)
        var btnCode: Button = view.findViewById(R.id.btnCode)
        var ivEdit: ImageView = view.findViewById(R.id.ivEdit)
        var editConstraintLayout: ConstraintLayout = view.findViewById(R.id.constraintLayout)
        var btnBill: Button = view.findViewById(R.id.btnBill)
        var lvReject: LinearLayout = view.findViewById(R.id.lvReject)
        var btnBillRejectReason: Button = view.findViewById(R.id.btnBillRejectReason)
        var btnCodeRejectReason: Button = view.findViewById(R.id.btnCodeRejectReason)
        var cvBillRejectReason: CardView = view.findViewById(R.id.cvBillRejectReason)
        var cvCodeRejectReason: CardView = view.findViewById(R.id.cvCodeRejectReason)
    }

    private fun showReasonsDialog(rejectReason: String,title:String) {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_subscription_benefits, null, false)
        val tvTitle=view.findViewById<TextView>(R.id.tvTop)
        tvTitle.text=title
        val lvLayout = view.findViewById<LinearLayout>(R.id.lvSubscription)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val rejectList: List<String> = rejectReason.split(",")
       var counterForReason=0
        for (i in rejectList.indices) {
            val rowView = LayoutInflater.from(context)
                .inflate(R.layout.row_subscription_benefits, null, false)
            val tvNo = rowView.findViewById<TextView>(R.id.tvNo)
            tvNo.setTextColor(Color.RED)
            val tvBenefits = rowView.findViewById<TextView>(R.id.tvBenefits)
            if (rejectList[i].isNotEmpty()) {
                counterForReason+=1
                tvNo.text = "$counterForReason"
                tvBenefits.text = rejectList[i]
                lvLayout.addView(rowView)
            }
        }
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
        alertDialog.setView(view)

        val dialog = alertDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


}