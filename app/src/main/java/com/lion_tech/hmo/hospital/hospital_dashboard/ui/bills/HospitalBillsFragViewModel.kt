package com.lion_tech.hmo.hospital.hospital_dashboard.ui.bills

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.hospital.hospital_dashboard.models.HospitalBillModel
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class HospitalBillsFragViewModel : ViewModel() {


    private lateinit var context: Context
    var hospitalApprovedBills: MutableLiveData<List<HospitalBillModel>> = MutableLiveData()
    var hospitalAuthBills: MutableLiveData<List<HospitalBillModel>> = MutableLiveData()
    var clientDiagnoseServicesAndDrugsJson: MutableLiveData<String> = MutableLiveData()
    val client = OkHttpClient()


    fun getHospitalAuthBillList(context: Context) {
        this.context=context

        GetHospitalAuthBills().execute()

    }

    fun getHospitalApprovedBillList() {


        GetHospitalPaidBills().execute()

    }

    fun getClientDiagnoseServiceAndDrugs(diagnoseId: String) {

        GetClientDiagnoseDrugsAndServices().execute(diagnoseId)

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
    private inner class GetHospitalPaidBills : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {

            val request = Request.Builder()
                .url("${ServerUrls.GET_HOSPITAL_APPROVED_BILLS}/${AppLevelData.hospitalId}")
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
                val serverRequestList: ArrayList<HospitalBillModel> = ArrayList()

                val jsonServer: JSONObject = JSONObject(result)
                val clientBills: JSONArray = jsonServer.getJSONArray("clientBills")
                for (i in 0 until clientBills.length()) {

                    val jsonObject = clientBills.getJSONObject(i)
                    serverRequestList.add(
                        HospitalBillModel(
                            jsonObject.getInt("diagnose_id"),
                            jsonObject.getInt("diagnose_client_id"),
                            jsonObject.getString("diagnose_generated_code"),
                            jsonObject.getInt("diagnose_status"),
                            jsonObject.getString("name"),
                            "",
                            "",
                            jsonObject.getString("diagnose_reject_reason"),
                            jsonObject.getDouble("diagnose_total_sum").toFloat(),
                            jsonObject.getString("diagnose_date"),
                            jsonObject.getString("diagnose_diagnose"),
                            jsonObject.getString("diagnose_procedure"),
                            jsonObject.getString("diagnose_medical"),
                            jsonObject.getString("diagnose_investigation")

                        )
                    )
                }

                hospitalApprovedBills.value = serverRequestList
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetHospitalAuthBills : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {

            val request = Request.Builder()
                .url("${ServerUrls.GET_HOSPITAL_AUTH_BILLS}/${AppLevelData.hospitalId}")
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
                val serverRequestList: ArrayList<HospitalBillModel> = ArrayList()

                val jsonServer: JSONObject = JSONObject(result)
                val clientBills: JSONArray = jsonServer.getJSONArray("clientBills")
                for (i in 0 until clientBills.length()) {

                    val jsonObject = clientBills.getJSONObject(i)
                    serverRequestList.add(
                        HospitalBillModel(
                            jsonObject.getInt("diagnose_id"),
                            jsonObject.getInt("diagnose_client_id"),
                            jsonObject.getString("diagnose_generated_code"),
                            jsonObject.getInt("diagnose_status"),
                            jsonObject.getString("name"),
                            "",
                            "",
                            jsonObject.getString("diagnose_reject_reason"),
                            jsonObject.getDouble("diagnose_total_sum").toFloat(),
                            jsonObject.getString("diagnose_date"),
                            jsonObject.getString("diagnose_diagnose"),
                            jsonObject.getString("diagnose_procedure"),
                            jsonObject.getString("diagnose_medical"),
                            jsonObject.getString("diagnose_investigation")

                        )
                    )
                }

                hospitalAuthBills.value = serverRequestList
            }
        }
    }

}