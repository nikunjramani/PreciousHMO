package com.lion_tech.hmo.client.activities.fragments.complains

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.models.ComplaintModel
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ComplainsViewModel : ViewModel() {
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()
    lateinit var context:Context
    private val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US) as DateFormat;
    private val inputFormat: DateFormat =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    var complainDataList: MutableLiveData<List<ComplaintModel>> = MutableLiveData()



    fun addClientTicket(subject: String, detail: String) {

        val body = "{ " +
                "\"support\":\"example update\"," +
                "\"subject_\":\"$subject\"," +
                "\"description\":\"$detail\"" +
                "}"

        AddNewComplaint().execute(
            body
        )
    }

    fun getClientComplains(context: Context) {
        this.context=context
        GetAllClientComplain().execute()
    }


    @SuppressLint("StaticFieldLeak")
    private inner class AddNewComplaint : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {
            val requestBody = params[0]!!.toRequestBody(JSON)
            val request: Request = Request.Builder()
                .url("${ServerUrls.ADD_CLIENT_COMPLAINT}/${AppLevelData.clientId}")
                .post(requestBody)
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
            if (!result.isNullOrEmpty()) {

                Log.d("newTicked", result)
                Toast.makeText(context, "Uploaded successfully", Toast.LENGTH_SHORT)
                    .show()
                GetAllClientComplain().execute()

            } else {
                Toast.makeText(context, "Wrong username or password", Toast.LENGTH_SHORT)
                    .show()

            }


        }

    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetAllClientComplain : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("${ServerUrls.GET_CLIENT_COMPLAINT}/${AppLevelData.clientId}")
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

          val serverComplaintList:ArrayList<ComplaintModel> = ArrayList()
            if (!result.isNullOrEmpty()) {

                val jsonArray:JSONArray = JSONArray(result)
                for (i in 0 until jsonArray.length()){
                    val jsonObject=jsonArray.getJSONObject(i)

                    val inputText = jsonObject.getString("created_at")
                    serverComplaintList.add(
                        ComplaintModel(
                            jsonObject.getInt("ticket_id"),
                            jsonObject.getString("subject"),
                            jsonObject.getString("description"),
                            jsonObject.getString("ticket_code"),
                            outputFormat.format(inputFormat.parse(inputText)!!),
                            jsonObject.getString("ticket_status")
                        ))
                }
                complainDataList.value=serverComplaintList
            }
        }
    }
}

