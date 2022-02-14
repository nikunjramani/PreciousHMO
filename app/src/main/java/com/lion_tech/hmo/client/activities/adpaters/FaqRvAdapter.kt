package com.lion_tech.hmo.client.activities.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.models.FaqModel

class FaqRvAdapter(var context: Context,newFaqList: List<FaqModel>) :
    RecyclerView.Adapter<FaqRvAdapter.FaqViewModel>() {

    private var faqList: List<FaqModel> = newFaqList



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewModel {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_faq, parent, false)
        return FaqViewModel(view)
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    override fun onBindViewHolder(holder: FaqViewModel, position: Int) {

        holder.tvQuestion.text = faqList[position].question
        holder.tvAnswer.text = faqList[position].answer
    }

    inner class FaqViewModel(view: View) : RecyclerView.ViewHolder(view) {
        var tvQuestion: TextView = view.findViewById(R.id.tvQuestion)
        var tvAnswer: TextView = view.findViewById(R.id.tvAnswer)
    }

}