package com.lion_tech.hmo.client.activities.fragments.changeLoginDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.lion_tech.hmo.R
import com.lion_tech.hmo.app_data.AppLevelData
import com.lion_tech.hmo.client.activities.DashboardClient

class ChangeLoginDetail : Fragment() {

    private lateinit var changeLoginDetail: ChangeLoginDetailViewModel

    lateinit var etCurrentPass: TextInputEditText
    lateinit var etNewPass: TextInputEditText
    lateinit var etConfPass: TextInputEditText
    lateinit var btnUpdate: Button
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changeLoginDetail =
            ViewModelProvider(this).get(ChangeLoginDetailViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_change_login_detail, container, false)


        (activity as DashboardClient).hideTextView(
            true,
            activity!!.getString(R.string.change_login_detail)
        )

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etConfPass = view.findViewById(R.id.etConfPass)
        etCurrentPass = view.findViewById(R.id.etCurrentPass)
        etNewPass = view.findViewById(R.id.etNewPass)
        btnUpdate = view.findViewById(R.id.btnUpdatePass)
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        btnUpdate.setOnClickListener {

            if (etConfPass.text.toString().isEmpty()) {
                etConfPass.requestFocus()
                etConfPass.error = getString(R.string.required)
                return@setOnClickListener
            } else if (etCurrentPass.text.toString().isEmpty()) {
                etCurrentPass.requestFocus()
                etCurrentPass.error = getString(R.string.required)
                return@setOnClickListener
            } else if (etNewPass.text.toString().isEmpty()) {
                etNewPass.requestFocus()
                etNewPass.error = getString(R.string.required)
                return@setOnClickListener
            } else if (etNewPass.text.toString() != etConfPass.text.toString()) {
                etConfPass.requestFocus()
                etConfPass.error = getString(R.string.pass_did_not_match)
                return@setOnClickListener
            } else if (etCurrentPass.text.toString() != AppLevelData.currentPass) {
                etCurrentPass.requestFocus()
                etCurrentPass.error = getString(R.string.wrong_password)
                return@setOnClickListener
            }
            if (!AppLevelData.verifyAvailableNetwork(context as DashboardClient)) {
                Toast.makeText(
                    context,
                    context!!.getString(R.string.conection_problem),
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                changeLoginDetail.changeLoginDetail(
                    activity as DashboardClient,
                    etNewPass.text.toString(),
                    progressBar
                )
            }
        }

    }


}