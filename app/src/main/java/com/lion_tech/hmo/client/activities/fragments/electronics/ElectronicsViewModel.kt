package com.lion_tech.hmo.client.activities.fragments.electronics

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ElectronicsViewModel : ViewModel() {
    val client = OkHttpClient()
    lateinit var context: Context
    var electronicsData: MutableLiveData<String> = MutableLiveData()


    fun getElectronicsData() {

        GetElectronicsData().execute()
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetElectronicsData : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {

            val request = Request.Builder()
                .url("${ServerUrls.GET_ELECTRONICS_DATA}/${AppLevelData.clientId}")
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

            if (result.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Something went wrong please try again!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                electronicsData.value = result
            }
        }
    }

}
