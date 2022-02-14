package com.lion_tech.hmo.client.activities.fragments.subscription

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.DashboardClient
import com.lion_tech.hmo.client.activities.adpaters.recyclerViewAdapters.SubscriptionRvAdapter
import com.lion_tech.hmo.client.activities.fragments.enrolleeProfile.EnrolleeViewModel
import com.lion_tech.hmo.client.activities.models.SubscriptionBenefits
import com.lion_tech.hmo.client.activities.models.SubscriptionModel
import org.json.JSONArray

class SubscriptionFragment : Fragment() {

    companion object {
        fun newInstance() = SubscriptionFragment()
    }

    private lateinit var viewModel: SubscriptionViewModel
    private lateinit var enrolleeViewModel: EnrolleeViewModel


    private lateinit var rvAdapter: SubscriptionRvAdapter
    private lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.subscription_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(this).get(SubscriptionViewModel::class.java)
        enrolleeViewModel =
            ViewModelProvider(this).get(EnrolleeViewModel::class.java)

        (activity as DashboardClient).hideTextView(
            true,
            activity!!.getString(R.string.subscription_package)
        )

        val currentSubscriptionLayout = view.findViewById<View>(R.id.row_layout)
        val tvPlanName: TextView = currentSubscriptionLayout.findViewById(R.id.tvPlan)
        val tvPlanCost: TextView = currentSubscriptionLayout.findViewById(R.id.tvCost)
        val btnCheckBenefits: TextView =
            currentSubscriptionLayout.findViewById(R.id.tvViewDescription)



        progressBar = view.findViewById(R.id.progressBar)

        rvAdapter =
            SubscriptionRvAdapter(activity as DashboardClient, enrolleeViewModel, progressBar)

        val rvSubscription = view.findViewById<RecyclerView>(R.id.rvSubscription)
        rvSubscription.layoutManager = LinearLayoutManager(activity as DashboardClient)
        rvSubscription.adapter = rvAdapter

        val clientJson = AppLevelData.clientDetailJson
        var subscriptionId = "18"
        if (clientJson != null) {
            subscriptionId = clientJson.getString("subscription_ids")
        }

        if (!AppLevelData.verifyAvailableNetwork(context as DashboardClient)) {
            Toast.makeText(
                context,
                context!!.getString(R.string.conection_problem),
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            viewModel.getSubscriptionList(activity as DashboardClient, subscriptionId)
        }
        viewModel.subscriptionList.observe(viewLifecycleOwner, Observer {
            rvAdapter.refreshAdapter(it)


            val currentClientSubscription = AppLevelData.clientCurrentSubscription
            if (currentClientSubscription != null) {
                tvPlanCost.text = "₦${currentClientSubscription.subscriptionCost}"
                tvPlanName.text = currentClientSubscription.subscriptionName
                btnCheckBenefits.text = "Current Plan"
                btnCheckBenefits.setOnClickListener {
                    getSubscriptionDetail(currentClientSubscription, progressBar, enrolleeViewModel,activity as DashboardClient)
                }
            }
            progressBar.visibility = View.GONE
        })

        enrolleeViewModel.subscriptionData.observe(viewLifecycleOwner, Observer {
            val jsonArray = JSONArray(it)
            var subscriptionBenefitsList: ArrayList<SubscriptionBenefits> = ArrayList()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                subscriptionBenefitsList.add(
                    SubscriptionBenefits(
                        jsonObject.getInt("subscription_detail_id"),
                        jsonObject.getString("subscription_benifit")
                    )
                )

            }
            progressBar.visibility = View.GONE
            showDialog(subscriptionBenefitsList)
        })

    }

    private fun showDialog(subscriptionBenefitsList: ArrayList<SubscriptionBenefits>) {

        val view = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_subscription_benefits, null, false)
        val lvLayout = view.findViewById<LinearLayout>(R.id.lvSubscription)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        for (i in 0 until subscriptionBenefitsList.size) {
            val rowView = LayoutInflater.from(activity)
                .inflate(R.layout.row_subscription_benefits, null, false)
            val tvNo = rowView.findViewById<TextView>(R.id.tvNo)
            val tvBenefits = rowView.findViewById<TextView>(R.id.tvBenefits)

            tvNo.text = "${i + 1}"
            tvBenefits.text = subscriptionBenefitsList[i].benefitsName

            lvLayout.addView(rowView)
        }
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(activity as DashboardClient)
        alertDialog.setView(view)

        val dialog = alertDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun getSubscriptionDetail(
        model: SubscriptionModel,
        mProgressBar: ProgressBar,
        mEnrolleeViewModel: EnrolleeViewModel,
        mContext: Context
    ) {

        if (!AppLevelData.verifyAvailableNetwork(mContext as DashboardClient)) {
            Toast.makeText(
                mContext,
                mContext!!.getString(R.string.conection_problem),
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            mProgressBar.visibility = View.VISIBLE
            mEnrolleeViewModel.getSubscription(model.subscriptionId.toString())
        }

    }


}
