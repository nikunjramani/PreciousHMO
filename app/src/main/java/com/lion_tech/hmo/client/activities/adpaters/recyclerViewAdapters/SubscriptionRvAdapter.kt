package com.lion_tech.hmo.client.activities.adpaters.recyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.fragments.enrolleeProfile.EnrolleeViewModel
import com.lion_tech.hmo.client.activities.fragments.subscription.SubscriptionFragment
import com.lion_tech.hmo.client.activities.models.SubscriptionModel
import java.text.NumberFormat

class SubscriptionRvAdapter(var context: Context,var enrolleeViewModel: EnrolleeViewModel,var progressBar: ProgressBar) :
    RecyclerView.Adapter<SubscriptionRvAdapter.SubscriptionViewModel>() {

    private var subscriptionList: List<SubscriptionModel> = emptyList()

    fun refreshAdapter(newSubscriptionList: List<SubscriptionModel>) {
        this.subscriptionList = newSubscriptionList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewModel {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_subscription, parent, false)

        return SubscriptionViewModel(view)
    }

    override fun getItemCount(): Int {
        return subscriptionList.size
    }

    override fun onBindViewHolder(holder: SubscriptionViewModel, position: Int) {


        holder.tvPlanName.text = subscriptionList[position].subscriptionName
        val cost=(subscriptionList[position].subscriptionCost).replace(",","")

        holder.tvPlanCost.text = "₦${NumberFormat.getInstance().format(cost.toFloat())}"
        holder.tvViewDescrption.setOnClickListener {
                        showDepDetail(subscriptionList[position])

            SubscriptionFragment.newInstance().getSubscriptionDetail(subscriptionList[position],progressBar,enrolleeViewModel,context)


        }
    }

    inner class SubscriptionViewModel(view: View) : RecyclerView.ViewHolder(view) {
        var tvPlanName: TextView = view.findViewById(R.id.tvPlan)
        var tvPlanCost: TextView = view.findViewById(R.id.tvCost)
        var tvViewDescrption: TextView = view.findViewById(R.id.tvViewDescription)
    }

    private fun showDepDetail(model: SubscriptionModel) {


    }

}