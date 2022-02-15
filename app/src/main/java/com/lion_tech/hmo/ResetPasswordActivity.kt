package com.lion_tech.hmo

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.server_urls.ServerUrls
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.lang.Exception

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var etUserName: TextInputEditText
    private lateinit var btnResetPassword: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var message: String

    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        etUserName = findViewById(R.id.etUsername)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        progressBar = findViewById(R.id.progressBar)

        supportActionBar?.hide()
        val ivLogo: ImageView = findViewById(R.id.ivLogo)

        var rotate: RotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotate.duration = 10000;
        rotate.repeatCount = Animation.INFINITE;
        ivLogo.startAnimation(rotate);
        btnResetPassword.setOnClickListener {
            if (!AppLevelData.verifyAvailableNetwork(this)) {
                Toast.makeText(this, getString(R.string.conection_problem), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (etUserName.text.toString().isEmpty()) {
                etUserName.error = getString(R.string.username_req)
                etUserName.requestFocus()
                return@setOnClickListener
            }
            var body = "{ " +
                    "\"email\":\"${etUserName.text}\"," +
                    "}"

            ResetPassword().execute(
                body,
                ServerUrls.resetPasswordUrl
            )
        }
    }
    @SuppressLint("StaticFieldLeak")
    inner class ResetPassword : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
            btnResetPassword.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String {
            val requestBody = params[0]!!.toRequestBody(JSON)
            val request: Request = Request.Builder()
                .url(params[1]!!)
                .post(requestBody)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .build()
            Log.d("loginResul", request.toString())

            client.newCall(request).execute()
                .use { response -> return response.body!!.string() }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d("loginResul", result)
//                try {

//                    val jsonObject = JSONObject(result)
//
//                    val responseStatus = jsonObject.getInt("status")
//                    message = jsonObject.getString("message")
//                    Log.d("loginResul", message)
//
//                    if (responseStatus == 200) {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Please check your mail",
                            Toast.LENGTH_SHORT
                        )
                            .show()
//                    }
//                }catch (ex: Exception){
//                    Toast.makeText(
//                        this@ResetPasswordActivity,
//                        "Internet connection is very slow",
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
                    progressBar.visibility = View.GONE
                    btnResetPassword.visibility = View.VISIBLE
//                }


        }
    }
}