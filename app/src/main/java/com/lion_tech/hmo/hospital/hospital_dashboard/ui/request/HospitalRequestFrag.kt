package com.lion_tech.hmo.hospital.hospital_dashboard.ui.request

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.hospital.hospital_dashboard.HospitalDashboard
import com.lion_tech.hmo.hospital.hospital_dashboard.activities.SelectDrugs
import com.lion_tech.hmo.hospital.hospital_dashboard.activities.SelectServices
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.autocompleteAdapters.EnrolleeAutoCompleteAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter.ClientDiagnoseDrugsAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter.ClientDiagnoseServiceAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter.HospitalRequestAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.spinner_adapters.DrugsSpinnerAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.spinner_adapters.ServicesSpinnerAdapter
import com.lion_tech.hmo.hospital.hospital_dashboard.models.DrugsModel
import com.lion_tech.hmo.hospital.hospital_dashboard.models.EnrolleeModel
import com.lion_tech.hmo.hospital.hospital_dashboard.models.HospitalRequestModel
import com.lion_tech.hmo.hospital.hospital_dashboard.models.ServicessModel
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class HospitalRequestFrag : Fragment() {

    private var dialog: AlertDialog? = null
    private lateinit var adapterRequestList: List<HospitalRequestModel>
    private var isUpdate: Boolean = false
    private var dProgressBar: ProgressBar? = null
    private lateinit var hospitalRequestFragViewModel: HospitalRequestFragViewModel
    private lateinit var spDrugAdapter: DrugsSpinnerAdapter
    private lateinit var spServicesAdapter: ServicesSpinnerAdapter
    private lateinit var rvRequest: RecyclerView
    private lateinit var rvAdapter: HospitalRequestAdapter


    var tvSelectedServices: TextView? = null
    var tvSelectedDrugs: TextView? = null
    var lvLayout: LinearLayout? = null

    private lateinit var enrolleeAdapter: EnrolleeAutoCompleteAdapter

    private lateinit var progressBar: ProgressBar
    private lateinit var rvClientDiagnoseServiceAdapter: ClientDiagnoseServiceAdapter
    private lateinit var rvClientDiagnoseDrugsAdapter: ClientDiagnoseDrugsAdapter

    private var hospitalRequestModel: HospitalRequestModel? = null
    private var requestDate: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hospitalRequestFragViewModel =
            ViewModelProvider(this).get(HospitalRequestFragViewModel::class.java)
        return inflater.inflate(R.layout.fragment_hospital_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HospitalDashboard).hideTextView(
            true,
            activity!!.getString(R.string.request)
        )
        AppLevelData.isHospitalComplaint = false
        AppLevelData.hospitalRequestFrag = this
        enrolleeAdapter = EnrolleeAutoCompleteAdapter(activity as HospitalDashboard)

        rvRequest = view.findViewById(R.id.rvRequest)
        progressBar = view.findViewById(R.id.progressBar)

        spDrugAdapter = DrugsSpinnerAdapter(activity as HospitalDashboard)
        spServicesAdapter = ServicesSpinnerAdapter(activity as HospitalDashboard)

        rvAdapter = HospitalRequestAdapter(activity as HospitalDashboard, this)
        rvClientDiagnoseServiceAdapter = ClientDiagnoseServiceAdapter(activity as HospitalDashboard)
        rvClientDiagnoseDrugsAdapter = ClientDiagnoseDrugsAdapter(activity as HospitalDashboard)

        rvRequest.layoutManager = LinearLayoutManager(activity as HospitalDashboard)
        rvRequest.adapter = rvAdapter
        if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
            Toast.makeText(context,context!!.getString(R.string.conection_problem),Toast.LENGTH_SHORT).show()
        }else{
            hospitalRequestFragViewModel.getTotalHospitalRequestList(activity as HospitalDashboard)
        }

        hospitalRequestFragViewModel.hospitalRequestList.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                adapterRequestList = it
                rvAdapter.refreshAdapter(it)
                progressBar.visibility = View.GONE
                (activity as HospitalDashboard).progressBar.visibility = View.GONE

                AppLevelData.hospitalRequestCount = it.size
                if (dProgressBar != null) {
                    dProgressBar!!.visibility = View.GONE
                    if (dialog != null) {
                        dialog!!.dismiss()
                    }

                    Toast.makeText(
                        activity as HospitalDashboard,
                        "Request added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })


        hospitalRequestFragViewModel.clientDiagnoseServicesAndDrugsJson.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer {

                //
                try {
                    val drugsList: ArrayList<DrugsModel> = ArrayList()
                    val serviceList: ArrayList<ServicessModel> = ArrayList()

                    val jsonObject = JSONObject(it)
                    val jsonServiceArray = jsonObject.getJSONArray("services")
                    val jsonDrugsArray = jsonObject.getJSONArray("drugs")

                    var drugTotalPrice: Float = 0f

                    for (i in 0 until jsonDrugsArray.length()) {
                        val jsonObject = jsonDrugsArray.getJSONObject(i)
                        var drugPrice: Float = jsonObject.getDouble("drug_price").toFloat()
                        drugTotalPrice += drugPrice
                        drugsList.add(
                            DrugsModel(
                                jsonObject.getInt("drug_id"),
                                jsonObject.getString("drug_name"),
                                drugPrice
                            )
                        )
                    }

                    var serviceTotalPrice: Float = 0f
                    for (i in 0 until jsonServiceArray.length()) {
                        val jsonObject = jsonServiceArray.getJSONObject(i)

                        var servicePrice = jsonObject.getDouble("service_price").toFloat()
                        serviceTotalPrice += servicePrice
                        serviceList.add(
                            ServicessModel(
                                jsonObject.getInt("id"),
                                jsonObject.getString("service_name"),
                                servicePrice
                            )
                        )
                    }

//                    AppLevelData.clientDiagnoseServicess = serviceList
//                    AppLevelData.clientDiagnoseDrugs = drugsList

                    AppLevelData.selectedServiceList = serviceList
                    AppLevelData.selectedDrugList = drugsList

                    AppLevelData.drugTotalPrice = drugTotalPrice
                    AppLevelData.serviceTotalPrice = serviceTotalPrice

                    progressBar.visibility = View.GONE
                    if (isUpdate) {
                        showUpdateDialog()
                    } else {
                        showDepDetail(serviceList, drugsList)
                    }
                } catch (exce: Exception) {

                }

                //
            })



        hospitalRequestFragViewModel.isRequestSent.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                //--------------- Hide progress Bar------------//
                // show toast ----------------//
            })



        (activity as HospitalDashboard).searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filterList = adapterRequestList!!.filter { model ->
                    model.enrolleeName.toLowerCase(Locale.US)
                        .contains(newText.toString().toLowerCase(Locale.US))
                }
                rvAdapter.refreshAdapter(filterList)
                return true
            }

        })


    }

    fun showDialog() {
        AppLevelData.selectedServiceList = ArrayList()
        AppLevelData.selectedDrugList = ArrayList()
        val views =
            LayoutInflater.from(activity).inflate(R.layout.dialog_hospital_request, null, false)

        val etSearchEnrollee: AutoCompleteTextView = views.findViewById(R.id.etSearchEnrollee)
        val etDiagnose: TextInputEditText = views.findViewById(R.id.etDiagnose)
        val etProcedures: TextInputEditText = views.findViewById(R.id.etProcedure)
        val etInvestigation: TextInputEditText = views.findViewById(R.id.etInvestigation)
        val etMedicalPersonnel: TextInputEditText = views.findViewById(R.id.etMedicalPersonnel)

        val btnSave: Button = views.findViewById(R.id.btnRequest)

        val tvDate: TextView = views.findViewById(R.id.tvDate)
        val tvDob: TextView = views.findViewById(R.id.tvDob)

        val tvSelectServices: TextView = views.findViewById(R.id.tvSelectServices)
        val tvSelectDrugs: TextView = views.findViewById(R.id.tvSelectDrugs)
        tvSelectedServices = views.findViewById(R.id.tvSelectedServices)
        tvSelectedDrugs = views.findViewById(R.id.tvSelectedDrugs)
//        tvSelectedServices!!.movementMethod = ScrollingMovementMethod();
//        tvSelectedDrugs!!.movementMethod = ScrollingMovementMethod();


        lvLayout = views.findViewById(R.id.lvLayout)

        dProgressBar = views.findViewById(R.id.progressBar)

        dProgressBar!!.visibility = View.VISIBLE

        if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
            Toast.makeText(context,context!!.getString(R.string.conection_problem),Toast.LENGTH_SHORT).show()
        }else{
            hospitalRequestFragViewModel.getHospitalRequestData(activity as HospitalDashboard)
        }


        hospitalRequestFragViewModel.hospitalRequestString.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                try {
                    val jsonObject = JSONObject(it)

                    val clientJson = jsonObject.getJSONArray("clientList")
                    val capitationJson = jsonObject.getJSONArray("capitationList")
                    val dependentJson = jsonObject.getJSONArray("hospitalDependentList")
                    val serviceJson = jsonObject.getJSONArray("serviceList")
                    val drugJson = jsonObject.getJSONArray("drugList")

                    val clientList: ArrayList<EnrolleeModel> = ArrayList()
                    val serviceList: ArrayList<ServicessModel> = ArrayList()
                    val drugList: ArrayList<DrugsModel> = ArrayList()

                    for (i in 0 until clientJson.length()) {
                        val clientJsonObject = clientJson.getJSONObject(i)
                        clientList.add(
                            EnrolleeModel(
                                clientJsonObject.getInt("client_id"),
                                clientJsonObject.getString("name"),
                                clientJsonObject.getString("last_name"),
                                clientJsonObject.getString("other_name"),
                                false,
                                "",
                                clientJsonObject.getString("created_at"),
                                "C",
                                "",
                                -1
                            )
                        )
                    }

                    for (i in 0 until capitationJson.length()) {
                        val capitationJsonObject = capitationJson.getJSONObject(i)
                        clientList.add(
                            EnrolleeModel(
                                capitationJsonObject.getInt("id"),
                                capitationJsonObject.getString("name"),
                                "", "",
                                true,
                                capitationJsonObject.getString("capitation_id"),
                                capitationJsonObject.getString("created_at"),
                                capitationJsonObject.getString("type"),
                                capitationJsonObject.getString("relation"),
                                -1

                            )
                        )
                    }

                    for (i in 0 until dependentJson.length()) {
                        val dependentObject = dependentJson.getJSONObject(i)
                        clientList.add(
                            EnrolleeModel(
                                dependentObject.getInt("clients_family_id"),
                                dependentObject.getString("name"),
                                dependentObject.getString("last_name"),
                                dependentObject.getString("other_name"),
                                false,
                                "",
                                dependentObject.getString("created_on"),
                                "D",
                                dependentObject.getString("relation"),
                                dependentObject.getInt("client_id")
                            )
                        )
                    }

                    for (i in 0 until serviceJson.length()) {
                        val serviceJsonObject = serviceJson.getJSONObject(i)
                        serviceList.add(
                            ServicessModel(
                                serviceJsonObject.getInt("id"),
                                serviceJsonObject.getString("service_name"),
                                serviceJsonObject.getDouble("service_price").toFloat()
                            )
                        )

                    }
                    for (i in 0 until drugJson.length()) {
                        val drugJsonObject = drugJson.getJSONObject(i)
                        drugList.add(
                            DrugsModel(
                                drugJsonObject.getInt("drug_id"),
                                drugJsonObject.getString("drug_name"),
                                drugJsonObject.getDouble("drug_price").toFloat()
                            )
                        )
                    }


                    enrolleeAdapter.refreshAdapter(clientList)
                    AppLevelData.serverServiceList = serviceList
                    AppLevelData.serverDrugList = drugList
                    dProgressBar!!.visibility = View.GONE

                } catch (ex: Exception) {

                }

            })



        setDate(tvDate, tvDob)

        tvDob.setOnClickListener {
            val newFragment = DatePickerFragment(tvDob)
            newFragment.show(fragmentManager!!, "datePicker")
        }

