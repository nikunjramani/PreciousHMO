package com.lion_tech.hmo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.DashboardClient
import com.lion_tech.hmo.client.activities.fragments.dashboard.DashboardViewModel
import com.lion_tech.hmo.client.activities.fragments.enrolleeProfile.EnrolleeViewModel
import com.lion_tech.hmo.hospital.hospital_dashboard.HospitalDashboard
import com.lion_tech.hmo.server_urls.ServerUrls
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.lang.Exception
import java.util.*


class SplashScreen : AppCompatActivity() {

    private lateinit var sharedPre: SharedPreferences
    private lateinit var message: String
    private lateinit var enrolleeViewModel: EnrolleeViewModel
    private lateinit var dashboardViewModel: DashboardViewModel
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPre = getSharedPreferences("hom_pref", Context.MODE_PRIVATE)


        val ivMiddle: ImageView = findViewById(R.id.ivMiddle);
        val ivTop: ImageView = findViewById(R.id.ivTop);
        val ivRight: ImageView = findViewById(R.id.ivRight);
        val ivLeft: ImageView = findViewById(R.id.ivLeft);
        val ivBottom: ImageView = findViewById(R.id.ivBottom);
        val tvTitle: TextView = findViewById(R.id.tvTitle);

        val aniFade = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in);
        val aniRight = AnimationUtils.loadAnimation(applicationContext, R.anim.from_right);
        val aniLeft = AnimationUtils.loadAnimation(applicationContext, R.anim.from_left);
        val aniTop = AnimationUtils.loadAnimation(applicationContext, R.anim.from_top);
        val aniBottom = AnimationUtils.loadAnimation(applicationContext, R.anim.from_bottom);

        ivMiddle.startAnimation(aniFade);
        ivTop.startAnimation(aniTop);
        ivRight.startAnimation(aniRight);
        ivBottom.startAnimation(aniBottom);
        ivLeft.startAnimation(aniLeft);

        var anim: Animation = AlphaAnimation(0.0f, 1.0f);
        anim.duration = 700 //You can manage the blinking time with this parameter
        anim.startOffset = 20;
        anim.repeatMode = Animation.REVERSE;
        anim.repeatCount = Animation.INFINITE;
        tvTitle.startAnimation(anim);

        var rotate: RotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotate.duration = 10000;
        rotate.repeatCount = Animation.INFINITE;
        ivMiddle.startAnimation(rotate);

        val backgroundThread = Thread(Runnable {

            val lastLoginTime = sharedPre.getString("login_date", "")
            val isClient = sharedPre.getBoolean("isClient", false)
            if (lastLoginTime!!.isNotEmpty()) {

                val currentDate = Calendar.getInstance().timeInMillis
                val lastTime = lastLoginTime.toLong()
                val timeDiff = currentDate - lastTime
                val diffHours = timeDiff / (60 * 60 * 1000)
                Log.d("timeDifference", "$diffHours")
                if (diffHours < 168) {
                    Thread.sleep(4 * 1000)

                    val username = sharedPre.getString("username", "")
                    val password = sharedPre.getString("password", "")

                    if (isClient) {
                        Log.d("timeDifference", "$username  $password   $isClient   $timeDiff")
                        var body = "{ " +
                                "\"username\":\"$username\"," +
                                "\"password\":\"$password\"" +
                                "}"


                        if (!AppLevelData.verifyAvailableNetwork(this)) {
                           runOnUiThread{
                               Toast.makeText(
                                   this@SplashScreen,
                                   getString(R.string.conection_problem),
                                   Toast.LENGTH_SHORT
                               ).show()
                           }
                            Thread.sleep(4 * 1000)
                            startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                            finish()
                        }else {

                            LoginToClientAccount(isClient, password!!).execute(
                                body,
                                ServerUrls.loginUr
                            )
                        }
                    } else {
                        Log.d("timeDifference", "$username  $password   $isClient   $timeDiff")
                        if (!AppLevelData.verifyAvailableNetwork(this)) {
                            runOnUiThread{
                                Toast.makeText(
                                    this@SplashScreen,
                                    getString(R.string.conection_problem),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Thread.sleep(4 * 1000)
                            startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                            finish()
                        }else {
                            var body = "{ " +
                                    "\"username\":\"$username\"," +
                                    "\"password\":\"$password\"" +
                                    "}"

                            LoginToClientAccount(isClient, password!!).execute(
                                body,
                                ServerUrls.HOSPITAL_LOGIN
                            )
                        }
                    }
                } else {
                    Thread.sleep(4 * 1000)
                    startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                    finish()
                }
            } else {
                Thread.sleep(4 * 1000)
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                finish()
            }


        })
        backgroundThread.start()


        enrolleeViewModel =
            ViewModelProvider(this).get(EnrolleeViewModel::class.java)
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        enrolleeViewModel.clientDetail.observe(this, androidx.lifecycle.Observer {
            val jsonObject = JSONObject(it)
            if (jsonObject.getInt("is_active") > 0) {
                AppLevelData.clientDetailJson = jsonObject
                dashboardViewModel.getDashboardCount(this@SplashScreen)
            } else {
                // Go to Login page
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                finish()
            }
        })

        enrolleeViewModel.hospitalDetail.observe(this, androidx.lifecycle.Observer {
            val jsonObject = JSONObject(it)
            AppLevelData.hospitalDetailJson = jsonObject
            dashboardViewModel.getHospitalDashboardCount(this@SplashScreen)
        })

        dashboardViewModel.dashboardCountData.observe(this, androidx.lifecycle.Observer {
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

            startActivity(Intent(this@SplashScreen, DashboardClient::class.java))
            finish()
        })

        dashboardViewModel.hospitalDashboardCountData.observe(this, androidx.lifecycle.Observer {
            val jsonObject = JSONObject(it)
            if (jsonObject.getString("hospitalRequest") != null) {
                AppLevelData.hospitalRequestCount = jsonObject.getString("hospitalRequest").toInt()
                AppLevelData.hospitalComplaintCount =
                    jsonObject.getString("hospitalComplaint").toInt()
                AppLevelData.hospitalClientCount = jsonObject.getString("hospitalClient").toInt()
            }
            startActivity(Intent(this@SplashScreen, HospitalDashboard::class.java))
            finish()
        })

    }


    @SuppressLint("StaticFieldLeak")
    inner class LoginToClientAccount(var isClient: Boolean, var password: String) :
        AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg params: String?): String {
            val requestBody = params[0]!!.toRequestBody(JSON)
            val request: Request = Request.Builder()
                .url(params[1]!!)
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
            Log.d("loginResul", result)
            if (!result.isNullOrEmpty()) {
                try {

                    val jsonObject = JSONObject(result)

                    val responseStatus = jsonObject.getInt("status")
                    message = jsonObject.getString("message")
                    if (responseStatus == 200) {
                        AppLevelData.token = jsonObject.getString("token")
                        AppLevelData.currentPass = password
                        if (isClient) {

                            AppLevelData.clientId = jsonObject.getString("client_id")
                            enrolleeViewModel.getEnrolleeData(this@SplashScreen)

                        } else {
                            AppLevelData.hospitalId = jsonObject.getString("hospital_id")
                            enrolleeViewModel.getHospitalDetail(this@SplashScreen)


                        }
                    }
                } catch (ex: Exception) {
                    Toast.makeText(
                        this@SplashScreen,
                        "Internet connection is very slow",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }
            } else {
                //  Go to Login Page
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                finish()
            }


        }
    }

}
