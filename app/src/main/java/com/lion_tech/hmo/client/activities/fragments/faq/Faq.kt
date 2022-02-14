package com.lion_tech.hmo.client.activities.fragments.providers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.DashboardClient
import com.lion_tech.hmo.client.activities.adpaters.FaqRvAdapter
import com.lion_tech.hmo.client.activities.models.FaqModel

class Faq : Fragment() {

    private lateinit var providersViewModel: FaqViewModel
    private lateinit var rvAdapter: FaqRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        providersViewModel =
            ViewModelProvider(this).get(FaqViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_faq, container, false)
        (activity as DashboardClient).hideTextView(
            true,
            activity!!.getString(R.string.faq)
        )
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


var _modelList:ArrayList<FaqModel> = ArrayList()

        _modelList.add(
            FaqModel(
            "Google icons in iOS, Material, Windows, and other design styles",
            "Get free icons of Google in iOS, Material, Windows and other design styles for web, mobile, and graphic design projects. The free images are pixel perfect to fit your design and available in both png and vector. Download icons in all formats or edit them for your designs."
        ))
        _modelList.add(
            FaqModel(
            "Google icons in iOS, Material, Windows, and other design styles",
            "Get free icons of Google in iOS, Material, Windows and other design styles for web, mobile, and graphic design projects. The free images are pixel perfect to fit your design and available in both png and vector. Download icons in all formats or edit them for your designs."
        ))
        _modelList.add(
            FaqModel(
            "Google icons in iOS, Material, Windows, and other design styles",
            "Get free icons of Google in iOS, Material, Windows and other design styles for web, mobile, and graphic design projects. The free images are pixel perfect to fit your design and available in both png and vector. Download icons in all formats or edit them for your designs."
        ))
        _modelList.add(
            FaqModel(
            "Google icons in iOS, Material, Windows, and other design styles",
            "Get free icons of Google in iOS, Material, Windows and other design styles for web, mobile, and graphic design projects. The free images are pixel perfect to fit your design and available in both png and vector. Download icons in all formats or edit them for your designs."
        ))
        _modelList.add(
            FaqModel(
            "Google icons in iOS, Material, Windows, and other design styles",
            "Get free icons of Google in iOS, Material, Windows and other design styles for web, mobile, and graphic design projects. The free images are pixel perfect to fit your design and available in both png and vector. Download icons in all formats or edit them for your designs."
        ))

        rvAdapter =
            FaqRvAdapter(activity as DashboardClient,_modelList)

        val rvFaq = view.findViewById<RecyclerView>(R.id.rvFaq)
        rvFaq.layoutManager = LinearLayoutManager(activity as DashboardClient)
        rvFaq.adapter = rvAdapter

    }
}