//

        tvSelectServices.setOnClickListener {

            (activity as HospitalDashboard).startActivity(
                Intent(
                    activity as HospitalDashboard,
                    SelectServices::class.java
                )
            )
        }

        tvSelectDrugs.setOnClickListener {

            (activity as HospitalDashboard).startActivity(
                Intent(
                    activity as HospitalDashboard,
                    SelectDrugs::class.java
                )
            )
        }

        var selectedEnrolledId = ""
        var isCapitation = false
        var userType = "C"

        etSearchEnrollee.setAdapter(enrolleeAdapter)

        etSearchEnrollee.onItemClickListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val enrolleeModel: EnrolleeModel =
                    parent!!.getItemAtPosition(position) as EnrolleeModel
                selectedEnrolledId = enrolleeModel.enrolleeId.toString()
                isCapitation = enrolleeModel.isCapitation
                userType = ""

                if (!isCapitation) {
                    userType =
                        enrolleeModel.capitationType   // if capitationType is also used for differentiate client and dependent
                }
            }

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val enrolleeModel: EnrolleeModel =
                    parent!!.getItemAtPosition(position) as EnrolleeModel
                selectedEnrolledId = enrolleeModel.enrolleeId.toString()
                isCapitation = enrolleeModel.isCapitation
                userType = ""

                if (!isCapitation) {
                    userType =
                        enrolleeModel.capitationType   // if capitationType is also used for differentiate client and dependent
                }
            }
        }



        btnSave.setOnClickListener {
            if (selectedEnrolledId.isEmpty()) {
                etSearchEnrollee.requestFocus()
                etSearchEnrollee.error =
                    (activity as HospitalDashboard).getString(R.string.name_required)
                return@setOnClickListener
            } else if (etDiagnose.text.toString().isEmpty()) {
                etDiagnose.requestFocus()
                etDiagnose.error =
                    (activity as HospitalDashboard).getString(R.string.required)
                return@setOnClickListener
            } else if (etProcedures.text.toString().isEmpty()) {
                etProcedures.requestFocus()
                etProcedures.error =
                    (activity as HospitalDashboard).getString(R.string.required)
                return@setOnClickListener
            } else if (etInvestigation.text.toString().isEmpty()) {
                etInvestigation.requestFocus()
                etInvestigation.error =
                    (activity as HospitalDashboard).getString(R.string.required)
                return@setOnClickListener
            } else if (etMedicalPersonnel.text.toString().isEmpty()) {
                etMedicalPersonnel.requestFocus()
                etMedicalPersonnel.error =
                    (activity as HospitalDashboard).getString(R.string.required)
                return@setOnClickListener
            }
            val capitationStatus = if (isCapitation) {
                "1"
            } else {
                "0"
            }
            if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
                Toast.makeText(context,context!!.getString(R.string.conection_problem),Toast.LENGTH_SHORT).show()
            return@setOnClickListener
            }
            hospitalRequestFragViewModel.addHospitalRequest(
                selectedEnrolledId,
                etDiagnose.text.toString(),
                etProcedures.text.toString(),
                etInvestigation.text.toString(),
                etMedicalPersonnel.text.toString(),
                tvDate.text.toString(),
                tvDob.text.toString(),
                capitationStatus,
                userType
            )
            //---------------- Show Progress Bar ----------------
            dProgressBar!!.visibility = View.VISIBLE

        }
        val alertDialog = AlertDialog.Builder(activity as HospitalDashboard)
        alertDialog.setView(views)

        dialog = alertDialog.create()
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog!!.show()


    }

    private fun showUpdateDialog() {

        val views =
            LayoutInflater.from(activity).inflate(R.layout.dialog_hospital_request, null, false)

        val etSearchEnrollee: AutoCompleteTextView = views.findViewById(R.id.etSearchEnrollee)
        val etDiagnose: TextInputEditText = views.findViewById(R.id.etDiagnose)
        val etProcedures: TextInputEditText = views.findViewById(R.id.etProcedure)
        val etInvestigation: TextInputEditText = views.findViewById(R.id.etInvestigation)
        val etMedicalPersonnel: TextInputEditText = views.findViewById(R.id.etMedicalPersonnel)

        val btnSave: Button = views.findViewById(R.id.btnRequest)

        val tvDate: TextView = views.findViewById(R.id.tvDate)
        val tvDob: TextView = views.findViewById(R.id.tvDob)

        val tvSelectServices: TextView = views.findViewById(R.id.tvSelectServices)
        val tvSelectDrugs: TextView = views.findViewById(R.id.tvSelectDrugs)
        tvSelectedServices = views.findViewById(R.id.tvSelectedServices)
        tvSelectedDrugs = views.findViewById(R.id.tvSelectedDrugs)

        lvLayout = views.findViewById(R.id.lvLayout)
        dProgressBar = views.findViewById(R.id.progressBar)
        var selectedEnrolledId = ""
        var isCapitation = false
        var userType = "C"

        //------------- Data to View -----------//
        setPrescribeDrugs()
        setPrescribeServices()
        etDiagnose.setText(hospitalRequestModel!!.diagnose)
        etMedicalPersonnel.setText(hospitalRequestModel!!.medical)
        etInvestigation.setText(hospitalRequestModel!!.investigation)
        etProcedures.setText(hospitalRequestModel!!.procedure)
        tvDate.text = hospitalRequestModel!!.date

        tvDob.text = hospitalRequestModel!!.dob
        etSearchEnrollee.setText(hospitalRequestModel!!.enrolleeName)
        selectedEnrolledId = hospitalRequestModel!!.clientId.toString()
        btnSave.text = "Update Request"
        isCapitation = hospitalRequestModel!!.capitationStatus == "1"
        if (isCapitation) {
            userType = ""
        } else {
            userType = hospitalRequestModel!!.userType
        }
        dProgressBar!!.visibility = View.VISIBLE

        if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
            Toast.makeText(context,context!!.getString(R.string.conection_problem),Toast.LENGTH_SHORT).show()
        }else {
            hospitalRequestFragViewModel.getHospitalRequestData(activity as HospitalDashboard)
        }

        hospitalRequestFragViewModel.hospitalRequestString.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                try {
                    val jsonObject = JSONObject(it)

                    val clientJson = jsonObject.getJSONArray("clientList")
                    val capitationJson = jsonObject.getJSONArray("capitationList")
                    val dependentJson = jsonObject.getJSONArray("hospitalDependentList")
                    val serviceJson = jsonObject.getJSONArray("serviceList")
                    val drugJson = jsonObject.getJSONArray("drugList")

                    val clientList: ArrayList<EnrolleeModel> = ArrayList()
                    val dependentList: ArrayList<EnrolleeModel> = ArrayList()
                    val serviceList: ArrayList<ServicessModel> = ArrayList()
                    val drugList: ArrayList<DrugsModel> = ArrayList()

                    for (i in 0 until clientJson.length()) {
                        val clientJsonObject = clientJson.getJSONObject(i)
                        clientList.add(
                            EnrolleeModel(
                                clientJsonObject.getInt("client_id"),
                                clientJsonObject.getString("name"),
                                clientJsonObject.getString("last_name"),
                                clientJsonObject.getString("other_name"),
                                false,
                                "",
                                clientJsonObject.getString("created_at"),
                                "C",
                                "",
                                -1
                            )
                        )
                    }

                    for (i in 0 until capitationJson.length()) {
                        val capitationJsonObject = capitationJson.getJSONObject(i)
                        clientList.add(
                            EnrolleeModel(
                                capitationJsonObject.getInt("id"),
                                capitationJsonObject.getString("name"),
                                "", "",
                                true,
                                capitationJsonObject.getString("capitation_id"),
                                capitationJsonObject.getString("created_at"),
                                capitationJsonObject.getString("type"),
                                capitationJsonObject.getString("relation"),
                                -1

                            )
                        )
                    }

                    for (i in 0 until dependentJson.length()) {
                        val dependentJsonObject = dependentJson.getJSONObject(i)
                        clientList.add(
                            EnrolleeModel(
                                dependentJsonObject.getInt("clients_family_id"),
                                dependentJsonObject.getString("name"),
                                dependentJsonObject.getString("last_name"),
                                dependentJsonObject.getString("other_name"),
                                false,
                                "",
                                dependentJsonObject.getString("created_on"),
                                "D",
                                dependentJsonObject.getString("relation"),
                                dependentJsonObject.getInt("client_id")
                            )
                        )
                    }

                    for (i in 0 until serviceJson.length()) {
                        val serviceJsonObject = serviceJson.getJSONObject(i)
                        serviceList.add(
                            ServicessModel(
                                serviceJsonObject.getInt("id"),
                                serviceJsonObject.getString("service_name"),
                                serviceJsonObject.getDouble("service_price").toFloat()
                            )
                        )

                    }
                    for (i in 0 until drugJson.length()) {
                        val drugJsonObject = drugJson.getJSONObject(i)
                        drugList.add(
                            DrugsModel(
                                drugJsonObject.getInt("drug_id"),
                                drugJsonObject.getString("drug_name"),
                                drugJsonObject.getDouble("drug_price").toFloat()
                            )
                        )
                    }


                    enrolleeAdapter.refreshAdapter(clientList)
                    AppLevelData.serverServiceList = serviceList
                    AppLevelData.serverDrugList = drugList
                    dProgressBar!!.visibility = View.GONE

                    Toast.makeText(activity as HospitalDashboard, "sdfa", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {

                }

            })




        tvDob.setOnClickListener {
            val newFragment = DatePickerFragment(tvDob)
            newFragment.show(fragmentManager!!, "datePicker")
        }

