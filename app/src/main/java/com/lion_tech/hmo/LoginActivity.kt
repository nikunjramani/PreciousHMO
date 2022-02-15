package com.lion_tech.hmo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.DashboardClient
import com.lion_tech.hmo.client.activities.fragments.dashboard.DashboardViewModel
import com.lion_tech.hmo.client.activities.fragments.enrolleeProfile.EnrolleeViewModel
import com.lion_tech.hmo.hospital.hospital_dashboard.HospitalDashboard
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*


class LoginActivity : AppCompatActivity() {

    private var SEND_PERMISSION_CODE: Int = 101;

    private lateinit var message: String
    private lateinit var enrolleeViewModel: EnrolleeViewModel
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var etUserName: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnForgotPassword: TextView

    private lateinit var radioGroup: RadioGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPref:SharedPreferences
    private lateinit var prefEditor:SharedPreferences.Editor

    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        checkManifestPermission()
        sharedPref=getSharedPreferences("hom_pref",Context.MODE_PRIVATE)
        prefEditor=sharedPref.edit()

        enrolleeViewModel =
            ViewModelProvider(this).get(EnrolleeViewModel::class.java)
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        etUserName = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        radioGroup = findViewById(R.id.rg)
        progressBar = findViewById(R.id.progressBar)
        btnForgotPassword = findViewById(R.id.tvForgotPassword)

        btnForgotPassword.setOnClickListener {
            startActivity(Intent(this,ResetPasswordActivity::class.java))
        }
        val ivLogo: ImageView = findViewById(R.id.ivLogo)

        var rotate: RotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotate.duration = 10000;
        rotate.repeatCount = Animation.INFINITE;
        ivLogo.startAnimation(rotate);

//        etUserName.setText("test@liontech.com.ng")
//        etUserName.setText("kennedyjob@yeffgs.ng")
//        etUserName.setText("kennedyjob@myweb.ng")
//        etUserName.setText("limih@gmail.com")
//        etUserName.setText("test@liontech.com.ng")
//        etUserName.setText("john@john.com")
//        etUserName.setText("kennedyjob@yahoo.com")




        enrolleeViewModel.clientDetail.observe(this, Observer {
            val jsonObject = JSONObject(it)
            if (jsonObject.getInt("is_active") > 0) {
                AppLevelData.clientDetailJson = jsonObject
                dashboardViewModel.getDashboardCount(this@LoginActivity)
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.your_account_is_not_active),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        enrolleeViewModel.hospitalDetail.observe(this, Observer {
            val jsonObject = JSONObject(it)
            AppLevelData.hospitalDetailJson = jsonObject

            dashboardViewModel.getHospitalDashboardCount(this@LoginActivity)
        })


        dashboardViewModel.dashboardCountData.observe(this, Observer {
            val jsonObject = JSONObject(it)
            if (jsonObject.getString("hospitalCount") != null) {
                AppLevelData.clientHospitalCount = jsonObject.getString("hospitalCount").toInt()
                AppLevelData.clientDependentCount = jsonObject.getString("dependentCount").toInt()
                AppLevelData.clientProviderChangeRequestCount =
                    jsonObject.getString("changeHospitalRequest").toInt()
                AppLevelData.clientComplaintCount = jsonObject.getString("complaintCount").toInt()
                AppLevelData.clientSubscriptionCount =
                    jsonObject.getString("subscriptionCount").toInt()
            }

            prefEditor.putString("username",etUserName.text.toString())
            prefEditor.putString("password",etPassword.text.toString())
            prefEditor.putBoolean("isClient",true)
            prefEditor.putString("login_date","${Calendar.getInstance().timeInMillis}")
            prefEditor.apply()

            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@LoginActivity, DashboardClient::class.java))
            Log.d("client_detail", jsonObject.toString())
            progressBar.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
        })

