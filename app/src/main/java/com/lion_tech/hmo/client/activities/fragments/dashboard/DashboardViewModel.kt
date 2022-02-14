package com.lion_tech.hmo.client.activities.fragments.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class DashboardViewModel : ViewModel() {

    var dashboardCountData: MutableLiveData<String> = MutableLiveData()
    var hospitalDashboardCountData: MutableLiveData<String> = MutableLiveData()
    lateinit var context: Context

    fun getDashboardCount(context: Context){
        this.context=context
        GetClientDashboardData().execute()
    }

    fun getHospitalDashboardCount(context: Context){
        this.context=context
        GetHospitalDashboardData().execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetClientDashboardData : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("${ServerUrls.GET_CLIENT_DASHBOARD_COUNT}/${AppLevelData.clientId}")
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.clientId)
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
Log.d("count",result)
            if (result.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Something went wrong please try again!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                dashboardCountData.value = result

            }
        }
    }



    @SuppressLint("StaticFieldLeak")
    private inner class GetHospitalDashboardData : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("${ServerUrls.GET_HOSPITAL_DASHBOARD_COUNT}/${AppLevelData.hospitalId}")
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

                hospitalDashboardCountData.value = result

            }
        }
    }

}