//

        tvSelectServices.setOnClickListener {

            (activity as HospitalDashboard).startActivity(
                Intent(
                    activity as HospitalDashboard,
                    SelectServices::class.java
                )
            )
        }

        tvSelectDrugs.setOnClickListener {

            (activity as HospitalDashboard).startActivity(
                Intent(
                    activity as HospitalDashboard,
                    SelectDrugs::class.java
                )
            )
        }



        etSearchEnrollee.setAdapter(enrolleeAdapter)

        etSearchEnrollee.onItemClickListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val enrolleeModel: EnrolleeModel =
                    parent!!.getItemAtPosition(position) as EnrolleeModel
                selectedEnrolledId = enrolleeModel.enrolleeId.toString()
                isCapitation = enrolleeModel.isCapitation
                userType = ""

                if (!isCapitation) {
                    userType =
                        enrolleeModel.capitationType   // if capitationType is also used for differentiate client and dependent
                }
            }

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val enrolleeModel: EnrolleeModel =
                    parent!!.getItemAtPosition(position) as EnrolleeModel
                selectedEnrolledId = enrolleeModel.enrolleeId.toString()
                isCapitation = enrolleeModel.isCapitation
                userType = ""

                if (!isCapitation) {
                    userType =
                        enrolleeModel.capitationType    // if capitationType is also used for differentiate client and dependent
                }
            }
        }



        btnSave.setOnClickListener {
            if (selectedEnrolledId.isEmpty()) {
                etSearchEnrollee.requestFocus()
                etSearchEnrollee.error =
                    (activity as HospitalDashboard).getString(R.string.name_required)
                return@setOnClickListener
            } else if (etDiagnose.text.toString().isEmpty()) {
                etDiagnose.requestFocus()
                etDiagnose.error =
                    (activity as HospitalDashboard).getString(R.string.required)
                return@setOnClickListener
            } else if (etProcedures.text.toString().isEmpty()) {
                etProcedures.requestFocus()
                etProcedures.error =
                    (activity as HospitalDashboard).getString(R.string.required)
                return@setOnClickListener
            } else if (etInvestigation.text.toString().isEmpty()) {
                etInvestigation.requestFocus()
                etInvestigation.error =
                    (activity as HospitalDashboard).getString(R.string.required)
                return@setOnClickListener
            } else if (etMedicalPersonnel.text.toString().isEmpty()) {
                etMedicalPersonnel.requestFocus()
                etMedicalPersonnel.error =
                    (activity as HospitalDashboard).getString(R.string.required)
                return@setOnClickListener
            }
            val capitationStatus = if (isCapitation) {
                "1"
            } else {
                "0"
            }

            if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
                Toast.makeText(context,context!!.getString(R.string.conection_problem),Toast.LENGTH_SHORT).show()
            return@setOnClickListener
            }
            hospitalRequestFragViewModel.updateHospitalRequest(
                selectedEnrolledId,
                etDiagnose.text.toString(),
                etProcedures.text.toString(),
                etInvestigation.text.toString(),
                etMedicalPersonnel.text.toString(),
                tvDate.text.toString(),
                tvDob.text.toString(),
                capitationStatus,
                hospitalRequestModel!!.requestId.toString(),
                userType
            )
            //---------------- Show Progress Bar ----------------
            dProgressBar!!.visibility = View.VISIBLE

        }
        val alertDialog = AlertDialog.Builder(activity as HospitalDashboard)
        alertDialog.setView(views)

        dialog = alertDialog.create()
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog!!.show()


    }


    fun setPrescribeDrugs() {
        lvLayout!!.visibility = View.VISIBLE
        var selectedDrugsText = "Selected Drugs : \n"
        for (mode in AppLevelData.selectedDrugList) {
            selectedDrugsText = "$selectedDrugsText ${mode.drugName}\n"
        }
        tvSelectedDrugs!!.text = selectedDrugsText
    }

    fun setPrescribeServices() {
        lvLayout!!.visibility = View.VISIBLE

        var selectedServiceText = "Selected Service : \n"
        for (mode in AppLevelData.selectedServiceList) {
            selectedServiceText = "$selectedServiceText ${mode.serviceName}\n"
        }
        tvSelectedServices!!.text = selectedServiceText
    }


    private fun setDate(tvDate: TextView, tvDob: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        tvDate.text = "$year/${month + 1}/$day"
        tvDob.text = "$year/${month + 1}/$day"
    }


    fun sendRequestForDrugsAndService(model: HospitalRequestModel, date: String) {
        isUpdate = false
        progressBar.visibility = View.VISIBLE
        hospitalRequestModel = model
        requestDate = date

        if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
            Toast.makeText(context,context!!.getString(R.string.conection_problem),Toast.LENGTH_SHORT).show()
        }else {
            hospitalRequestFragViewModel.getClientDiagnoseServiceAndDrugs(model.requestId.toString())
        }
    }


    fun showDepDetail(serviceList: List<ServicessModel>, drugsList: List<DrugsModel>) {

        val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_request_detail, null, false)
