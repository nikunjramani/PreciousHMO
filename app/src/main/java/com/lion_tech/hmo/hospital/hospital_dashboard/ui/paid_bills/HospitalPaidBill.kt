package com.lion_tech.hmo.hospital.hospital_dashboard.ui.paid_bills

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.lion_tech.hmo.BuildConfig
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.hospital.hospital_dashboard.HospitalDashboard
import com.lion_tech.hmo.hospital.hospital_dashboard.adapters.recycler_view_adapter.*
import com.lion_tech.hmo.hospital.hospital_dashboard.models.DrugsModel
import com.lion_tech.hmo.hospital.hospital_dashboard.models.HospitalBillModel
import com.lion_tech.hmo.hospital.hospital_dashboard.models.ServicessModel
import com.lion_tech.hmo.hospital.hospital_dashboard.ui.bills.HospitalBillsFragViewModel
import kotlinx.android.synthetic.main.content_hospital_dashboard.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HospitalPaidBill : Fragment() {

    private var filterList: ArrayList<HospitalBillModel>? = null
    private var pdfFile: File? = null
    private var approvedBillList: List<HospitalBillModel> = emptyList()
    private lateinit var hospitalBillFragViewModel: HospitalBillsFragViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var rvClientDiagnoseServiceAdapter: ClientDiagnoseServiceAdapter
    private lateinit var rvClientDiagnoseDrugsAdapter: ClientDiagnoseDrugsAdapter
    private lateinit var rvBills: RecyclerView
    private lateinit var rvAdapter: HospitalPaidBillAdapter
    private var hospitalBillModel: HospitalBillModel? = null
    private var requestDate: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        hospitalBillFragViewModel =
            ViewModelProvider(this).get(HospitalBillsFragViewModel::class.java)
        return inflater.inflate(R.layout.fragment_hospital_bills, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HospitalDashboard).hideTextView(
            true,
            activity!!.getString(R.string.paid_bills)
        )

        filterList = ArrayList()
        AppLevelData.hospitalPaidBillFrag = this
        AppLevelData.isPaidBill = true
        rvBills = view.findViewById(R.id.rvRequest)
        progressBar = view.findViewById(R.id.progressBar)

        rvAdapter = HospitalPaidBillAdapter(activity as HospitalDashboard, this)
        rvClientDiagnoseServiceAdapter = ClientDiagnoseServiceAdapter(activity as HospitalDashboard)
        rvClientDiagnoseDrugsAdapter = ClientDiagnoseDrugsAdapter(activity as HospitalDashboard)

        rvBills.layoutManager = LinearLayoutManager(activity as HospitalDashboard)
        rvBills.adapter = rvAdapter

        if (!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)) {
            Toast.makeText(
                context,
                context!!.getString(R.string.conection_problem),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            hospitalBillFragViewModel.getHospitalApprovedBillList()
        }


        hospitalBillFragViewModel.hospitalApprovedBills.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {


                approvedBillList = (it.sortedBy { it.date }).reversed()
                filterList!!.addAll(approvedBillList)
                rvAdapter.refreshAdapter(it)

                var totalAmount: Float = 0f
                for (model in it) {
                    totalAmount += model.totalBill
                }

                (activity as HospitalDashboard).tvAmount.text = "₦${NumberFormat.getInstance().format(totalAmount)}"
                progressBar.visibility = View.GONE
            })


        hospitalBillFragViewModel.clientDiagnoseServicesAndDrugsJson.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer {

                //
                try {
                    val drugsList: ArrayList<DrugsModel> = ArrayList()
                    val serviceList: ArrayList<ServicessModel> = ArrayList()

                    val jsonObject = JSONObject(it)
                    val jsonServiceArray = jsonObject.getJSONArray("services")
                    val jsonDrugsArray = jsonObject.getJSONArray("drugs")

                    for (i in 0 until jsonDrugsArray.length()) {
                        val jsonObject = jsonDrugsArray.getJSONObject(i)
                        drugsList.add(
                            DrugsModel(
                                jsonObject.getInt("drug_id"),
                                jsonObject.getString("drug_name"),
                                jsonObject.getDouble("drug_price").toFloat()
                            )
                        )
                    }

                    for (i in 0 until jsonServiceArray.length()) {
                        val jsonObject = jsonServiceArray.getJSONObject(i)
                        serviceList.add(
                            ServicessModel(
                                jsonObject.getInt("id"),
                                jsonObject.getString("service_name"),
                                jsonObject.getDouble("service_price").toFloat()
                            )
                        )
                    }

                    AppLevelData.clientDiagnoseServicess = serviceList
                    AppLevelData.clientDiagnoseDrugs = drugsList

                    progressBar.visibility = View.GONE
                    showDepDetail(serviceList, drugsList)
                } catch (exce: Exception) {

                }

                //
            })

        (activity as HospitalDashboard).tvPdf.setOnClickListener {

            if (filterList!!.isEmpty()) {
                Toast.makeText(
                    activity as HospitalDashboard,
                    "Cannot export empty list",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            val pdfDir: File =
                File((activity as HospitalDashboard).getExternalFilesDir(null), "HMO_DATA")
            if (!pdfDir.exists()) {
                Toast.makeText(activity as HospitalDashboard, "Not Exist", Toast.LENGTH_SHORT)
                    .show()
                pdfDir.mkdir()
            }

            pdfFile = File(pdfDir.absolutePath, "hmo_bill_${System.currentTimeMillis()}.pdf")
            val outPutStream: OutputStream = FileOutputStream(pdfFile)
            val pdfDoc = Document(PageSize.A4)
            val pdfTable = PdfPTable(floatArrayOf(3f, 3f, 3f))

            pdfTable.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
            pdfTable.defaultCell.verticalAlignment = Element.ALIGN_MIDDLE
            pdfTable.defaultCell.fixedHeight = 50f
            pdfTable.widthPercentage = 100f

            pdfTable.addCell(
                Paragraph(
                    "Enrolle Name",
                    Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD, BaseColor.BLACK)
                )
            )
            pdfTable.addCell(
                Paragraph(
                    "Date",
                    Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD, BaseColor.BLACK)
                )
            )
            pdfTable.addCell(
                Paragraph(
                    "Amount",
                    Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD, BaseColor.BLACK)
                )
            )

            pdfTable.headerRows = 1

            var pdfTableCell = pdfTable.getRow(0).cells

            for (i in pdfTableCell.indices) {
                pdfTableCell[i].backgroundColor = BaseColor.LIGHT_GRAY
            }

            for (model in filterList!!) {
                pdfTable.addCell(model.enrolleeName)
                pdfTable.addCell(model.date)
                pdfTable.addCell("${model.totalBill}")
            }
            pdfTable.addCell("")
            pdfTable.addCell("Total")
            pdfTable.addCell("${(activity as HospitalDashboard).tvAmount.text}")

            PdfWriter.getInstance(pdfDoc, outPutStream)
            pdfDoc.open()

            val fromDate = filterList!![filterList!!.size-1].date
            val toDate = filterList!![0].date

            val fontF = Font(Font.FontFamily.TIMES_ROMAN, 20f, Font.UNDERLINE, BaseColor.BLACK)
            val fontG = Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.UNDERLINE, BaseColor.BLACK)
            var pdfTitle = "HMO Paid Bills"
            val hospitalJson = AppLevelData.hospitalDetailJson

            if (hospitalJson != null) {
                pdfTitle = "${hospitalJson.getString("hospital_name")} Paid Bills \n"
            }

            pdfDoc.add(Paragraph(pdfTitle, fontF))

            pdfDoc.add(Paragraph("Date : $fromDate To $toDate\n\n", fontG))
            pdfDoc.add(pdfTable)
            pdfDoc.close()
            if (pdfFile != null) {
                previewPdf()
            } else {
                progressBar.visibility = View.GONE

            }
        }

    }

    private fun previewPdf() {
        val packageManager: PackageManager = (activity as HospitalDashboard).packageManager
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.type = "application/pdf"
        var pdfSupportApp =
            packageManager.queryIntentActivities(pdfIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (pdfSupportApp.size > 0) {
            val uri = FileProvider.getUriForFile(
                activity as HospitalDashboard,
                BuildConfig.APPLICATION_ID + ".provider",
                pdfFile!!
            )
            progressBar.visibility = View.GONE

            val mIntent = Intent()
            mIntent.action = Intent.ACTION_VIEW
//            val uri:Uri = Uri.fromFile(pdfFile)
            mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mIntent.setDataAndType(uri, "application/pdf")
            (activity as HospitalDashboard).startActivity(mIntent)

        } else {
            progressBar.visibility = View.GONE

            Toast.makeText(
                activity as HospitalDashboard,
                "Please Download Pdf Viewer",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun sendRequestForDrugsAndService(model: HospitalBillModel, date: String) {

        progressBar.visibility = View.VISIBLE
        hospitalBillModel = model
        requestDate = date

        if (!AppLevelData.verifyAvailableNetwork(context as HospitalDashboard)) {
            Toast.makeText(
                context,
                context!!.getString(R.string.conection_problem),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            hospitalBillFragViewModel.getClientDiagnoseServiceAndDrugs(model.requestId.toString())
        }


    }


    fun showDepDetail(serviceList: List<ServicessModel>, drugsList: List<DrugsModel>) {

        val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_request_detail, null, false)
//
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvCode: TextView = view.findViewById(R.id.tvCode)
        val tvCodeTitle: TextView = view.findViewById(R.id.tvCodeTitle)
        val tvHospitalName: TextView = view.findViewById(R.id.tvHospitalName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)

        val tvTotalBills: TextView = view.findViewById(R.id.tvBill)
        val tvDiagnose: TextView = view.findViewById(R.id.tvDiagnose)
        val tvMedical: TextView = view.findViewById(R.id.tvMedical)
        val tvInvestigation: TextView = view.findViewById(R.id.tvInvestigation)
        val tvProcedure: TextView = view.findViewById(R.id.tvProcedure)

        val rvServices: RecyclerView = view.findViewById(R.id.rvServices)
        val rvDrugs: RecyclerView = view.findViewById(R.id.rvDrugs)
        rvServices.layoutManager = LinearLayoutManager(activity)
        rvDrugs.layoutManager = LinearLayoutManager(activity)

        rvDrugs.adapter = rvClientDiagnoseDrugsAdapter
        rvServices.adapter = rvClientDiagnoseServiceAdapter

        rvClientDiagnoseDrugsAdapter.refreshAdapter(drugsList)
        rvClientDiagnoseServiceAdapter.refreshAdapter(serviceList)


        val btnDone: Button = view.findViewById(R.id.btnDone)

        if (hospitalBillModel != null) {


            tvName.text = hospitalBillModel!!.enrolleeName

            tvDate.text = requestDate
            tvTotalBills.text = "₦${NumberFormat.getInstance().format(hospitalBillModel!!.totalBill)}"
            tvDiagnose.text = hospitalBillModel!!.diagnose
            tvInvestigation.text = hospitalBillModel!!.investigation
            tvMedical.text = hospitalBillModel!!.medical
            tvProcedure.text = hospitalBillModel!!.procedure

            val hospitalJson = AppLevelData.hospitalDetailJson
            if (hospitalJson != null) {
                tvHospitalName.text = hospitalJson.getString("hospital_name")

            }

            var requestCode: String = hospitalBillModel!!.requestCode
            if (requestCode == "null") {
                requestCode = "........."
            }
            tvCode.text = requestCode
            tvCode.visibility = View.GONE
            tvCodeTitle.visibility = View.GONE

        }
        val alertDialog = android.app.AlertDialog.Builder(context)
        alertDialog.setView(view)

        val dialog = alertDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnDone.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


    }

    fun filterBills(fromDateStrin: String, toDateString: String) {
//        String dtStart = "2010-10-15T09:27:37Z";
        val format: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy");
        val format1: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd");
        try {

            filterList!!.clear()

            val fromDate: Date = format.parse(fromDateStrin);
            val toDate: Date = format.parse(toDateString);

            var totalFilterAmount: Float = 0f

            for (model in approvedBillList) {
                val billDate: Date = format1.parse(model.date)
                val isDateIn = fromDate.compareTo(billDate) * billDate.compareTo(toDate) >= 0
                if (isDateIn) {
                    filterList!!.add(model)
                    totalFilterAmount += model.totalBill
                }
            }
            var mList=filterList!!.sortedBy { it.date }
//                    as ArrayList<HospitalBillModel>
             if(mList!!.isNotEmpty()){
                mList=mList!!.reversed()
             }

            (activity as HospitalDashboard).tvAmount.text = "₦${NumberFormat.getInstance().format(totalFilterAmount)}"
            rvAdapter.refreshAdapter(mList!!)

        } catch (e: ParseException) {
            e.printStackTrace();
        }
    }

}