package com.lion_tech.hmo.client.activities.adpaters.recyclerViewAdapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.DashboardClient
import com.lion_tech.hmo.client.activities.fragments.changeProviderRequest.ChangeProviderRequestViewModel
import com.lion_tech.hmo.client.activities.models.ProviderModel

class ChangeProviderRvAdapter(
    var context: Context,
    private var changeProviderRequestViewModel: ChangeProviderRequestViewModel
) :
    RecyclerView.Adapter<ChangeProviderRvAdapter.ProviderViewHolder>() {

    private var providerList: List<ProviderModel> = emptyList()
    private lateinit var alertDialog: AlertDialog

    fun refreshAdapter(providerList: List<ProviderModel>) {
        this.providerList = providerList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_change_provider, parent, false)

        return ProviderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return providerList.size
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        holder.tvProviderName.text = providerList[position].hospitalName
        holder.tvLocationName.text = providerList[position].hospitalLocation
        holder.tvAddress.text = providerList[position].hospitalAddress
        holder.tvChangProvider.setOnClickListener {

            if (!AppLevelData.verifyAvailableNetwork(context as DashboardClient)) {
                Toast.makeText(
                    context,
                    context!!.getString(R.string.conection_problem),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showReasonDialog(
                    providerList[position].hospitalId,
                    providerList[position],
                    context
                )
            }


        }
    }

    private  fun showReasonDialog(hospitalId: Int, providerModel: ProviderModel, context: Context) {

        val layout: CardView = LayoutInflater.from(context).inflate(
            R.layout.dialog_hospital_request_change_reason,
            null,
            false
        ) as CardView
        val etReason: TextInputEditText = layout.findViewById(R.id.etReason)
        val cvProviderChange: CardView = layout.findViewById(R.id.cvChangeProvider)
        val dialogProgressBar: ProgressBar = layout.findViewById(R.id.progressBar)

        cvProviderChange.setOnClickListener {
            if (etReason.text.toString().isEmpty()) {
                etReason.error = "Please provide reason."
                etReason.requestFocus()
                return@setOnClickListener
            }

            dialogProgressBar.visibility=View.VISIBLE
            changeProviderRequestViewModel.changeClientProvider(
                hospitalId,
                providerModel,
                etReason.text.toString(),
                context
            )

        }


        val dialog = AlertDialog.Builder(context)
        dialog.setView(layout)
        alertDialog = dialog.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
    }

    fun dismissDialog() {
        alertDialog.dismiss()
    }

    inner class ProviderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvProviderName: TextView = view.findViewById(R.id.tvProviderName)
        var tvLocationName: TextView = view.findViewById(R.id.tvLocationName)
        var tvChangProvider: TextView = view.findViewById(R.id.tvChangeProvider)
        var tvAddress: TextView = view.findViewById(R.id.tvAddress)
    }
}