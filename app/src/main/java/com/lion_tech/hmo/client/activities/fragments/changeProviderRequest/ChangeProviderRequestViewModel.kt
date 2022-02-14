package com.lion_tech.hmo.client.activities.fragments.changeProviderRequest

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.models.ProviderModel
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception

class ChangeProviderRequestViewModel : ViewModel() {
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()
    lateinit var context: Context
    val selectedProvider: MutableLiveData<ProviderModel> = MutableLiveData()
    val isProviderChanged:MutableLiveData<Boolean> = MutableLiveData( false)

    fun changeClientProvider(hospital_id: Int, providerModel: ProviderModel,reason:String,context: Context) {
        this.context=context
        var body = "{ " +
                "\"client_id\":\"${AppLevelData.clientId}\"," +
                "\"hospital_id\":\"$hospital_id\"," +
                "\"reason\":\"$reason\"" +
                "}"
        ChangeClientProvider(providerModel).execute(body)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ChangeClientProvider(var providerModel: ProviderModel) :
        AsyncTask<String, Void, String>() {


        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {
            val requestBody = params[0]!!.toRequestBody(JSON)
            val request: Request = Request.Builder()
                .url(ServerUrls.CHANGE_CLIENT_PROVIDER)
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
            try {



            if (!result.isNullOrEmpty()) {
                selectedProvider.value = providerModel
                isProviderChanged.value=true
                Toast.makeText(context,"Request successful", Toast.LENGTH_SHORT).show()
            }
            } catch (ex: Exception) {

            }

        }

    }
}