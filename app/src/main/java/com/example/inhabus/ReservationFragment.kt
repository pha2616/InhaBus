package com.example.inhabus

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_reservation.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ReservationFragment: Fragment() {
    private var IP_ADDRESS: String = "192.168.219.107"
    private lateinit var mJsonString: String
    lateinit var calendar_view: CalendarView
    lateinit var direction_spinner: Spinner
    lateinit var city_spinner: Spinner
    lateinit var time_spinner: Spinner
    lateinit var selected_date: String
    var selected_direction = "school"
    var selected_city = "sincheon"
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
                showAlertDialog("IMPOSSIBLE DAY","choose another day")
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
                        selected_direction = "school"
                        go_to_school = true
                    }
                    "하교" -> {
                        city_info.text = "목적지"
                        selected_direction = "home"
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
                            selected_city = "sincheon"
                            var adapter_sincheon_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_sincheon))
                            time_spinner.adapter = adapter_sincheon_time
                        }
                        "강남" -> {
                            selected_city = "gangnam"
                            var adapter_gangnam_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_gangnam))
                            time_spinner.adapter = adapter_gangnam_time
                        }
                        "가양","안양","수원","분당" -> {
                            var adapter_etc_time = ArrayAdapter<String>(activity!!.baseContext, android.R.layout.simple_spinner_dropdown_item, res.getStringArray(R.array.time_to_home_etc))
                            time_spinner.adapter = adapter_etc_time
                            when(city_spinner.getItemAtPosition(p2)){
                                "가양" -> selected_city = "gayang"
                                "안양" -> selected_city = "anyang"
                                "수원" -> selected_city = "suwon"
                                "분당" -> selected_city = "bundang"
                            }
                        }
                        "일산" -> {
                            selected_city = "ilsan"
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
                bundle.putString("direction",selected_direction)
                bundle.putString("city",selected_city)
                bundle.putString("date",reservation_date.text.toString())
                bundle.putString("time",time.text.toString())

                var task: ReservationFragment.GetData = GetData()
                task.execute("http://" + IP_ADDRESS + "/checkQuantity.php", selected_direction, selected_city, time.text.toString(), reservation_date.text.toString())
            }
            else{
                showAlertDialog("IMPOSSIBLE DAY","choose another day")
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

    private fun showAlertDialog(title: String, message: String){
        val dlg: AlertDialog.Builder = AlertDialog.Builder(activity!!,
            android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        dlg.setTitle(title)
        dlg.setMessage(message)
        dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()})
        dlg.show()
    }

    inner private class GetData : AsyncTask<String?, Void, String?>() {
        //lateinit var progressDialog: ProgressDialog
        var errorMessage: String = "Failed!"

        protected override fun onPreExecute(){
            super.onPreExecute()
        }

        protected override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if(result == null){
            }
            else{
                if(result!=""){
                    mJsonString = result
                    checkQuantity()
                }
            }
        }

        override fun doInBackground(vararg p0: String?): String? {
            var serverURL: String? = p0[0]
            var direction: String? = p0[1]
            var city: String? = p0[2]
            var time: String? = p0[3]
            var date: String? = p0[4]
            var postParameters: String? = "direction=" + direction + "&city=" + city + "&time=" + time + "&date=" + date

            try{
                val url: URL = URL(serverURL)
                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.readTimeout = 5000
                httpURLConnection.connectTimeout = 5000
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.doInput = true
                httpURLConnection.connect()

                val outputStream = DataOutputStream(httpURLConnection.outputStream)
                outputStream.writeBytes(postParameters)
                outputStream.flush()
                outputStream.close()

                var responseStatusCode: Int = httpURLConnection.responseCode
                Log.d("Test", "response code - " + responseStatusCode)

                val inputStream: InputStream
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.inputStream
                }
                else{
                    inputStream = httpURLConnection.errorStream
                }

                val inputStreamReader: InputStreamReader = InputStreamReader(inputStream,"UTF-8")
                val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)

                val sb = StringBuilder()
                var line: String? = null

                line = bufferedReader.readLine()
                while(line != null){
                    sb.append(line)
                    line = bufferedReader.readLine()
                }

                bufferedReader.close()

                return sb.toString().trim()
            } catch(e: Exception){
                Log.d("Test","GetData : Error ",e)
                errorMessage = e.toString()
                return null
            }
        }

        private fun checkQuantity() {
            val TAG_JSON: String = "hyuna"
            val TAG_QUANTITY= "quantity_left"

            try{
                var jsonObject: JSONObject = JSONObject(mJsonString)
                var jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)

                val item: JSONObject = jsonArray.getJSONObject(0)

                val Jquantity: Int = item.getInt(TAG_QUANTITY)
                if(Jquantity == 0){
                    showAlertDialog("IMPOSSIBLE BUS","매진되었습니다")
                }
                else{
                    replaceFragment(PaymentFragment())
                }
            } catch(e: java.lang.Exception){
                Log.d("Test", "showResult : ", e)
            }
        }

        private fun replaceFragment(fragment: Fragment){
            val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.main_content, fragment)
            fragmentTransaction.addToBackStack(null)
            fragment.arguments = bundle
            fragmentTransaction.commit()
        }
    }
}