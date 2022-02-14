package com.lion_tech.hmo.app_data

import android.content.Context
import android.net.ConnectivityManager
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.lion_tech.hmo.client.activities.adpaters.autocompleteAdapters.ProvidersAutoCompleteAdapter
import com.lion_tech.hmo.client.activities.fragments.complains.Complains
import com.lion_tech.hmo.client.activities.fragments.dependants.DependantViewModel
import com.lion_tech.hmo.client.activities.models.ProviderModel
import com.lion_tech.hmo.client.activities.models.SubscriptionModel
import com.lion_tech.hmo.hospital.hospital_dashboard.models.DrugsModel
import com.lion_tech.hmo.hospital.hospital_dashboard.models.ServicessModel
import com.lion_tech.hmo.hospital.hospital_dashboard.ui.bills.HospitalBillsFrag
import com.lion_tech.hmo.hospital.hospital_dashboard.ui.hospital_compains.HospitalComplainsFrag
import com.lion_tech.hmo.hospital.hospital_dashboard.ui.paid_bills.HospitalPaidBill
import com.lion_tech.hmo.hospital.hospital_dashboard.ui.request.HospitalRequestFrag
import org.json.JSONObject
import java.util.ArrayList

class AppLevelData {

    companion object {

         var hospitalBillFrag: HospitalBillsFrag? =null
         var hospitalPaidBillFrag: HospitalPaidBill? =null
        var  isPaidBill=false


        var clientDiagnoseServicess: ArrayList<ServicessModel> = ArrayList()
         var clientDiagnoseDrugs: ArrayList<DrugsModel> = ArrayList()
        var serverDrugList: ArrayList<DrugsModel> = ArrayList()
        var serverServiceList: ArrayList<ServicessModel> = ArrayList()
        var selectedDrugList: ArrayList<DrugsModel> = ArrayList()
        var selectedServiceList: ArrayList<ServicessModel> = ArrayList()
        var serviceTotalPrice: Float = 0f
        var drugTotalPrice: Float = 0f
        lateinit var clientCurrentSubscription: SubscriptionModel
        var isHospitalComplaint: Boolean = true

        var dependentDialog: AlertDialog? = null
        var dependentProgressBar: ProgressBar? = null
        var currentPass: String = ""
        lateinit var dependentViewModel: DependantViewModel
        const val clientServiceKey = "Client-Service"
        const val clientServiceValue = "frontend-client"
        const val authKey = "Auth-Key"
        const val authKeyValue = "hmorestapi"
        const val contentTypeKey = "Content-Type"
        const val contentTypeValue = "application/json"

        var hospitalId: String = ""
        var clientId = ""
        var token = ""

        var providerSpinnerAdapter: ProvidersAutoCompleteAdapter? = null
        var complaintFragmentObject: Complains? = null
        var hospitalComplaintObject: HospitalComplainsFrag? = null
        var hospitalRequestFrag: HospitalRequestFrag? = null

        var providerList: List<ProviderModel>? = null
        var clientDetailJson: JSONObject? = null
        var hospitalDetailJson: JSONObject? = null


        var clientHospitalCount: Int = 0
        var clientDependentCount: Int = 0
        var clientProviderChangeRequestCount: Int = 0
        var clientSubscriptionCount: Int = 0
        var clientComplaintCount: Int = 0


        var hospitalClientCount: Int = 0
        var hospitalComplaintCount: Int = 0
        var hospitalRequestCount: Int = 0


        fun verifyAvailableNetwork(activity: AppCompatActivity):Boolean{
            val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo=connectivityManager.activeNetworkInfo
            return  networkInfo!=null && networkInfo.isConnected
        }

    }

}