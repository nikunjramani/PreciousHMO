package com.lion_tech.hmo.client.activities.fragments.contactUs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.DashboardClient
import com.lion_tech.hmo.client.activities.fragments.providers.ContactUsViewModel

class ContactUs : Fragment() {

    private lateinit var providersViewModel: ContactUsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        providersViewModel =
            ViewModelProvider(this).get(ContactUsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_contact_us, container, false)

        (activity as DashboardClient).hideTextView(
            true,
            activity!!.getString(R.string.contact_Us)
        )

        return root
    }
}