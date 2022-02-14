package com.lion_tech.hmo.client.activities.fragments.providers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.DashboardClient

class Providers : Fragment() {

    private lateinit var providersViewModel: ProvidersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        providersViewModel =
            ViewModelProvider(this).get(ProvidersViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_total_providers, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)


        (activity as DashboardClient).hideTextView(true,activity!!.getString(R.string.total_providers))

        return root
    }
}