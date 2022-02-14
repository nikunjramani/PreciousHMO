package com.lion_tech.hmo.client.activities.fragments.changeLoginDetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.lion_tech.hmo.LoginActivity
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ChangeLoginDetailViewModel : ViewModel() {

    lateinit var context: Context
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()
    lateinit var progressBar: ProgressBar

    fun changeLoginDetail(context: Context, clientPassword: String,progressBar: ProgressBar) {


        this.context = context
        this.progressBar=progressBar
        progressBar.visibility=View.VISIBLE
        var body = "{ " +
                "\"client_password\":\"${clientPassword}\"" +
                "}"
        ChangeLoginDetail(clientPassword).execute(body)
    }


    fun logOut(context: Context,progressBar: ProgressBar) {


        this.progressBar=progressBar
        this.context = context
        var body = "{ " +
                "\"nothing\":\"nothing\"" +
                "}"
        LogOut().execute(body)
    }


    @SuppressLint("StaticFieldLeak")
    inner class ChangeLoginDetail(var clientPassword: String) : AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg params: String?): String {
            val requestBody = params[0]!!.toRequestBody(JSON)
            val request: Request = Request.Builder()
                .url("${ServerUrls.CLIENT_PASSWORD}/${AppLevelData.clientId}")
                .put(requestBody)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.clientId)
                .addHeader("token", AppLevelData.token)
                .build()
            client.newCall(request).execute().use { response -> return response.body!!.string() }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            Log.d("ChangeResult", result)
            if (!result.isNullOrEmpty()) {

                val jsonObject = JSONObject(result)
                val responseStatus = jsonObject.getInt("status")
                if (responseStatus == 200) {
                    Toast.makeText(context, "Password update successful", Toast.LENGTH_SHORT)
                        .show()
                    AppLevelData.currentPass=clientPassword
                    progressBar.visibility = View.GONE
                }
            } else {
                Toast.makeText(context, "Something went wron please try again", Toast.LENGTH_SHORT)
                    .show()
            }
            progressBar.visibility = View.GONE


        }
    }


        @SuppressLint("StaticFieldLeak")
        inner class LogOut : AsyncTask<String, Void, String>() {

            override fun doInBackground(vararg params: String?): String {
                val requestBody = params[0]!!.toRequestBody(JSON)
                val request: Request = Request.Builder()
                    .url(ServerUrls.LOG_OUT)
                    .post(requestBody)
                    .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                    .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                    .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                    .build()
                client.newCall(request).execute()
                    .use { response -> return response.body!!.string() }
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)

                Log.d("LogOutResult", result)
                progressBar.visibility=View.GONE

                context.startActivity(Intent(context,LoginActivity::class.java))

            }

        }


}


