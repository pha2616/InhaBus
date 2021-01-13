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
    var bundle: Bundle = Bundle()
    var go_to_school: Boolean = true
    var possible_day: Boolean = true
    val calendar = Calendar.getInstance()
    val format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_reservation,container,false)
        var reservation_btn: Button = view.findViewById(R.id.reservation_button)
        var res: Resources = resources

        bundle.putString("nickname",arguments!!.getString("nickname"))

        calendar_view = view!!.findViewById(R.id.calendarView)
        direction_spinner = view!!.findViewById(R.id.where_to_go)
        city_spinner = view!!.findViewById(R.id.city_spinner)
        time_spinner = view!!.findViewById(R.id.time_spinner)

        calendar_view.minDate = Calendar.getInstance().timeInMillis
        calendar.add(Calendar.MONTH, 1)
        calendar_view.maxDate = calendar.timeInMillis

        calendar_view.setOnDateChangeListener { calendarView, i, i2, i3 ->
            calendar.set(i,i2,i3)
            if(calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7){
                possible_day = false
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
                direction.text = res.getStringArray(R.array.home_or_school).get(p2)

                var adapter_school_city = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.city_array))
                city_spinner.adapter = adapter_school_city
                when (direction_spinner.getItemAtPosition(p2)) {
                    "등교" -> {
                        city_info.text = "출발지"
                        go_to_school = true
                    }
                    "하교" -> {
                        city_info.text = "목적지"
                        go_to_school = false
                    }
                }
            }
        }

        city_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if(go_to_school) {
                    city.text = res.getStringArray(R.array.city_array).get(p2)
                    var adapter_school_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_school))
                    time_spinner.adapter = adapter_school_time
                }

                else {
                    city.text = res.getStringArray(R.array.city_array).get(p2)
                    when(city_spinner.getItemAtPosition(p2)){
                        "신촌" -> {
                            var adapter_sincheon_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_sincheon))
                            time_spinner.adapter = adapter_sincheon_time
                        }
                        "강남" -> {
                            var adapter_gangnam_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_gangnam))
                            time_spinner.adapter = adapter_gangnam_time
                        }
                        "가양","안양","수원","분당" -> {
                            var adapter_etc_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_etc))
                            time_spinner.adapter = adapter_etc_time
                        }
                        "일산" -> {
                            var adapter_ilsan_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_ilsan))
                            time_spinner.adapter = adapter_ilsan_time
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
                        time.text = "08:00"
                    }
                    "17:30" -> {
                        time.text = "17:30"
                    }
                    "18:30" -> {
                        time.text = "18:30"
                    }
                    "19:30" -> {
                        time.text = "19:30"
                    }
                    "20:30" -> {
                        time.text = "20:30"
                    }
                    "21:30" -> {
                        time.text = "21:30"
                    }
                    "22:40" -> {
                        time.text = "22:40"
                    }
                }
            }
        }

        reservation_btn.setOnClickListener {
            if(possible_day){
                if(direction.text.toString() == "등교"){
                    bundle.putString("direction","school")
                }
                else if(direction.text.toString() == "하교"){
                    bundle.putString("direction","home")
                }

                if(city.text.toString() == "신촌"){
                    bundle.putString("city","sincheon")
                }
                else if(city.text.toString() == "강남"){
                    bundle.putString("city","gangnam")
                }
                else if(city.text.toString() == "가양"){
                    bundle.putString("city","gayang")
                }
                else if(city.text.toString() == "안양"){
                    bundle.putString("city","anyang")
                }
                else if(city.text.toString() == "일산"){
                    bundle.putString("city","ilsan")
                }
                else if(city.text.toString() == "수원"){
                    bundle.putString("city","suwon")
                }
                else if(city.text.toString() == "분당"){
                    bundle.putString("city","bundang")
                }

                bundle.putString("date",reservation_date.text.toString())
                bundle.putString("time",time.text.toString())
               replaceFragment(PaymentFragment())
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
        reservation_date.text = format.format(Calendar.getInstance().time)
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
        fragment.arguments = bundle
        fragmentTransaction.commit()
    }
}