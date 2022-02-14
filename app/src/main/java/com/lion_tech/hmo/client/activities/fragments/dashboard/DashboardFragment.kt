package com.lion_tech.hmo.client.activities.fragments.dashboard

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.DashboardClient

class DashboardFragment : Fragment() {

    private lateinit var barChard: BarChart
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_client_dashboard, container, false)

        (activity as DashboardClient).hideTextView(true, activity!!.getString(R.string.dashboard))


        return root
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)


        val cvDependent: CardView = root.findViewById(R.id.cvDependent)
        val cvProviders: CardView = root.findViewById(R.id.cvProviders)
        val cvDependentRequest: CardView = root.findViewById(R.id.cvDependentRequest)
        val cvEncounter: CardView = root.findViewById(R.id.cvEncounter)
        val cvChangeProviderRequest: CardView = root.findViewById(R.id.cvChangeProviderRequest)
        val cvTotalComplains: CardView = root.findViewById(R.id.cvTotalComplains)

        val tvTotalDependent: TextView = root.findViewById(R.id.tvTotalDependants)
        val tvTotalComplains: TextView = root.findViewById(R.id.tvTotalComplains)
        val tvTotalDependentRequest: TextView = root.findViewById(R.id.tvTotalDependentRequest)
        val tvTotalChangeProviderRequest: TextView = root.findViewById(R. id.tvTotalChangeProviderRequest)
        val tvTotalProviders: TextView = root.findViewById(R.id.tvTotalProviders)

        tvTotalDependent.text=AppLevelData.clientDependentCount.toString()
        tvTotalComplains.text=AppLevelData.clientComplaintCount.toString()
        tvTotalDependentRequest.text=AppLevelData.clientSubscriptionCount.toString()
        tvTotalChangeProviderRequest.text=AppLevelData.clientProviderChangeRequestCount.toString()
        tvTotalProviders.text=AppLevelData.clientHospitalCount.toString()


        barChard = root.findViewById(R.id.barChart)
        barChard.setDrawBarShadow(false)
        barChard.setDrawValueAboveBar(true)
        barChard.setPinchZoom(false)
        barChard.setMaxVisibleValueCount(50)
        barChard.setDrawBarShadow(true)

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics);
        val screenHeight = displayMetrics.heightPixels;
        val screenWidth = displayMetrics.widthPixels;

        setDataToBarChart()


        cvDependent.layoutParams.width = (screenWidth / 2.3).toInt()
//        cvDependent.layoutParams.height = (screenWidth / 2.5).toInt()

        cvProviders.layoutParams.width = (screenWidth / 2.3).toInt()
//        cvProviders.layoutParams.height = (screenWidth / 2.5).toInt()

        cvDependentRequest.layoutParams.width = (screenWidth / 2.3).toInt()
//        cvDependentRequest.layoutParams.height = (screenWidth / 2.5).toInt()

        cvEncounter.layoutParams.width = (screenWidth / 2.3).toInt()
//        cvEncounter.layoutParams.height = (screenWidth / 2.5).toInt()

        cvChangeProviderRequest.layoutParams.width = (screenWidth / 2.3).toInt()
//        cvChangeProviderRequest.layoutParams.height = (screenWidth / 2.5).toInt()

        cvTotalComplains.layoutParams.width = (screenWidth / 2.3).toInt()
//        cvTotalComplains.layoutParams.height = (screenWidth / 2.5).toInt()

        cvDependent.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_dependants)

        }


        cvProviders.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_change_provider_request)

        }

        cvDependentRequest.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.subscriptionFragment)

        }

        cvEncounter.setOnClickListener {
        }

        cvChangeProviderRequest.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_change_provider_request)

        }

        cvTotalComplains.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_complains)

        }

//        Navigation.findNavController(it).navigate(R.id.ambulanceServices)
    }

    private fun setDataToBarChart() {

   val barEntry:ArrayList<BarEntry> = ArrayList()

        barEntry.add(BarEntry(1f,40f))
        barEntry.add(BarEntry(2f,20f))
        barEntry.add(BarEntry(3f,30f))
        barEntry.add(BarEntry(4f,25f))
        barEntry.add(BarEntry(5f,50f))
        barEntry.add(BarEntry(6f,30f))
        barEntry.add(BarEntry(7f,35f))
        barEntry.add(BarEntry(8f,40f))
        barEntry.add(BarEntry(9f,50f))

        val barDataSet= BarDataSet(barEntry,"Enrollee")
        barDataSet.setColors(
            activity!!.resources.getColor(R.color.colorOne),
            activity!!.resources.getColor(R.color.colorTwo),
            activity!!.resources.getColor(R.color.colorThree),
            activity!!.resources.getColor(R.color.colorFour),
            activity!!.resources.getColor(R.color.colorFive),
            activity!!.resources.getColor(R.color.colorSix),
            activity!!.resources.getColor(R.color.colorSeven),
            activity!!.resources.getColor(R.color.colorEight),
            activity!!.resources.getColor(R.color.colorNine)
        )

        val barData= BarData(barDataSet)

        barChard.data=barData


    }
}