package com.lion_tech.hmo.client.activities.fragments.electronics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.DashboardClient
import com.squareup.picasso.Picasso
import org.json.JSONObject

class ElectronicsFrag : Fragment() {

    companion object {
        fun newInstance() = ElectronicsFrag()
    }

    private lateinit var viewModel: ElectronicsViewModel


    private lateinit var tvId: TextView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvContact: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvSex: TextView
    private lateinit var tvMaritalStatus: TextView
    private lateinit var tvProvider: TextView
    private lateinit var tvOrganization: TextView
    private lateinit var tvDateOfBirth: TextView
    private lateinit var tvState: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.electronics_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ElectronicsViewModel::class.java)
        (activity as DashboardClient).hideTextView(
            false,
            activity!!.getString(R.string.electronics)
        )
        tvId = view.findViewById(R.id.tvId)
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvContact = view.findViewById(R.id.tvContact)
        tvSex = view.findViewById(R.id.tvSex)
        tvMaritalStatus = view.findViewById(R.id.tvMaritalStatus)
        tvDateOfBirth = view.findViewById(R.id.tvDate)
        tvState = view.findViewById(R.id.tvState)
        tvProvider = view.findViewById(R.id.tvProvider)
        tvOrganization = view.findViewById(R.id.tvOrganization)
        tvAddress = view.findViewById(R.id.tvAddress)
        progressBar = view.findViewById(R.id.progressBar)

        if (!AppLevelData.verifyAvailableNetwork(context as DashboardClient)) {
            Toast.makeText(
                context,
                context!!.getString(R.string.conection_problem),
                Toast.LENGTH_SHORT
            ).show()

        } else {
            viewModel.getElectronicsData()
        }

        viewModel.electronicsData.observe(viewLifecycleOwner, Observer {

            val jsonObject = JSONObject(it)
            val clientJson = AppLevelData.clientDetailJson
            val electronicsId = jsonObject.getString("client_id")
            val hospitalName: String = jsonObject.getString("hospital_name")

            tvId.text = electronicsId
            tvProvider.text = hospitalName

            tvName.text = validateText(clientJson!!.getString("name")).plus(" ").plus(validateText(clientJson!!.getString("last_name")))
            tvEmail.text = validateText(clientJson!!.getString("email"))
            tvContact.text = validateText(clientJson!!.getString("contact_number"))

            tvSex.text = validateText(clientJson!!.getString("sex"))
            tvMaritalStatus.text = validateText(clientJson!!.getString("marital_status"))
            tvDateOfBirth.text = validateText(clientJson!!.getString("dob"))
            tvState.text = validateText(jsonObject.getString("state"))
            tvAddress.text = validateText(clientJson!!.getString("address_1"))
            tvOrganization.text = validateText(clientJson!!.getString("org_name"))

            if (clientJson.getString("client_profile").isNotEmpty()) {
                Picasso.get()
                    .load(
                        "https://liontechhmo.liontech.com.ng/app/uploads/clients/${clientJson.getString(
                            "client_profile"
                        )}"
                    )
                    .into((activity as DashboardClient).ivProfileElectronics)
            } else {

                Toast.makeText(
                    activity as DashboardClient,
                    "Profile image not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
            progressBar.visibility = View.GONE

        })


    }

    private fun validateText(text: String): String {
        if (text != "null" && text.isNotEmpty()) {
            return text
        }

        return ".............."
    }
}