//
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvCode: TextView = view.findViewById(R.id.tvCode)
        val tvHospitalName: TextView = view.findViewById(R.id.tvHospitalName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvCodeTitle: TextView = view.findViewById(R.id.tvCodeTitle)
        val tvTotalBills: TextView = view.findViewById(R.id.tvBill)
        val tvDiagnose: TextView = view.findViewById(R.id.tvDiagnose)
        val tvMedical: TextView = view.findViewById(R.id.tvMedical)
        val tvInvestigation: TextView = view.findViewById(R.id.tvInvestigation)
        val tvProcedure: TextView = view.findViewById(R.id.tvProcedure)

        val rvServices: RecyclerView = view.findViewById(R.id.rvServices)
        val rvDrugs: RecyclerView = view.findViewById(R.id.rvDrugs)
        rvServices.layoutManager = LinearLayoutManager(activity)
        rvDrugs.layoutManager = LinearLayoutManager(activity)

        tvCode.visibility = View.VISIBLE
        tvCodeTitle.visibility = View.VISIBLE


        rvDrugs.adapter = rvClientDiagnoseDrugsAdapter
        rvServices.adapter = rvClientDiagnoseServiceAdapter

        rvClientDiagnoseDrugsAdapter.refreshAdapter(drugsList)
        rvClientDiagnoseServiceAdapter.refreshAdapter(serviceList)


        val btnDone: Button = view.findViewById(R.id.btnDone)

        if (hospitalRequestModel != null) {

//            var clientName = hospitalRequestModel!!.enrolleeName
//            if (hospitalRequestModel!!.otherName != "null") {
//                clientName = "$clientName ${hospitalRequestModel!!.otherName}"
//            }
//            if (hospitalRequestModel!!.lastName != "null") {
//                clientName = "$clientName ${hospitalRequestModel!!.lastName}"
//            }
            tvName.text = hospitalRequestModel!!.enrolleeName

            tvDate.text = requestDate
            tvTotalBills.text = "₦${hospitalRequestModel!!.totalBill}"
            tvDiagnose.text = hospitalRequestModel!!.diagnose
            tvInvestigation.text = hospitalRequestModel!!.investigation
            tvMedical.text = hospitalRequestModel!!.medical
            tvProcedure.text = hospitalRequestModel!!.procedure

            val hospitalJson = AppLevelData.hospitalDetailJson
            if (hospitalJson != null) {
                tvHospitalName.text = hospitalJson.getString("hospital_name")

            }


            var requestCode: String = hospitalRequestModel!!.requestCode
            if (requestCode == "null") {
                requestCode = "........."
            } else {
                val reqData = hospitalRequestModel!!.date
                val removeDashes = reqData.replace("-", "")
                requestCode =
                    "HCP-${hospitalJson!!.getString("hospital_name")}-$requestCode-$removeDashes-$requestCode"
            }
            tvCode.text = requestCode

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

    fun sendRequestForBill(requestId: String) {


        try {
            if(!AppLevelData.verifyAvailableNetwork(activity as HospitalDashboard)){
                Toast.makeText(activity as HospitalDashboard,(activity as HospitalDashboard).getString(R.string.conection_problem),Toast.LENGTH_SHORT).show()
            }else {
                (activity as HospitalDashboard).progressBar.visibility = View.VISIBLE
                hospitalRequestFragViewModel.sendRequestForBill(requestId)
            }
        } catch (ex: Exception) {
            Toast.makeText(context as HospitalDashboard, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun sendRequestForCode(requestId: String) {
        if(!AppLevelData.verifyAvailableNetwork(activity as HospitalDashboard)){
            Toast.makeText(activity as HospitalDashboard,(activity as HospitalDashboard).getString(R.string.conection_problem),Toast.LENGTH_SHORT).show()
        }else {
            (activity as HospitalDashboard).progressBar.visibility = View.VISIBLE
            hospitalRequestFragViewModel.sendRequestForCode(requestId)
        }
    }

    fun updateRequestData(model: HospitalRequestModel, date: String) {
        isUpdate = true
        progressBar.visibility = View.VISIBLE
        hospitalRequestModel = model
        requestDate = date

        if(!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)){
            Toast.makeText(context,context!!.getString(R.string.conection_problem),Toast.LENGTH_SHORT).show()
        }else {
            (activity as HospitalDashboard).progressBar.visibility = View.VISIBLE
            hospitalRequestFragViewModel.getClientDiagnoseServiceAndDrugs(model.requestId.toString())
        }
    }


    class DatePickerFragment(private var tvDates: TextView) : DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(activity as HospitalDashboard, this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {

//            Log.d("My Date",)

            tvDates.text = "$year/${month + 1}/$day"
        }
    }


}