package com.lion_tech.hmo.hospital.hospital_dashboard.ui.bills

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.hospital.hospital_dashboard.HospitalDashboard
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter.ClientDiagnoseDrugsAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter.ClientDiagnoseServiceAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter.HospitalBillAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.models.DrugsModel
import com.lion_tech.hmo.hospital.hospital_dashboard.models.HospitalBillModel
import com.lion_tech.hmo.hospital.hospital_dashboard.models.ServicessModel
import org.json.JSONObject
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HospitalBillsFrag : Fragment() {

    private var approvedBillList: List<HospitalBillModel> = emptyList()
    private lateinit var hospitalBillFragViewModel: HospitalBillsFragViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var rvClientDiagnoseServiceAdapter: ClientDiagnoseServiceAdapter
    private lateinit var rvClientDiagnoseDrugsAdapter: ClientDiagnoseDrugsAdapter
    private lateinit var rvBills: RecyclerView
    private lateinit var rvAdapter: HospitalBillAdapter
    private var hospitalBillModel: HospitalBillModel? = null
    private var requestDate: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        hospitalBillFragViewModel =
            ViewModelProvider(this).get(HospitalBillsFragViewModel::class.java)
        return inflater.inflate(R.layout.fragment_hospital_bills, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HospitalDashboard).hideTextView(
            true,
            activity!!.getString(R.string.approved_bills)
        )

        AppLevelData.hospitalBillFrag = this
        AppLevelData.isPaidBill=false

        rvBills = view.findViewById(R.id.rvRequest)
        progressBar = view.findViewById(R.id.progressBar)

        rvAdapter = HospitalBillAdapter(activity as HospitalDashboard, this)
        rvClientDiagnoseServiceAdapter = ClientDiagnoseServiceAdapter(activity as HospitalDashboard)
        rvClientDiagnoseDrugsAdapter = ClientDiagnoseDrugsAdapter(activity as HospitalDashboard)

        rvBills.layoutManager = LinearLayoutManager(activity as HospitalDashboard)
        rvBills.adapter = rvAdapter

        if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
            Toast.makeText(context,context!!.getString(R.string.conection_problem), Toast.LENGTH_SHORT).show()
        }else {
            hospitalBillFragViewModel.getHospitalAuthBillList(activity as HospitalDashboard)
        }
        hospitalBillFragViewModel.hospitalAuthBills.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {

                approvedBillList = it
                rvAdapter.refreshAdapter(it)
                progressBar.visibility = View.GONE
            })


        hospitalBillFragViewModel.clientDiagnoseServicesAndDrugsJson.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer {

                //
                try {
                    val drugsList: ArrayList<DrugsModel> = ArrayList()
                    val serviceList: ArrayList<ServicessModel> = ArrayList()

                    val jsonObject = JSONObject(it)
                    val jsonServiceArray = jsonObject.getJSONArray("services")
                    val jsonDrugsArray = jsonObject.getJSONArray("drugs")

                    for (i in 0 until jsonDrugsArray.length()) {
                        val jsonObject = jsonDrugsArray.getJSONObject(i)
                        drugsList.add(
                            DrugsModel(
                                jsonObject.getInt("drug_id"),
                                jsonObject.getString("drug_name"),
                                jsonObject.getDouble("drug_price").toFloat()
                            )
                        )
                    }

                    for (i in 0 until jsonServiceArray.length()) {
                        val jsonObject = jsonServiceArray.getJSONObject(i)
                        serviceList.add(
                            ServicessModel(
                                jsonObject.getInt("id"),
                                jsonObject.getString("service_name"),
                                jsonObject.getDouble("service_price").toFloat()
                            )
                        )
                    }

                    AppLevelData.clientDiagnoseServicess = serviceList
                    AppLevelData.clientDiagnoseDrugs = drugsList

                    progressBar.visibility = View.GONE
                    showDepDetail(serviceList, drugsList)
                } catch (exce: Exception) {

                }

                //
            })
    }


    fun sendRequestForDrugsAndService(model: HospitalBillModel, date: String) {

        progressBar.visibility = View.VISIBLE
        hospitalBillModel = model
        requestDate = date
        if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
            Toast.makeText(context,context!!.getString(R.string.conection_problem), Toast.LENGTH_SHORT).show()
        }else {
            hospitalBillFragViewModel.getClientDiagnoseServiceAndDrugs(model.requestId.toString())
        }

    }


    fun showDepDetail(serviceList: List<ServicessModel>, drugsList: List<DrugsModel>) {

        val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_request_detail, null, false)
