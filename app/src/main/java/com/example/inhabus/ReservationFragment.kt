package com.example.inhabus

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_reservation.*
import java.text.SimpleDateFormat
import java.util.*

class ReservationFragment: Fragment() {
    lateinit var calendar_view: CalendarView
    lateinit var direction_spinner: Spinner
    lateinit var city_spinner: Spinner
    lateinit var time_spinner: Spinner
    lateinit var selected_date: String
    var possible_day: Boolean = false
    val calendar = Calendar.getInstance()
    val format: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_reservation,container,false)
        var reservation_btn: Button = view.findViewById(R.id.reservation_button)
        var res: Resources = resources

        calendar_view = view!!.findViewById(R.id.calendarView)
        direction_spinner = view!!.findViewById(R.id.where_to_go)
        city_spinner = view!!.findViewById(R.id.city_spinner)
        time_spinner = view!!.findViewById(R.id.time_spinner)

        calendar_view.minDate = Calendar.getInstance().timeInMillis
        calendar_view.setOnDateChangeListener { calendarView, i, i2, i3 ->
            calendar.set(i,i2,i3)
            if(calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7){
                showAlertDialog()
            }
            else{
                possible_day = true
                selected_date = format.format(calendar.time)
                reservation_date.text = selected_date
            }
        }

        var adapter = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_list_item_1, res.getStringArray(R.array.home_or_school))
          direction_spinner.adapter = adapter
          direction_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                direction.text = res.getStringArray(R.array.home_or_school).get(p2) + ", "
                when (direction_spinner.getItemAtPosition(p2)) {
                    "등교" -> {
                        var adapter_school_city = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.city_to_school))
                        city_spinner.adapter = adapter_school_city
                    }
                    "하교" -> {
                        var adapter_school_city = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.city_to_home))
                        city_spinner.adapter = adapter_school_city
                    }
                }
            }
        }

        city_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                when (city_spinner.getItemAtPosition(p2)) {
                    "인하대" -> {
                        city.text = res.getStringArray(R.array.city_to_school).get(p2) + " 행 "
                        var adapter_school_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_school))
                        time_spinner.adapter = adapter_school_time
                    }

                    else -> {
                        when(city_spinner.getItemAtPosition(p2)){
                            "신촌" -> {
                                city.text = res.getStringArray(R.array.city_to_home).get(p2) + " 행 "
                                var adapter_sincheon_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_sincheon))
                                time_spinner.adapter = adapter_sincheon_time
                            }
                            "강남" -> {
                                city.text = res.getStringArray(R.array.city_to_home).get(p2) + " 행 "
                                var adapter_gangnam_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_gangnam))
                                time_spinner.adapter = adapter_gangnam_time
                            }
                            "가양","안양","수원","분당" -> {
                                city.text = res.getStringArray(R.array.city_to_home).get(p2) + " 행 "
                                var adapter_etc_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_etc))
                                time_spinner.adapter = adapter_etc_time
                            }
                            "일산" -> {
                                city.text = res.getStringArray(R.array.city_to_home).get(p2) + " 행 "
                                var adapter_ilsan_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_ilsan))
                                time_spinner.adapter = adapter_ilsan_time
                            }
                        }
                    }
                }
            }
        }

        time_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (time_spinner.getItemAtPosition(p2)) {
                    "08:00" -> {
                        time.text = "08시 00분 버스"
                    }
                    "17:30" -> {
                        time.text = "17시 30분 버스"
                    }
                    "18:30" -> {
                        time.text = "18시 30분 버스"
                    }
                    "19:30" -> {
                        time.text = "19시 30분 버스"
                    }
                    "20:30" -> {
                        time.text = "20시 30분 버스"
                    }
                    "21:30" -> {
                        time.text = "21시 30분 버스"
                    }
                    "22:40" -> {
                        time.text = "22시 40분 버스"
                    }
                }
            }
        }

        reservation_btn.setOnClickListener {
            if(possible_day){
               //replaceFragment()
            }
            else{
                showAlertDialog()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        nickname.text = arguments!!.getString("nickname") + "님 "

    }

    override fun onResume() {
        super.onResume()
    }

    private fun showAlertDialog(){
        val dlg: AlertDialog.Builder = AlertDialog.Builder(activity!!,
            android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        dlg.setTitle("IMPOSSIBLE DAY")
        dlg.setMessage("choose another day")
        dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()})
        dlg.show()
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.main_content, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}