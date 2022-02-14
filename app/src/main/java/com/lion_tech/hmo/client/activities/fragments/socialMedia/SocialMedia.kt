package com.lion_tech.hmo.client.activities.fragments.providers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.DashboardClient

class SocialMedia : Fragment() {

    private lateinit var providersViewModel: SocialMediaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        providersViewModel =
            ViewModelProvider(this).get(SocialMediaViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_social_media, container, false)

        (activity as DashboardClient).hideTextView(
            true,
            activity!!.getString(R.string.social_media)
        )


        return root
    }
}