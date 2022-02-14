package com.lion_tech.hmo.hospital.hospital_dashboard.ui.dashboard

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.hospital.hospital_dashboard.HospitalDashboard
import java.text.SimpleDateFormat
import java.util.*

class HospitalDashboardFrag : Fragment() {

    private lateinit var hospitalDashboardFragViewModel: HospitalDashboardFragViewModel
    private lateinit var barChard: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hospitalDashboardFragViewModel =
            ViewModelProvider(this).get(HospitalDashboardFragViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_hospital_dashboard, container, false)
        return root
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        (activity as HospitalDashboard).hideTextView(true,activity!!.getString(R.string.dashboard))





        val cvEnrollee: CardView = root.findViewById(R.id.cvEnrollees)
//        val cvAuthorization: CardView = root.findViewById(R.id.cvAuthoriaztion)
        val cvTotalBills: CardView = root.findViewById(R.id.cvTotalBills)

        val cvEncounter: CardView = root.findViewById(R.id.cvEncounter)
//        val cvTotalAppointment: CardView = root.findViewById(R.id.cvTotalAppointment)
        val cvTotalComplains: CardView = root.findViewById(R.id.cvTotalComplains)

        cvEncounter.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_hospital_request)

        }

        cvTotalBills.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_hospital_bills)

        }


        cvTotalComplains.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_hospital_complains)

        }



        val tvTotalEnrollees: TextView = root.findViewById(R.id.tvTotalEnrollees)
        val tvTotalBills: TextView = root.findViewById(R.id.tvTotalBills)
        val tvTotalEncounters: TextView = root.findViewById(R.id.tvTotalEncounter)
        val tvTotalComplains: TextView = root.findViewById(R.id.tvTotalComplains)



        tvTotalEnrollees.text=AppLevelData.hospitalClientCount.toString()
        tvTotalBills.text=AppLevelData.hospitalRequestCount.toString()
        tvTotalEncounters.text=AppLevelData.hospitalRequestCount.toString()
        tvTotalComplains.text=AppLevelData.hospitalComplaintCount.toString()

        barChard = root.findViewById(R.id.barChart)
        barChard.setDrawBarShadow(false)
        barChard.setDrawValueAboveBar(true)
        barChard.setPinchZoom(false)
        barChard.setMaxVisibleValueCount(50)
        barChard.setDrawBarShadow(true)

setDataToBarChart()
        val displayMetrics: DisplayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics);
//        val screenHeight = displayMetrics.heightPixels;
        val screenWidth = displayMetrics.widthPixels;


        cvEnrollee.layoutParams.width=(screenWidth/2.3).toInt()
//        cvEnrollee.layoutParams.height=(screenWidth/2.5).toInt()

//        cvAuthorization.layoutParams.width=(screenWidth/2.2).toInt()
//        cvAuthorization.layoutParams.height=(screenWidth/2.5).toInt()

        cvTotalBills.layoutParams.width=(screenWidth/2.3).toInt()
//        cvTotalBills.layoutParams.height=(screenWidth/2.5).toInt()

        cvEncounter.layoutParams.width=(screenWidth/2.3).toInt()
//        cvEncounter.layoutParams.height=(screenWidth/2.5).toInt()

//        cvTotalAppointment.layoutParams.width=(screenWidth/2.2).toInt()
//        cvTotalAppointment.layoutParams.height=(screenWidth/2.5).toInt()

        cvTotalComplains.layoutParams.width=(screenWidth/2.3).toInt()
//        cvTotalComplains.layoutParams.height=(screenWidth/2.5).toInt()
    }

    class DatePickerFragment(private var tvDates: TextView, private var formate: String = "") :
        DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(activity as HospitalDashboard, this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {

//            Log.d("My Date",)


            if (formate.isNotEmpty()) {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val date: Date = simpleDateFormat.parse("$year-${month + 1}-$day")
                var dateFormat = SimpleDateFormat(formate, Locale.US)

                tvDates.text=dateFormat.format(date)

            } else {
                tvDates.text = "$year/${month + 1}/$day"

            }
        }
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

        val barDataSet= BarDataSet(barEntry,"Encounters")
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