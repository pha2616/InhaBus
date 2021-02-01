package com.example.inhabus.navigation

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inhabus.AlarmList
import com.example.inhabus.CustomAdapter
import com.example.inhabus.R
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.android.synthetic.main.fragment_user.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class AlarmFragment : Fragment() {
    var bundle: Bundle = Bundle()
    private var IP_ADDRESS: String = "192.168.219.107"
    private lateinit var mJsonString: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_alarm,container,false)
        var task: AlarmFragment.GetData = GetData()
        task.execute("http://" + IP_ADDRESS + "/checkSub.php", arguments!!.getString("nickname"))
        return view
    }

    inner private class GetData() : AsyncTask<String?, Void, String?>() {
        //lateinit var progressDialog: ProgressDialog
        var errorMessage: String = "Failed!"

        constructor(parcel: Parcel) : this() {
            errorMessage = parcel.readString().toString()
        }

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
                    val data: MutableList<AlarmList> = loadSub()
                    var adapter = CustomAdapter()
                    adapter.alarmList = data
                    alarmfragment_recyclerview.adapter = adapter
                    alarmfragment_recyclerview.layoutManager = LinearLayoutManager(activity!!.baseContext)
                    alarmfragment_recyclerview.addItemDecoration(DividerItemDecoration(view!!.context, 1))
                }
            }
        }

        override fun doInBackground(vararg p0: String?): String? {
            var serverURL: String? = p0[0]
            var nickname: String? = p0[1]
            var postParameters: String? = "nickname=" + nickname

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

        private fun loadSub(): MutableList<AlarmList> {
            val data:MutableList<AlarmList> = mutableListOf()
            val TAG_JSON: String = "hyuna"
            val TAG_DATE: String = "bus_date"
            val TAG_DIRECTION = "bus_direction"
            val TAG_CITY = "city"
            val TAG_TIME = "bus_time"

            try{
                var jsonObject: JSONObject = JSONObject(mJsonString)
                var jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)

                for(i in jsonArray.length() - 1 downTo 0) {
                    val item: JSONObject = jsonArray.getJSONObject(i)

                    var reservation_date = item.getString(TAG_DATE)
                    var direction = item.getString(TAG_DIRECTION)
                    var city = item.getString(TAG_CITY) + " 예약이 있습니다."
                    var time = item.getString(TAG_TIME)
                    var alarmlist = AlarmList(reservation_date, time, direction, city)
                    data.add(alarmlist)
                }
                return data
            } catch(e: java.lang.Exception){
                Log.d("Test", "showResult : ", e)
            }
            return data
        }
    }
}