package com.lion_tech.hmo.hospital.hospital_dashboard.ui.chage_password

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

class HospitalChangePasswordFragViewModel : ViewModel() {

    lateinit var context: Context
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()
    lateinit var progressBar: ProgressBar

    fun changeLoginDetail(context: Context, hospitalPassword: String, progressBar: ProgressBar) {
        this.context = context
        this.progressBar = progressBar

        progressBar.visibility = View.VISIBLE
        var body = "{ " +
                "\"password\":\"${hospitalPassword}\"" +
                "}"
        ChangeLoginDetail(hospitalPassword).execute(body)
    }


    fun logOut(context: Context, progressBar: ProgressBar) {
        this.context = context
        this.progressBar = progressBar

        var body = "{ " +
                "\"nothing\":\"nothing\"" +
                "}"
        LogOut().execute(body)
    }


    @SuppressLint("StaticFieldLeak")
    inner class ChangeLoginDetail(var hospitalPassword: String) :
        AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg params: String?): String {
            val requestBody = params[0]!!.toRequestBody(JSON)
            val request: Request = Request.Builder()
                .url("${ServerUrls.HOSPITAL_PASSWORD}/${AppLevelData.hospitalId}")
                .put(requestBody)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .addHeader("User-ID", AppLevelData.hospitalId)
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
                    progressBar.visibility = View.GONE
                    AppLevelData.currentPass = hospitalPassword
                }
            } else {
                Toast.makeText(
                    context,
                    "Something went wrong. please try again",
                    Toast.LENGTH_SHORT
                )
                    .show()
                progressBar.visibility = View.GONE
            }


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
            progressBar.visibility = View.GONE

            context.startActivity(Intent(context, LoginActivity::class.java))

        }

    }

}