//
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvCode: TextView = view.findViewById(R.id.tvCode)
        val tvCodeTitle: TextView = view.findViewById(R.id.tvCodeTitle)
        val tvHospitalName: TextView = view.findViewById(R.id.tvHospitalName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)

        val tvTotalBills: TextView = view.findViewById(R.id.tvBill)
        val tvDiagnose: TextView = view.findViewById(R.id.tvDiagnose)
        val tvMedical: TextView = view.findViewById(R.id.tvMedical)
        val tvInvestigation: TextView = view.findViewById(R.id.tvInvestigation)
        val tvProcedure: TextView = view.findViewById(R.id.tvProcedure)

        val rvServices: RecyclerView = view.findViewById(R.id.rvServices)
        val rvDrugs: RecyclerView = view.findViewById(R.id.rvDrugs)
        rvServices.layoutManager = LinearLayoutManager(activity)
        rvDrugs.layoutManager = LinearLayoutManager(activity)

        rvDrugs.adapter = rvClientDiagnoseDrugsAdapter
        rvServices.adapter = rvClientDiagnoseServiceAdapter

        rvClientDiagnoseDrugsAdapter.refreshAdapter(drugsList)
        rvClientDiagnoseServiceAdapter.refreshAdapter(serviceList)


        val btnDone: Button = view.findViewById(R.id.btnDone)

        if (hospitalBillModel != null) {



//            var clientName = hospitalBillModel!!.enrolleeName
//            if (hospitalBillModel!!.otherName != "null") {
//                clientName = "$clientName ${hospitalBillModel!!.otherName}"
//            }
//            if (hospitalBillModel!!.lastName != "null") {
//                clientName = "$clientName ${hospitalBillModel!!.lastName}"
//            }
            tvName.text =hospitalBillModel!!.enrolleeName

            tvDate.text = requestDate
            tvTotalBills.text = "₦${hospitalBillModel!!.totalBill}"
            tvDiagnose.text = hospitalBillModel!!.diagnose
            tvInvestigation.text = hospitalBillModel!!.investigation
            tvMedical.text = hospitalBillModel!!.medical
            tvProcedure.text = hospitalBillModel!!.procedure

            val hospitalJson = AppLevelData.hospitalDetailJson
            if (hospitalJson != null) {
                tvHospitalName.text = hospitalJson.getString("hospital_name")

            }

            var requestCode: String = hospitalBillModel!!.requestCode
            if (requestCode == "null") {
                requestCode = "........."
            }
            tvCode.text = requestCode
            tvCode.visibility=View.GONE
            tvCodeTitle.visibility=View.GONE

        }
        val alertDialog = android.app.AlertDialog.Builder(context)
        alertDialog.setView(view)

        val dialog = alertDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnDone.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


    }

    fun filterBills(fromDateStrin: String, toDateString: String) {
//        String dtStart = "2010-10-15T09:27:37Z";
        val format: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy");
        val format1: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd");
        try {

            var filterList: ArrayList<HospitalBillModel> = ArrayList()

            val fromDate: Date = format.parse(fromDateStrin);
            val toDate: Date = format.parse(toDateString);

            for (model in approvedBillList) {
                val billDate: Date = format1.parse(model.date)
                val isDateIn = fromDate.compareTo(billDate) * billDate.compareTo(toDate) >= 0
                if (isDateIn) {
                    filterList.add(model)
                }
            }

            rvAdapter.refreshAdapter(filterList)

        } catch (e: ParseException) {
            e.printStackTrace();
        }
    }

}