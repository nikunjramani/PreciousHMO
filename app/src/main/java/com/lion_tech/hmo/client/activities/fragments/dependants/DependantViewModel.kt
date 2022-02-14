package com.lion_tech.hmo.client.activities.fragments.dependants

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.models.DependentModel
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException as IOException1

class DependantViewModel : ViewModel() {
    var dependentList: MutableLiveData<List<DependentModel>> = MutableLiveData()

    var context: Context? = null

    fun getClientDependant(context: Context) {

        this.context = context
        GetClientDependent().execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetClientDependent : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("${ServerUrls.GET_CLIENT_DEPENDANT}/${AppLevelData.clientId}")
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.clientId)
                .addHeader("token", AppLevelData.token)
                .build()

            try {
                val response = client.newCall(request).execute()
                return response.body!!.string()
            } catch (e: IOException1) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result!!)
            Log.d("ClientDependent", result)

            if (result.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Something went wrong please try again!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val jsonArray = JSONArray(result)
                var serverDependentList:ArrayList<DependentModel> = ArrayList()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    serverDependentList.add(
                        DependentModel(
                            jsonObject.getInt("client_id"),
                            jsonObject.getString("dep_is"),
                            jsonObject.getString("name"),
                            jsonObject.getString("last_name"),
                            jsonObject.getString("relation"),
                            jsonObject.getString("other_name"),
                            jsonObject.getString("client_profile"),
                            jsonObject.getInt("hospital_id"),
                            jsonObject.getString("hospital_name"),
                            jsonObject.getString("location_name"),
                            jsonObject.getString("plan_name"),
                            jsonObject.getInt("dependent_id"),
                            jsonObject.getString("dob")
                    ))
                    Log.d("jsonName",jsonObject.getString("name"))
                }

                dependentList.value=serverDependentList
            }

//            if (result.isNullOrEmpty()) {

//            } else {
////                subscriptionData.value = result
//
//            }
        }
    }
}