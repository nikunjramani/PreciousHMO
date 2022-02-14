package com.lion_tech.hmo.client.activities.fragments.subscription

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.models.SubscriptionModel
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class SubscriptionViewModel : ViewModel() {

    var subscriptionList: MutableLiveData<List<SubscriptionModel>> = MutableLiveData()
    lateinit var context: Context


    fun getSubscriptionList(context: Context,subId:String) {
        this.context = context


        GetAllSubscription().execute(subId)
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetAllSubscription : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("${ServerUrls.GET_CLIENT_SUBSCRIPTION_LIST}/${params[0]}")
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
                val responseJson=JSONObject(result)

                val jsonArray = responseJson.getJSONArray("subsList")
                val clientSubs = responseJson.getJSONArray("subsClient")
                val clientSubsObject=clientSubs.getJSONObject(0)
              AppLevelData.clientCurrentSubscription=  SubscriptionModel(
                    clientSubsObject.getInt("subscription_id"),
                    clientSubsObject.getString("plan_name"),
                    clientSubsObject.getString("plan_cost")
                )

                val serverList: ArrayList<SubscriptionModel> = ArrayList()

                for (i in 0 until jsonArray.length()) {

                    val jsonObject = jsonArray.getJSONObject(i)
                    serverList.add(
                        SubscriptionModel(
                            jsonObject.getInt("subscription_id"),
                            jsonObject.getString("plan_name"),
                            jsonObject.getString("plan_cost")
                        )
                    )
                }

                subscriptionList.value = serverList
            }
        }
    }
}
