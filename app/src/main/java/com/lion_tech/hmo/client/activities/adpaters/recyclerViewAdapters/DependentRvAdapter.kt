package com.lion_tech.hmo.client.activities.adpaters.recyclerViewAdapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lion_tech.hmo.R
import com.lion_tech.hmo.client.activities.models.DependentModel
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DependentRvAdapter(var context: Context) :
    RecyclerView.Adapter<DependentRvAdapter.DependentViewModel>() {

    private var dependentList: List<DependentModel> = emptyList()

    fun refreshAdapter(dependentList: List<DependentModel>) {
        this.dependentList = dependentList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DependentViewModel {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_rv_dependent, parent, false)

        return DependentViewModel(view)
    }

    override fun getItemCount(): Int {
        return dependentList.size
    }

    override fun onBindViewHolder(holder: DependentViewModel, position: Int) {
        holder.tvDependentName.text = dependentList[position].name
        holder.tvPlan.text = dependentList[position].hospitalName
        holder.tvRelation.text = dependentList[position].relation
        holder.tvDetail.setOnClickListener {
            showDepDetail(dependentList[position],position+1)

        }
    }

    inner class DependentViewModel(view: View) : RecyclerView.ViewHolder(view) {
        var tvDependentName: TextView = view.findViewById(R.id.tvDepName)
        var tvPlan: TextView = view.findViewById(R.id.tvPlan)
        var tvRelation: TextView = view.findViewById(R.id.tvRelation)
        var tvDetail: Button = view.findViewById(R.id.tvDetail)
    }

    private fun showDepDetail(model: DependentModel,position:Int) {

       val view= LayoutInflater.from(context).inflate(R.layout.dialog_dependant_detail,null,false)

        val ivProfile:ImageView=view.findViewById(R.id.ivDependantProfile)
        val tvFirstName:TextView=view.findViewById(R.id.tvFirstName)
        val tvLastName:TextView=view.findViewById(R.id.tvLastName)
        val tvOtherName:TextView=view.findViewById(R.id.tvOtherName)
        val tvHospital:TextView=view.findViewById(R.id.tvHospitalName)
        val tvHospitalLocation:TextView=view.findViewById(R.id.tvHospitalLocation)
        val tvAge:TextView=view.findViewById(R.id.tvAge)
        val tvId:TextView=view.findViewById(R.id.tvId)
        val btnCancel:Button=view.findViewById(R.id.btnCancel)
        Log.d("depCode", "${model.clientProfile}")

       Picasso.get()
           .load("https://care.precioushmo.com/app/uploads/clients/${model.clientProfile}${model.clientProfile}")
           .placeholder(context.resources.getDrawable(R.drawable.ic_profile))
           .into(ivProfile)

        tvFirstName.text=model.name
        tvLastName.text=model.lastName
        tvOtherName.text=model.otherName
        tvHospital.text=model.hospitalName
        tvHospitalLocation.text=model.hospitalLocation

        tvId.text=model.dependentId_code;

        val dateOfBirth=model.dob
        val split=dateOfBirth.split("-")

         val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US);

        val date: Date = inputFormat.parse(dateOfBirth)!!

        tvAge.text=calculateAge(date)
//        tvAge.text=getAge(split[0].toInt(),split[1].toInt(),split[2].toInt())

        val alertDialog= AlertDialog.Builder(context)
        alertDialog.setView(view)

        val dialog= alertDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


    }

    private fun getAge(year: Int, month: Int, day: Int): String {
        val dob: Calendar = Calendar.getInstance();
        val today: Calendar = Calendar.getInstance();

        dob.set(year, month, day);

        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        var ageInt: Integer = Integer(age + 1)
        var ageS: String = ageInt.toString();
        Log.d("calculated age", ageS)
        return "$ageS years";
    }


    private fun calculateAge(birthDate:Date):String
    {
        var years:Int = 0;
        var months:Int = 0;
        var days:Int = 0;

        //create calendar object for birth day
        val birthDay:Calendar = Calendar.getInstance();
        birthDay.timeInMillis = birthDate.getTime();

        //create calendar object for current day
        var currentTime:Long = System.currentTimeMillis();
        val now:Calendar = Calendar.getInstance();
        now.timeInMillis = currentTime;

        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        var currMonth:Int = now.get(Calendar.MONTH) + 1;
        var birthMonth:Int = birthDay.get(Calendar.MONTH) + 1;

        //Get difference between months
        months = currMonth - birthMonth;

        //if month difference is in negative then reduce years by one
        //and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }

        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            var today:Int = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        }
        else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        //Create new Age object
//        return new Age(days, months, years);

      return "$years years,$months months,$days days"
    }
}