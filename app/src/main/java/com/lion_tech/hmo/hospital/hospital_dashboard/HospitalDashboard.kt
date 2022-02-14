package com.lion_tech.hmo.hospital.hospital_dashboard

import android.os.Bundle
import android.view.Gravity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.hospital.hospital_dashboard.ui.chage_password.HospitalChangePasswordFragViewModel
import com.lion_tech.hmo.hospital.hospital_dashboard.ui.dashboard.HospitalDashboardFrag
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_dashboard_client.*
import java.text.SimpleDateFormat
import java.util.*

class HospitalDashboard : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var ivHamburger: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvClientName: TextView
    private lateinit var tvWelcome: TextView
    private lateinit var tvAdd: TextView
    private lateinit var tvLogOut: TextView
    private lateinit var tvPdf: TextView
    private lateinit var tvAmountTitle: TextView
    private lateinit var tvAmount: TextView
    private lateinit var ivEnrolleeProfile: ImageView
    private lateinit var ivDialogProfileImage: ImageView
    private lateinit var ivHeader: ImageView
    private lateinit var tvHeaderName: TextView
    private lateinit var tvHeaderEmail: TextView
    lateinit var searchView: SearchView
    private lateinit var cvSearch: CardView
    private lateinit var cvFilterBills: CardView

    private lateinit var changeHospitalLoginDetail: HospitalChangePasswordFragViewModel
    private val calendar: Calendar = Calendar.getInstance()

    private lateinit var cvDateFilterComplain: CardView
    private lateinit var tvDateFilterForComplaint: TextView
    private lateinit var tvDateFilterBillFrom: TextView
    private lateinit var tvDateFilterBillTo: TextView
    private lateinit var ivSearchComplaint: ImageView
    private lateinit var ivFilterBills: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.hide()

        changeHospitalLoginDetail =
            ViewModelProvider(this).get(HospitalChangePasswordFragViewModel::class.java)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        ivHamburger = findViewById(R.id.ivHamburger)
        tvClientName = findViewById(R.id.tvClientName)
        tvTitle = findViewById(R.id.tvTitle)
        tvDate = findViewById(R.id.tvDate)
        tvWelcome = findViewById(R.id.tvWelcome)
        tvAdd = findViewById(R.id.tvAddDependant)
        tvLogOut = findViewById(R.id.tvLogout)
        ivEnrolleeProfile = findViewById(R.id.ivProfile)
        searchView = findViewById(R.id.searchView)
        cvSearch = findViewById(R.id.cvSearch)
        cvFilterBills = findViewById(R.id.cvFilterBills)
        cvDateFilterComplain = findViewById(R.id.cvDate)
        tvDateFilterForComplaint = findViewById(R.id.tvDateFilter)
        ivSearchComplaint = findViewById(R.id.tvSearchComplaint)
        tvDateFilterBillFrom = findViewById(R.id.tvDateFilterBillFrom)
        tvDateFilterBillTo = findViewById(R.id.tvDateFilterBillTo)
        ivFilterBills = findViewById(R.id.ivFilterBills)
        progressBar = findViewById(R.id.progressBar)
        tvPdf = findViewById(R.id.tvPdf)
        tvAmount = findViewById(R.id.tvAmount)
        tvAmountTitle = findViewById(R.id.tvAmountTitle)

        var dateFormat = SimpleDateFormat("EEEE", Locale.US)
        val date = Date()
        var dateString = "${dateFormat.format(date)}, ${calendar.get(Calendar.DAY_OF_MONTH)}"

        dateFormat = SimpleDateFormat("MMMM", Locale.US)

        dateString = "$dateString ${dateFormat.format(date)} ${calendar.get(Calendar.YEAR)}"
        tvDate.text = dateString

        dateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
        tvDateFilterForComplaint.text = dateFormat.format(date)

        dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

        tvDateFilterBillFrom.text = dateFormat.format(date)
        tvDateFilterBillTo.text = dateFormat.format(date)


        ivHamburger.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        tvLogOut.setOnClickListener {
            changeHospitalLoginDetail.logOut(this, progressBar)
        }

        tvAdd.setOnClickListener {
            if (AppLevelData.isHospitalComplaint) {
                AppLevelData.hospitalComplaintObject!!.showComplaintDialog()

            } else {
                AppLevelData.hospitalRequestFrag!!.showDialog()
            }
        }

        ivSearchComplaint.setOnClickListener {
            AppLevelData.hospitalComplaintObject!!.filterComplain(tvDateFilterForComplaint.text.toString())
        }

        tvDateFilterForComplaint.setOnClickListener {
            val newFragment =
                HospitalDashboardFrag.DatePickerFragment(tvDateFilterForComplaint, "MMM yyyy")
            newFragment.show(supportFragmentManager, "datePicker")
        }

        tvDateFilterBillTo.setOnClickListener {
            val newFragment =
                HospitalDashboardFrag.DatePickerFragment(tvDateFilterBillTo, "dd MMM yyyy")
            newFragment.show(supportFragmentManager, "datePicker")
        }

        tvDateFilterBillFrom.setOnClickListener {
            val newFragment =
                HospitalDashboardFrag.DatePickerFragment(tvDateFilterBillFrom, "dd MMM yyyy")
            newFragment.show(supportFragmentManager, "datePicker")
        }

        ivFilterBills.setOnClickListener {
            if (AppLevelData.isPaidBill) {
                AppLevelData.hospitalPaidBillFrag!!.filterBills(
                    tvDateFilterBillFrom.text.toString(),
                    tvDateFilterBillTo.text.toString()
                )
            } else {
                AppLevelData.hospitalBillFrag!!.filterBills(
                    tvDateFilterBillFrom.text.toString(),
                    tvDateFilterBillTo.text.toString()
                )
            }
        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_hospital_request,
                R.id.nav_hospital_dashboard,
                R.id.nav_hospital_change_password,
                R.id.nav_hospital_bills,
                R.id.hospitalPaidBill,
                R.id.nav_hospital_complains
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val view = navView.getHeaderView(0)
        ivHeader = view.findViewById(R.id.iv_Header)
        tvHeaderName = view.findViewById(R.id.tvHeaderHospitaltName)
        tvHeaderEmail = view.findViewById(R.id.tvHeaderHospitalEmail)

        val hospitalJson = AppLevelData.hospitalDetailJson
        if (hospitalJson != null) {
            tvClientName.text = hospitalJson.getString("hospital_name")
            tvHeaderName.text = hospitalJson.getString("hospital_name")
            tvHeaderEmail.text = hospitalJson.getString("email")

            Picasso.get()
                .load("https://care.precioushmo.com/app/uploads/clients/${hospitalJson.getString("logo_img")}")
                .into(ivHeader)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.hospital_dashboard, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    fun hideTextView(show: Boolean, title: String) {
        if (show) {
            tvClientName.visibility = View.VISIBLE
            tvTitle.visibility = View.VISIBLE
            tvDate.visibility = View.VISIBLE
            tvWelcome.visibility = View.VISIBLE
            tvPdf.visibility = View.GONE
            tvAmount.visibility = View.GONE
            tvAmountTitle.visibility = View.GONE
            tvTitle.text = title

            ivEnrolleeProfile.visibility = View.GONE

            if (title == getString(R.string.my_complains) || title == getString(R.string.request)) {
                tvAdd.visibility = View.VISIBLE
                tvAdd.text = ""
                tvAdd.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getDrawable(R.drawable.ic_add),
                    null,
                    null
                )

            } else if (title == getString(R.string.my_dependant)) {
                tvAdd.visibility = View.VISIBLE
                tvAdd.text = getString(R.string.add_dependant)
                tvAdd.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getDrawable(R.drawable.ic_dependants),
                    null,
                    null
                )
            } else {
                tvAdd.visibility = View.GONE

            }

            if (title == getString(R.string.request)) {
                cvSearch.visibility = View.VISIBLE
            } else {
                cvSearch.visibility = View.GONE
            }

            if (title == getString(R.string.my_complains)) {
                cvDateFilterComplain.visibility = View.VISIBLE
            } else {
                cvDateFilterComplain.visibility = View.GONE
            }

            if (title == getString(R.string.approved_bills) || title == getString(R.string.paid_bills)) {
                cvFilterBills.visibility = View.VISIBLE
                if (title == getString(R.string.paid_bills)) {
                    tvPdf.visibility = View.VISIBLE
                    tvAmount.visibility = View.VISIBLE
                    tvAmountTitle.visibility = View.VISIBLE
                }
            } else {
                cvFilterBills.visibility = View.GONE
            }

        } else {
            cvSearch.visibility = View.GONE
            cvFilterBills.visibility = View.GONE
            cvSearch.visibility = View.GONE
            tvClientName.visibility = View.GONE
            tvTitle.visibility = View.GONE
            tvDate.visibility = View.GONE
            tvWelcome.visibility = View.GONE
            tvPdf.visibility = View.GONE
            tvAmount.visibility = View.GONE
            tvAmountTitle.visibility = View.GONE
            ivEnrolleeProfile.visibility = View.VISIBLE
            tvAdd.visibility = View.GONE

        }
    }

    fun hideAddDependantButton() {
        tvAddDependant.visibility = View.GONE
    }
}