        dashboardViewModel.hospitalDashboardCountData.observe(this, Observer {
            val jsonObject = JSONObject(it)
            if (jsonObject.getString("hospitalRequest") != null) {
                AppLevelData.hospitalRequestCount = jsonObject.getString("hospitalRequest").toInt()
                AppLevelData.hospitalComplaintCount =
                    jsonObject.getString("hospitalComplaint").toInt()
                AppLevelData.hospitalClientCount = jsonObject.getString("hospitalClient").toInt()
            }

            prefEditor.putString("username",etUserName.text.toString())
            prefEditor.putString("password",etPassword.text.toString())
            prefEditor.putBoolean("isClient",false)
            prefEditor.putString("login_date","${Calendar.getInstance().timeInMillis}")
            prefEditor.apply()

            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@LoginActivity, HospitalDashboard::class.java))
            progressBar.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
        })



        btnLogin.setOnClickListener {

            if (!AppLevelData.verifyAvailableNetwork(this)) {
                Toast.makeText(this, getString(R.string.conection_problem), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (etUserName.text.toString().isEmpty()) {
                etUserName.error = getString(R.string.username_req)
                etUserName.requestFocus()
                return@setOnClickListener
            } else if (etPassword.text.toString().isEmpty()) {
                etPassword.error = getString(R.string.password_req)
                etPassword.requestFocus()
                return@setOnClickListener
            }


            if (radioGroup.checkedRadioButtonId == R.id.rdClient) {
                //--------- Login to client account --------//
                var body = "{ " +
                        "\"username\":\"${etUserName.text}\"," +
                        "\"password\":\"${etPassword.text}\"" +
                        "}"

                LoginToClientAccount().execute(
                    body,
                    ServerUrls.loginUr
                )

            } else {
                //-------- Login to hospital account -------//
                //--------- Login to client account --------//
                var body = "{ " +
                        "\"username\":\"${etUserName.text}\"," +
                        "\"password\":\"${etPassword.text}\"" +
                        "}"

                LoginToClientAccount().execute(
                    body,
                    ServerUrls.HOSPITAL_LOGIN
                )


            }

        }


    }

    private fun checkManifestPermission() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED

            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED

        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

                ),
                SEND_PERMISSION_CODE
            );
        }
    }


    @SuppressLint("StaticFieldLeak")
    inner class LoginToClientAccount : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
            btnLogin.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String {
            val requestBody = params[0]!!.toRequestBody(JSON)
            val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("username",etUserName.text.toString())
                .addFormDataPart("password", etPassword.text.toString())
                .build()
            val request: Request = Request.Builder()
                .url(params[1]!!)
                .method("POST", body)
                .addHeader(AppLevelData.authKey, AppLevelData.authKeyValue)
                .addHeader(AppLevelData.clientServiceKey, AppLevelData.clientServiceValue)
                .addHeader(AppLevelData.contentTypeKey, AppLevelData.contentTypeValue)
                .build()
            Log.d("TAGGG", "doInBackground: "+ request)
                client.newCall(request).execute()
                    .use { response -> return response.body!!.string() }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d("loginResul", result)
            if (!result.isNullOrEmpty()) {
               try {

                   val jsonObject = JSONObject(result)

                   val responseStatus = jsonObject.getInt("status")
                   message = jsonObject.getString("message")
                   if (responseStatus == 200) {
                       AppLevelData.token = jsonObject.getString("token")
                       AppLevelData.currentPass = etPassword.text.toString()
                       if (radioGroup.checkedRadioButtonId == R.id.rdClient) {
                           AppLevelData.clientId = jsonObject.getString("client_id")
                           enrolleeViewModel.getEnrolleeData(this@LoginActivity)

                       } else {
                           AppLevelData.hospitalId = jsonObject.getString("hospital_id")
                           enrolleeViewModel.getHospitalDetail(this@LoginActivity)
                       }
                       AppLevelData.currentPass = etPassword.text.toString()
                   }else{
                       Toast.makeText(
                           this@LoginActivity,
                           jsonObject.getString("message"),
                           Toast.LENGTH_SHORT
                       ).show()
                   }
               }catch (ex:Exception){
                   Toast.makeText(
                       this@LoginActivity,
                       "Internet connection is very slow",
                       Toast.LENGTH_SHORT
                   )
                       .show()
                   progressBar.visibility = View.GONE
                   btnLogin.visibility = View.VISIBLE
               }
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Wrong username or password",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            progressBar.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE


        }
    }
}
