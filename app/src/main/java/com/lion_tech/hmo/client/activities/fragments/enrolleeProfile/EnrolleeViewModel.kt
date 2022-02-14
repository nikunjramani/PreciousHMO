package com.lion_tech.hmo.client.activities.fragments.enrolleeProfile

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.models.ProviderModel
import com.lion_tech.hmo.client.activities.models.StateModel
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class EnrolleeViewModel() : ViewModel() {

    var clientDetail: MutableLiveData<String> = MutableLiveData()
    var clientHospitalData: MutableLiveData<ProviderModel> = MutableLiveData()
    var hospitalDetail: MutableLiveData<String> = MutableLiveData()
    var isAccountUpdated: MutableLiveData<Boolean> = MutableLiveData()
    var providersData: MutableLiveData<List<ProviderModel>> = MutableLiveData()
    var stateList: MutableLiveData<List<StateModel>> = MutableLiveData()
    var subscriptionData: MutableLiveData<String> = MutableLiveData()
    lateinit var context: Context


    fun getEnrolleeData(context: Context) {

        this.context = context
        GetEnrolleeData().execute()
    }

    fun getHospitalDetail(context: Context) {
        this.context = context
        GetHospitalDetail().execute()
    }

    fun getAllProviders() {
        GetAllProviders().execute()
    }

    fun getSubscription(providerId: String) {
        GetSubscription().execute(providerId)
    }

    fun getStateList() {
        GetStatesList().execute()
    }

    fun getClientHospital(selectedHospitalId: String) {
        GetClientHospitalData().execute(selectedHospitalId)
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetEnrolleeData : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val client = OkHttpClient()
            var clientId = AppLevelData.clientId
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
            Log.d("client_detail", result)
            if (result.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Something went wrong please try again!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                clientDetail.value = result
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetHospitalDetail : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("${ServerUrls.HOSPITAL_DETAIL}/${AppLevelData.hospitalId}")
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
                hospitalDetail.value = result
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetAllProviders : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("${ServerUrls.GET_CLIENT_PROVIDERS}/${AppLevelData.clientId}")
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

                val jsonObject = JSONObject(result)
                val jsonArray = jsonObject.getJSONArray("hospitalList")
                val providerList: ArrayList<ProviderModel> = ArrayList()

                for (i in 0 until jsonArray.length()) {

                    val jsonObject = jsonArray.getJSONObject(i)
                    providerList.add(
                        ProviderModel(
                            jsonObject.getInt("hospital_id"),
                            jsonObject.getString("hospital_name"),
                            jsonObject.getString("location_name"),
                            jsonObject.getString("address"),
                            0
                        )
                    )

//                    Log.d("providersdata", "${jsonArray.getJSONObject(i)}")
                }

                AppLevelData.providerList = providerList
                providersData.value = providerList
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetSubscription : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

            val request = Request.Builder()
                .url("${ServerUrls.GET_SUBSCRIPTOIN}/${params[0]!!}")
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
                subscriptionData.value = result

            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetStatesList : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(ServerUrls.GET_STATE_LIST)
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


                val jsonArray = JSONArray(result)
                val serverStateList: ArrayList<StateModel> = ArrayList()

                for (i in 0 until jsonArray.length()) {

                    val jsonObject = jsonArray.getJSONObject(i)
                    serverStateList.add(
                        StateModel(
                            jsonObject.getInt("location_id"),
                            jsonObject.getString("location_name")
                        )
                    )

//                    Log.d("providersdata", "${jsonArray.getJSONObject(i)}")
                }

//                AppLevelData.providerList=providerList
                stateList.value = serverStateList
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetClientHospitalData : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("${ServerUrls.GET_CLIENT_HOSPITAL_DATA}/${params[0]}")
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

                if (result.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "Something went wrong please try again!!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (result.isNotEmpty()) {
                        val jsonArray = JSONArray(result)
                        val jsonObject = jsonArray.getJSONObject(0)
                        val model = ProviderModel(
                            jsonObject.getInt("hospital_id"),
                            jsonObject.getString("hospital_name"),
                            jsonObject.getString("location_name"),
                            jsonObject.getString("address"),
                            jsonObject.getInt("band_id")
                        )
                        clientHospitalData.value = model
                    }


                }

            }
        }
    }


}