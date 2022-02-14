package com.lion_tech.hmo.client.activities.fragments.providers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.DashboardClient
import com.lion_tech.hmo.client.activities.fragments.userPolicy.UserPolicyViewModel

class UserPolicy : Fragment() {

    private lateinit var providersViewModel: UserPolicyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        providersViewModel =
            ViewModelProvider(this).get(UserPolicyViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user_policy, container, false)

        (activity as DashboardClient).hideTextView(
            true,
            activity!!.getString(R.string.user_policy)
        )
        return root
    }
}