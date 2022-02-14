package com.lion_tech.hmo.client.activities.fragments.enrolleeProfile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.DashboardClient
import com.lion_tech.hmo.client.activities.adpaters.autocompleteAdapters.ProvidersAutoCompleteAdapter
import com.lion_tech.hmo.client.activities.adpaters.spinner.StateSpinnerAdapter
import com.lion_tech.hmo.client.activities.models.ProviderModel
import com.lion_tech.hmo.client.activities.models.StateModel
import com.lion_tech.hmo.client.activities.models.SubscriptionBenefits
import com.lion_tech.hmo.server_urls.ServerUrls
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EnrolleeProfile : Fragment() {

    private lateinit var checkBoxLayout: LinearLayout
    private var filePath: String? = null
    private lateinit var enrolleeViewModel: EnrolleeViewModel

    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etOtherName: TextInputEditText
    private lateinit var etOrganizationName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etContactNo: TextInputEditText
    private lateinit var etResidentAddress: TextInputEditText
    private lateinit var etSubscriptionPackage: TextInputEditText
    private lateinit var etDiseaseComments: TextInputEditText
    private lateinit var btnPackages: Button
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBar1: ProgressBar

    private lateinit var tvDate: TextView

    private lateinit var spSex: Spinner
    private lateinit var spMaritalStatus: Spinner
    private lateinit var spState: Spinner
    private lateinit var atvProvider: AutoCompleteTextView


    private lateinit var providerSpinnerAdapter: ProvidersAutoCompleteAdapter
    private lateinit var stateSpinnerAdapter: StateSpinnerAdapter
    private var subscriptionBenefitsList: ArrayList<SubscriptionBenefits> = ArrayList()

    private var selectedProviderId: String = ""

    private var diseaseList: ArrayList<String> = ArrayList()
    private var organizationId: Int = 0;

    private var isAccountUpdated = false;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        enrolleeViewModel =
            ViewModelProvider(this).get(EnrolleeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_enrollee_profile, container, false)
        (activity as DashboardClient).hideTextView(
            false,
            activity!!.getString(R.string.enrollee_profile)
        )
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        providerSpinnerAdapter = ProvidersAutoCompleteAdapter(activity as DashboardClient)

        stateSpinnerAdapter = StateSpinnerAdapter(activity as DashboardClient)

        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etOtherName = view.findViewById(R.id.etOtherName)
        etOrganizationName = view.findViewById(R.id.etOrganizationName)
        etEmail = view.findViewById(R.id.etEmail)
        etContactNo = view.findViewById(R.id.etContactNo)
        etResidentAddress = view.findViewById(R.id.etResidentAddress)
        etSubscriptionPackage = view.findViewById(R.id.etSubscribePackage)
        progressBar = view.findViewById(R.id.progressBar)
        progressBar1 = view.findViewById(R.id.progressBar1)
        btnPackages = view.findViewById(R.id.btnShowBenefits)
        btnSave = view.findViewById(R.id.btnSave)
        etSubscriptionPackage.visibility = View.GONE
        btnPackages.visibility = View.GONE

        //--------------- Enrollee Medical History layout----------//
        val historyLayout = view.findViewById<View>(R.id.layoutEnrolleeM)
        etDiseaseComments = historyLayout.findViewById(R.id.etDetail)


        checkBoxLayout = historyLayout.findViewById(R.id.layoutMedical)

        for (innerLayoutIndex in 0 until checkBoxLayout.childCount) {

            val innerLayout: LinearLayout =
                checkBoxLayout.getChildAt(innerLayoutIndex) as LinearLayout
            val checkBoxLeft: CheckBox = innerLayout.getChildAt(0) as CheckBox
            checkBoxLeft.setOnClickListener {
                val checkBox = it as CheckBox
                if (checkBox.isChecked) {
                    diseaseList.add("${checkBox.text},")
                } else {
                    diseaseList.remove("${checkBox.text},")
                }

            }

            val checkBoxRight: CheckBox = innerLayout.getChildAt(1) as CheckBox
            checkBoxRight.setOnClickListener {
                val checkBox = it as CheckBox
                if (checkBox.isChecked) {
                    diseaseList.add("${checkBox.text},")
                } else {
                    diseaseList.remove("${checkBox.text},")
                }
            }

        }


        etOrganizationName.isFocusable = false
        etSubscriptionPackage.isFocusable = false

        tvDate = view.findViewById(R.id.tvDate)
        setDate()

        spSex = view.findViewById(R.id.spSex)
        atvProvider = view.findViewById(R.id.atcProvider)
        spMaritalStatus = view.findViewById(R.id.spMaritalStatus)
        spState = view.findViewById(R.id.spState)

        tvDate.setOnClickListener {
            val newFragment = DatePickerFragment(tvDate)
            newFragment.show(fragmentManager!!, "datePicker")
        }

        spState.adapter = stateSpinnerAdapter

        atvProvider.setAdapter(providerSpinnerAdapter)

        atvProvider.onItemClickListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val providerModel: ProviderModel =
                    parent!!.getItemAtPosition(position) as ProviderModel
                selectedProviderId = providerModel.hospitalId.toString()

            }

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val providerModel: ProviderModel =
                    parent!!.getItemAtPosition(position) as ProviderModel
                selectedProviderId = providerModel.hospitalId.toString()
            }
        }
        setAdapterToSpinners()

        setDataToTextField()

        enrolleeViewModel.clientDetail.observe(viewLifecycleOwner, Observer {

            val jsonObject = JSONObject(it)
            AppLevelData.clientDetailJson = jsonObject
//            setDataToTextField()
//            setProviderToTextField()
            progressBar.visibility = View.GONE
            progressBar1.visibility = View.GONE
        })

        enrolleeViewModel.subscriptionData.observe(viewLifecycleOwner, Observer {
            Log.d("subscription", it)
            val jsonArray = JSONArray(it)
            subscriptionBenefitsList.clear()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                subscriptionBenefitsList.add(
                    SubscriptionBenefits(
                        jsonObject.getInt("subscription_detail_id"),
                        jsonObject.getString("subscription_benifit")
                    )
                )

            }
            enrolleeViewModel.getAllProviders()

        })

        //---------- Set adapter to provider spinner
        enrolleeViewModel.providersData.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                providerSpinnerAdapter.refreshAdapter(it)

                //------- GET States -------------//
                enrolleeViewModel.getStateList()
            }
            AppLevelData.providerSpinnerAdapter = providerSpinnerAdapter

        })


        enrolleeViewModel.stateList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                stateSpinnerAdapter.refreshAdapter(it)
                if (selectedProviderId.isNotEmpty()) {
                    enrolleeViewModel.getClientHospital(selectedProviderId)
                } else {
                    progressBar.visibility = View.GONE
                    progressBar1.visibility = View.GONE
                }
            }
        })

        enrolleeViewModel.clientHospitalData.observe(viewLifecycleOwner, Observer {
            atvProvider.setText("${it.hospitalName} (${it.hospitalLocation})")

            progressBar.visibility = View.GONE
            progressBar1.visibility = View.GONE
        })

        enrolleeViewModel.isAccountUpdated.observe(viewLifecycleOwner, Observer {
            if (it) {
                isAccountUpdated = true;
                showWarningDialog("SUCCESSFUL! YOUR PROFILE WILL BE ACTIVATED WITHIN 24hrs")
            }
        })


        btnPackages.setOnClickListener {
            showPackageDialog()
        }


        btnSave.setOnClickListener {


            if (etFirstName.text.toString().trim().isEmpty()) {
                etFirstName.error = (activity as DashboardClient).getString(R.string.required)
                etFirstName.requestFocus()
                return@setOnClickListener

            } else if (etLastName.text.toString().trim().isEmpty()) {
                etLastName.error = (activity as DashboardClient).getString(R.string.required)
                etLastName.requestFocus()
                return@setOnClickListener

            } else if (etOrganizationName.text.toString().trim().isEmpty()) {
                etOrganizationName.error =
                    (activity as DashboardClient).getString(R.string.required)
                etOrganizationName.requestFocus()
                return@setOnClickListener

            } else if (etEmail.text.toString().trim().isEmpty()) {
                etEmail.error = (activity as DashboardClient).getString(R.string.required)
                etEmail.requestFocus()
                return@setOnClickListener

            } else if (etContactNo.text.toString().trim().isEmpty()) {
                etContactNo.error = (activity as DashboardClient).getString(R.string.required)
                etContactNo.requestFocus()
                return@setOnClickListener

            } else if (etResidentAddress.text.toString().trim().isEmpty()) {
                etResidentAddress.error = (activity as DashboardClient).getString(R.string.required)
                etResidentAddress.requestFocus()
                return@setOnClickListener
            } else if (spState.selectedItem == null) {
                Toast.makeText(
                    activity as DashboardClient,
                    getString(R.string.please_select_state),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (selectedProviderId.isEmpty()) {
                atvProvider.error = (activity as DashboardClient).getString(R.string.required)
                atvProvider.requestFocus()
                return@setOnClickListener
            }
            filePath = (activity as DashboardClient).filePath

            if (filePath == null) {
                Toast.makeText(
                    activity as DashboardClient,
                    "Select profile image",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            var fullDiseaseString = ""
            for (disease in diseaseList) {
                fullDiseaseString = "$fullDiseaseString$disease"
            }

            val selectedSex = spSex.selectedItem.toString()
            val selectedMaritalStatus = spMaritalStatus.selectedItem.toString()
            val selectedStateModel: StateModel = spState.selectedItem as StateModel
            val selectedStateId: Int = selectedStateModel.locationId

            if (!AppLevelData.verifyAvailableNetwork(context as DashboardClient)) {
                Toast.makeText(
                    context,
                    context!!.getString(R.string.conection_problem),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            progressBar1.visibility = View.VISIBLE
            UpdateClientData().execute(
                etFirstName.text.toString(),
                etLastName.text.toString(),
                etOtherName.text.toString(),
                etEmail.text.toString(),
                etContactNo.text.toString(),
                organizationId.toString(),
                etResidentAddress.text.toString(),
                tvDate.text.toString(),
                selectedSex,
                selectedMaritalStatus,
                fullDiseaseString,
                etDiseaseComments.text.toString(),
                selectedStateId.toString(),
                selectedProviderId
            )


        }


    }

    private fun setProviderToTextField() {

        if (selectedProviderId.isNotEmpty()) {

            enrolleeViewModel.getClientHospital(selectedProviderId)
//            val providerList = AppLevelData.providerList
//            if (providerList != null) {
//                val providerModel =
//                    AppLevelData.providerList!!.single { providerModel -> providerModel.hospitalId == selectedProviderId.toInt() }
//            }
        }
    }

    private fun setDataToTextField() {

        val jsonObject = AppLevelData.clientDetailJson
        if (jsonObject != null) {
            val clientId = jsonObject.getString("client_id")

            if (!clientId.isNullOrEmpty()) {
                //client_profile   hospital_id   LionTech
                etFirstName.setText(validateText(jsonObject.getString("name")))
                etLastName.setText(validateText(jsonObject.getString("last_name")))
                etOtherName.setText(validateText(jsonObject.getString("other_name")))
                etOrganizationName.setText(validateText(jsonObject!!.getString("org_name")))
                etContactNo.setText(validateText(jsonObject.getString("contact_number")))
                etEmail.setText(validateText(jsonObject.getString("email")))
                etResidentAddress.setText(validateText(jsonObject.getString("address_1")))
                etSubscriptionPackage.setText(validateText(jsonObject.getString("ind_family")))
                etDiseaseComments.setText(validateText(jsonObject.getString("disease_comment")))
                organizationId = (jsonObject.getString("company_name")).toInt()

                val subsId = validateText(jsonObject.getString("subscription_ids"))
                if (subsId.isNotEmpty()) {
                    if (!AppLevelData.verifyAvailableNetwork(context as DashboardClient)) {
                        Toast.makeText(
                            context,
                            context!!.getString(R.string.conection_problem),
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        enrolleeViewModel.getSubscription(jsonObject.getString("subscription_ids"))
                    }
                }


                Picasso.get()
                    .load("https://liontechhmo.liontech.com.ng/app/uploads/clients/${jsonObject.getString("client_profile")}")
                    .into((activity as DashboardClient).ivEnrolleeProfile)

                val sex = validateText(jsonObject.getString("sex"))
                val maritalStatus = validateText(jsonObject.getString("marital_status"))

                spSex.setSelection(
                    if (sex == "Male") {
                        0
                    } else {
                        1
                    }
                )
                spMaritalStatus.setSelection(
                    if (maritalStatus == "Single") {
                        0
                    } else {
                        1
                    }
                )
                var lastModified = validateText(jsonObject.getString("last_modified"))

                if (lastModified.isEmpty()) {
                    if (!isAccountUpdated) {
                        showWarningDialog((activity as DashboardClient).getString(R.string.please_fill_the_form_carefully_because_you_only_one_chance_to_update_your_profile))
                    }
                } else {
                    btnSave.visibility = View.GONE
                }

                selectedProviderId = validateText(jsonObject.getString("hospital_id"))

                checkCheckboxesForDiseases(validateText(jsonObject.getString("diseases")))


                Log.d("enrooledd data", jsonObject.toString())
            }
        }
    }

    private fun checkCheckboxesForDiseases(diseaseString: String) {

        if (diseaseString.isNotEmpty()) {
            val splitDisease: ArrayList<String> = diseaseString.split(",") as ArrayList<String>

            for (disease in splitDisease) {
                diseaseList.add("$disease,")
                for (innerLayoutIndex in 0 until checkBoxLayout.childCount) {

                    val innerLayout: LinearLayout =
                        checkBoxLayout.getChildAt(innerLayoutIndex) as LinearLayout
                    val checkBoxLeft: CheckBox = innerLayout.getChildAt(0) as CheckBox
                    if (checkBoxLeft.text == disease) {
                        checkBoxLeft.isChecked = true
                    }

                    val checkBoxRight: CheckBox = innerLayout.getChildAt(1) as CheckBox
                    if (checkBoxRight.text == disease) {
                        checkBoxRight.isChecked = true
                    }
                }
            }
        }


    }

    private fun showPackageDialog() {

        val view = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_subscription_benefits, null, false)
        val lvLayout = view.findViewById<LinearLayout>(R.id.lvSubscription)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)


        for (i in 0 until subscriptionBenefitsList.size) {
            val rowView = LayoutInflater.from(activity)
                .inflate(R.layout.row_subscription_benefits, null, false)
            val tvNo = rowView.findViewById<TextView>(R.id.tvNo)
            val tvBenefits = rowView.findViewById<TextView>(R.id.tvBenefits)

            tvNo.text = "${i + 1}"
            tvBenefits.text = subscriptionBenefitsList[i].benefitsName

            lvLayout.addView(rowView)
        }
        val alertDialog = AlertDialog.Builder(activity as DashboardClient)
        alertDialog.setView(view)

        val dialog = alertDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun showWarningDialog(message: String) {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.dialog_enrollee_warning, null, false)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val tvWarnings = view.findViewById<TextView>(R.id.tvWarning)
        tvWarnings.text = message
        val alertDialog = AlertDialog.Builder(activity as DashboardClient)
        alertDialog.setView(view)

        val dialog = alertDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setAdapterToSpinners() {

        // -------- Set adapter to sex spinner
        ArrayAdapter.createFromResource(
            activity as DashboardClient,
            R.array.sex_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spSex.adapter = adapter
        }

        // -------- Set adapter to MaritalStatus spinner
        ArrayAdapter.createFromResource(
            activity as DashboardClient,
            R.array.marital_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spMaritalStatus.adapter = adapter
        }

    }

    private fun validateText(text: String): String {
        if (text != "null") {
            return text
        }

        return ""
    }

    private fun setDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        tvDate.text = "$year/${month + 1}/$day"

    }


    class DatePickerFragment(private var tvDates: TextView, private var formate: String = "") :
        DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(activity as DashboardClient, this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {

//            Log.d("My Date",)


            if (formate.isNotEmpty()) {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = simpleDateFormat.parse("$year-${month + 1}-$day")
                var dateFormat = SimpleDateFormat("MMM yyyy", Locale.US)

                tvDates.text = dateFormat.format(date)

            } else {
                tvDates.text = "$year/${month + 1}/$day"

            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class UpdateClientData : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {

            val sourceFile = File(filePath);

            val MEDIA_TYPE_PNG = "image/*".toMediaTypeOrNull();

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "client_profile",
                    "mmm.png",
                    RequestBody.create(MEDIA_TYPE_PNG, sourceFile)
                )
                .addFormDataPart("name", params[0])
                .addFormDataPart("last_name", params[1])
                .addFormDataPart("other_name", params[2])
                .addFormDataPart("email", params[3])
                .addFormDataPart("contact_number", params[4])
                .addFormDataPart("org_id", params[5])
                .addFormDataPart("address_1", params[6])
                .addFormDataPart("dob", params[7])
                .addFormDataPart("sex", params[8])
                .addFormDataPart("marital_status", params[9])
                .addFormDataPart("diseases", params[10])
                .addFormDataPart("disease_comment", params[11])
                .addFormDataPart("state", params[12])
                .addFormDataPart("hospital_id", params[13])

                .build();

            val request = Request.Builder()
                .url("${ServerUrls.UPDATE_CLIENT_DATA}/${AppLevelData.clientId}")
                .post(requestBody)
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.clientId)
                .addHeader("token", AppLevelData.token)
                .build();

            val client = OkHttpClient();

            try {
                val response = client.newCall(request).execute()
                return response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d("Dependant result", result)

            try {
                if (result != "") {

                    val jsonObject: JSONObject = JSONObject(result)
                    val responseObject: JSONObject = jsonObject.getJSONObject("response")
                    if (responseObject.getInt("status") == 200) {
//                        Toast.makeText(
//                            activity as DashboardClient,
//                            "SUCCESSFUL! YOUR PROFILE WILL BE ACTIVATED WITHIN 24hrs",
//                            Toast.LENGTH_SHORT
//                        ).show()

                        (activity as DashboardClient).updateHeaderData(
                            etFirstName.text.toString(),
                            etEmail.text.toString(),
                            jsonObject.getString("profileImage")
                        )
                        btnSave.visibility = View.GONE

                        enrolleeViewModel.getEnrolleeData(activity as DashboardClient)
                        enrolleeViewModel.isAccountUpdated.value = true

                    } else {

                        Toast.makeText(
                            activity as DashboardClient,
                            "Some thing went wrong please try again1",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        activity as DashboardClient,
                        "Some thing went wrong please try again2",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (cs: Exception) {
                Toast.makeText(
                    activity as DashboardClient,
                    "Some thing went wrong please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
            progressBar1.visibility = View.GONE

        }
    }
}



