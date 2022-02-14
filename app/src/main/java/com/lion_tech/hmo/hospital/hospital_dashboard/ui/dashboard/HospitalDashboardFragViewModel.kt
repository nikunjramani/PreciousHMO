package com.lion_tech.hmo.hospital.hospital_dashboard.ui.dashboard

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class HospitalDashboardFragViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text



    @SuppressLint("StaticFieldLeak")
    private inner class GetHospitalDetail : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("${ServerUrls.GET_ENROLLEE_DATA}/${AppLevelData.clientId}")
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

            Log.d("hospitalDetail",result)

//            if (result.isNullOrEmpty()) {
//                Toast.makeText(
//                    context,
//                    "Something went wrong please try again!!",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                clientDetail.value = result
//            }
        }
    }

}