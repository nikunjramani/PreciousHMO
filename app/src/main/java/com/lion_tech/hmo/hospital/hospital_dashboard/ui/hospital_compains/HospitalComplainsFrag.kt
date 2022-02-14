package com.lion_tech.hmo.hospital.hospital_dashboard.ui.hospital_compains

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.adpaters.recyclerViewAdapters.ComplaintRvAdapter
import com.lion_tech.hmo.client.activities.models.ComplaintModel
import com.lion_tech.hmo.hospital.hospital_dashboard.HospitalDashboard

class HospitalComplainsFrag : Fragment() {

    private lateinit var hospitalComplainsFragViewModel: HospitalComplainsFragViewModel




    private lateinit var dialogProgressBar: ProgressBar
    private lateinit var btnAddComplaint: Button
    private var dialog: AlertDialog? = null
    private lateinit var rvAdapter: ComplaintRvAdapter
    private lateinit var progressBar: ProgressBar
    private var complaintList:List<ComplaintModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hospitalComplainsFragViewModel =
            ViewModelProvider(this).get(HospitalComplainsFragViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_hospital_complains, container, false)


        (activity as HospitalDashboard).hideTextView(
            true,
            activity!!.getString(R.string.my_complains)
        )
        AppLevelData.hospitalComplaintObject = this
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppLevelData.isHospitalComplaint=true
        progressBar = view.findViewById(R.id.progressBar)
        dialogProgressBar = progressBar

        if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
            Toast.makeText(context,context!!.getString(R.string.conection_problem), Toast.LENGTH_SHORT).show()
        }else {
            hospitalComplainsFragViewModel.getHospitalComplains(activity as HospitalDashboard)
        }
        rvAdapter = ComplaintRvAdapter(activity as HospitalDashboard)

        val rvComplaint = view.findViewById<RecyclerView>(R.id.rvComplaint)
        rvComplaint.layoutManager = LinearLayoutManager(activity as HospitalDashboard)
        rvComplaint.adapter = rvAdapter

        hospitalComplainsFragViewModel.complainDataList.observe(viewLifecycleOwner, Observer {
           complaintList=it
            rvAdapter.refreshAdapter(it)

            AppLevelData.hospitalComplaintCount=it.size
            progressBar.visibility = View.GONE
            dialogProgressBar.visibility = View.GONE
            if (dialog != null) {
                dialog!!.dismiss()
            }
        })

    }

    fun showComplaintDialog() {
        val view =
            LayoutInflater.from(activity as HospitalDashboard)
                .inflate(R.layout.dialog_dependent_complaint, null, false)
        val etSubject: TextInputEditText = view.findViewById(R.id.etSubject)
        val etDetail: TextInputEditText = view.findViewById(R.id.etDescription)
        btnAddComplaint = view.findViewById(R.id.btnAddComplaint)
        dialogProgressBar = view.findViewById(R.id.progressBar)
        dialogProgressBar.visibility = View.GONE

        btnAddComplaint.setOnClickListener {

            if (etSubject.text.toString().isEmpty()) {
                etSubject.error = getString(R.string.subject_required)
                etSubject.requestFocus()
                return@setOnClickListener
            } else if (etDetail.text.toString().isEmpty()) {
                etDetail.error = "Complaint description required"
                etDetail.requestFocus()
                return@setOnClickListener
            }

            if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
                Toast.makeText(context,context!!.getString(R.string.conection_problem), Toast.LENGTH_SHORT).show()
              return@setOnClickListener
            }
            dialogProgressBar.visibility = View.VISIBLE
            btnAddComplaint.visibility = View.GONE
            hospitalComplainsFragViewModel.addHospitalTicket(etSubject.text.toString(), etDetail.text.toString())


        }


        val alertDialog = AlertDialog.Builder(activity as HospitalDashboard)
        alertDialog.setView(view)

        dialog = alertDialog.create()
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog!!.show()
    }

    fun filterComplain(date: String) {

        val compliantFilterList= complaintList.filter { complaintModel -> complaintModel.ticketDate.contains(date) }
        rvAdapter.refreshAdapter(compliantFilterList)

    }


}