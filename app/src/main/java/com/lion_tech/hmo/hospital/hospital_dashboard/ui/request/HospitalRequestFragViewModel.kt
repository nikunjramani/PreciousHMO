package com.lion_tech.hmo.hospital.hospital_dashboard.ui.request

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.hospital.hospital_dashboard.models.HospitalRequestModel
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class HospitalRequestFragViewModel : ViewModel() {

    var isRequestSent: MutableLiveData<Boolean> = MutableLiveData()
    var hospitalRequestString: MutableLiveData<String> = MutableLiveData()

    private lateinit var context: Context
    var hospitalRequestList: MutableLiveData<List<HospitalRequestModel>> = MutableLiveData()
    var clientDiagnoseServicesAndDrugsJson: MutableLiveData<String> = MutableLiveData()
    val client = OkHttpClient()


    fun getHospitalRequestData(context: Context) {
        this.context = context
        GetHospitalRequestData().execute()
    }

    fun addHospitalRequest(
        clientId: String,
        diagnose: String,
        procedure: String,
        investigation: String,
        medical: String,
        investigationDate: String,
        dateOfBirth: String,
        capitationStatus: String,
        userType: String

    ) {
        AddHospitalRequest().execute(
            clientId,
            medical,
            investigation,
            procedure,
            diagnose,
            investigationDate,
            dateOfBirth,
            capitationStatus,
        userType
        )
    }

    fun updateHospitalRequest(
        clientId: String,
        diagnose: String,
        procedure: String,
        investigation: String,
        medical: String,
        investigationDate: String,
        dateOfBirth: String,
        capitationStatus: String,
        requestId: String,
        userType: String
    ) {

        UpdateRequestData().execute(
            clientId,
            medical,
            investigation,
            procedure,
            diagnose,
            investigationDate,
            dateOfBirth,
            capitationStatus,
            requestId,
            userType
        )
    }

    fun getTotalHospitalRequestList(context: Context) {

        GetHospitalTotalRequests().execute()

    }

    fun getClientDiagnoseServiceAndDrugs(diagnoseId: String) {
        GetClientDiagnoseDrugsAndServices().execute(diagnoseId)

    }


    fun sendRequestForCode(requestId: String) {
        SendRequestForCode().execute(requestId)
    }

    fun sendRequestForBill(requestId: String) {

        SendRequestForBill().execute(requestId)
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetHospitalRequestData : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {

            val request = Request.Builder()
                .url("${ServerUrls.GET_HOSPITAL_REQUEST_DATA}/${AppLevelData.hospitalId}")
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.hospitalId)
                .addHeader("token", AppLevelData.token)
                .build()

            try {
                val response = client.newCall(request).execute()
                return response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result!!)

            if (result.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Something went wrong please try again!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                hospitalRequestString.value = result
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class SendRequestForCode : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {

            val request = Request.Builder()
                .url("${ServerUrls.SEND_REQUEST_FOR_CODE}/${params[0]}")
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.hospitalId)
                .addHeader("token", AppLevelData.token)
                .build()

            try {
                val response = client.newCall(request).execute()
                return response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result!!)

            if (result.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Something went wrong please try again!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                GetHospitalTotalRequests().execute()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SendRequestForBill : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {

            val request = Request.Builder()
                .url("${ServerUrls.SEND_REQUEST_FOR_BILL}/${params[0]!!}")
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.hospitalId)
                .addHeader("token", AppLevelData.token)
                .build()

            try {
                val response = client.newCall(request).execute()
                return response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result!!)

            if (result.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Something went wrong please try again!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                GetHospitalTotalRequests().execute()
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetClientDiagnoseDrugsAndServices : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {

            val request = Request.Builder()
                .url("${ServerUrls.GET_CLIENT_DIAGNOSE_DRUGS_SERVICES}/${params[0]}")
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.hospitalId)
                .addHeader("token", AppLevelData.token)
                .build()

            try {
                val response = client.newCall(request).execute()
                return response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result!!)

            if (result.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Something went wrong please try again!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                clientDiagnoseServicesAndDrugsJson.value = result
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetHospitalTotalRequests : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {

            val request = Request.Builder()
                .url("${ServerUrls.GET_HOSPITAL_TOTAL_REQUEST}/${AppLevelData.hospitalId}")
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.hospitalId)
                .addHeader("token", AppLevelData.token)
                .build()

            try {
                val response = client.newCall(request).execute()
                return response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result!!)

            if (result.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Something went wrong please try again!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val serverRequestList: ArrayList<HospitalRequestModel> = ArrayList()
                val serverJsonObject = JSONObject(result)
                val jsonArray: JSONArray = serverJsonObject.getJSONArray("requestList")
//                val capitationJsonArray: JSONArray = serverJsonObject.getJSONArray("capitationList")
                for (i in 0 until jsonArray.length()) {

                    val jsonObject = jsonArray.getJSONObject(i)
//                    val viewJsonObject = viewJsonArray.getJSONObject(i)
                    serverRequestList.add(
                        HospitalRequestModel(
                            jsonObject.getInt("diagnose_id"),
                            jsonObject.getInt("diagnose_client_id"),
                            jsonObject.getString("diagnose_generated_code"),
                            jsonObject.getString("diagnose_status"),
                            jsonObject.getString("diagnose_bill_status"),
                            jsonObject.getString("name"),
                            "", "",
                            jsonObject.getString("diagnose_reject_reason"),
                            jsonObject.getDouble("diagnose_total_sum").toFloat(),
                            jsonObject.getString("diagnose_date"),
                            jsonObject.getString("diagnose_diagnose"),
                            jsonObject.getString("diagnose_procedure"),
                            jsonObject.getString("diagnose_medical"),
                            jsonObject.getString("diagnose_investigation"),
                            jsonObject.getString("diagnose_reject_reason"),
                            jsonObject.getString("diagnose_bill_reject_reason"),
                            jsonObject.getString("is_capitation"),
                            jsonObject.getString("diagnose_user_type"),
                            jsonObject.getString("diagnose_date_of_birth")

                        )
                    )
                }

//                for (i in 0 until capitationJsonArray.length()) {
//
//                    val jsonObject = capitationJsonArray.getJSONObject(i)
////                    val viewJsonObject = viewJsonArray.getJSONObject(i)
//                    serverRequestList.add(
//                        HospitalRequestModel(
//                            jsonObject.getInt("diagnose_id"),
//                            jsonObject.getInt("diagnose_client_id"),
//                            jsonObject.getString("diagnose_generated_code"),
//                            jsonObject.getString("diagnose_status"),
//                            jsonObject.getString("diagnose_bill_status"),
//                            jsonObject.getString("name"),
//                            "",
//                            "",
//                            jsonObject.getString("diagnose_reject_reason"),
//                            jsonObject.getDouble("diagnose_total_sum").toFloat(),
//                            jsonObject.getString("diagnose_date"),
//                            jsonObject.getString("diagnose_diagnose"),
//                            jsonObject.getString("diagnose_procedure"),
//                            jsonObject.getString("diagnose_medical"),
//                            jsonObject.getString("diagnose_investigation"),
//                            jsonObject.getString("diagnose_reject_reason"),
//                            jsonObject.getString("diagnose_bill_reject_reason"),
//                            jsonObject.getString("is_capitation"),
//                            jsonObject.getString("diagnose_date_of_birth")
//                        )
//                    )
//
//
//                }

                hospitalRequestList.value = serverRequestList


            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AddHospitalRequest : AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg data: String?): String {

            val gson: Gson = GsonBuilder().create()
            val drugJsonArray: JsonArray? =
                gson.toJsonTree(AppLevelData.selectedDrugList).asJsonArray
            val serviceJsonArray: JsonArray? =
                gson.toJsonTree(AppLevelData.selectedServiceList).asJsonArray

            val jsonObject: JSONObject = JSONObject()


            jsonObject.put("drugs", drugJsonArray)
            jsonObject.put("services", serviceJsonArray)
            jsonObject.put("clientId", data[0])
            jsonObject.put("medical", data[1])
            jsonObject.put("investigation", data[2])
            jsonObject.put("procedure", data[3])
            jsonObject.put("diagnose", data[4])
            jsonObject.put("investigationDate", data[5])
            jsonObject.put("dateOfBirth", data[6])
            jsonObject.put("capitationStatus", data[7])
            jsonObject.put("diagnose_user_type", data[8])
            jsonObject.put(
                "totalSum",
                "${AppLevelData.drugTotalPrice + AppLevelData.serviceTotalPrice}"
            )


            var stringObject: String = jsonObject.toString()

            val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

            val body = stringObject.toRequestBody(JSON)

            val request = Request.Builder()
                .url("${ServerUrls.ADD_HOSPITAL_REQUEST_DATA}/${AppLevelData.hospitalId}")
                .post(body)
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.hospitalId)
                .addHeader("token", AppLevelData.token)
                .build()

            try {
                val response = client.newCall(request).execute()
                return response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
//            sendRequestBody("http://www.bing.com", )
            return stringObject
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            GetHospitalTotalRequests().execute()


        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class UpdateRequestData : AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg data: String?): String {

            val gson: Gson = GsonBuilder().create()
            val drugJsonArray: JsonArray? =
                gson.toJsonTree(AppLevelData.selectedDrugList).asJsonArray
            val serviceJsonArray: JsonArray? =
                gson.toJsonTree(AppLevelData.selectedServiceList).asJsonArray

            val jsonObject: JSONObject = JSONObject()


            jsonObject.put("drugs", drugJsonArray)
            jsonObject.put("services", serviceJsonArray)
            jsonObject.put("clientId", data[0])
            jsonObject.put("medical", data[1])
            jsonObject.put("investigation", data[2])
            jsonObject.put("procedure", data[3])
            jsonObject.put("diagnose", data[4])
            jsonObject.put("investigationDate", data[5])
            jsonObject.put("dateOfBirth", data[6])
            jsonObject.put("capitationStatus", data[7])
            jsonObject.put("requestId", data[8])
            jsonObject.put("diagnose_user_type", data[9])
            jsonObject.put(
                "totalSum",
                "${AppLevelData.drugTotalPrice + AppLevelData.serviceTotalPrice}"
            )


            var stringObject: String = jsonObject.toString()

            val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

            val body = stringObject.toRequestBody(JSON)

            val request = Request.Builder()
                .url("${ServerUrls.UPDATE_HOSPITAL_REQUEST_DATA}/${AppLevelData.hospitalId}")
                .post(body)
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.hospitalId)
                .addHeader("token", AppLevelData.token)
                .build()

            try {
                val response = client.newCall(request).execute()
                return response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
//            sendRequestBody("http://www.bing.com", )
            return stringObject
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            GetHospitalTotalRequests().execute()


        }
    }

}