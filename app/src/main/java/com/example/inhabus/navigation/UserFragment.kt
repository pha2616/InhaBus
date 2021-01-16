package com.example.inhabus.navigation

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.inhabus.LoginActivity
import com.example.inhabus.MainActivity
import com.example.inhabus.PaymentFragment
import com.example.inhabus.R
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
import java.util.*

class UserFragment : Fragment() {
    private var IP_ADDRESS: String = "192.168.56.1"
    private lateinit var mJsonString: String
    private lateinit var mTextViewResult: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        var task: UserFragment.GetData = GetData()
        task.execute("http://" + IP_ADDRESS + "/checkSub.php", arguments!!.getString("nickname"))
        return view
    }

    override fun onStart() {
        super.onStart()
        user_nickname.text = arguments!!.getString("nickname")
    }

    override fun onResume() {
        super.onResume()
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
                mTextViewResult.text = errorMessage
            }
            else{
                if(result!=""){
                    mJsonString = result
                    checkSub()
                }
                else{
                    buy_time.text = "예매 내역이 없습니다"
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

        private fun showAlertDialog(title:String, message:String){
            val dlg: AlertDialog.Builder = AlertDialog.Builder(activity!!,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            dlg.setTitle(title)
            dlg.setMessage(message)
            dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()})
            dlg.show()
        }

        private fun checkSub() {
            val TAG_JSON: String = "hyuna"
            val TAG_DATE: String = "bus_date"
            val TAG_DIRECTION = "bus_direction"
            val TAG_CITY = "city"
            val TAG_TIME = "bus_time"
            val TAG_SUBDATE = "sub_date"

            try{
                var jsonObject: JSONObject = JSONObject(mJsonString)
                var jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)

                val item: JSONObject = jsonArray.getJSONObject(jsonArray.length() - 1)

                buy_time.text = "구매 날짜: " + item.getString(TAG_SUBDATE)
                reservation_date.text = "예약 날짜: " + item.getString(TAG_DATE)
                direction.text = "등/하교: " + item.getString(TAG_DIRECTION)
                city.text = "도시: " + item.getString(TAG_CITY)
                time.text = "시간: " + item.getString(TAG_TIME)
            } catch(e: java.lang.Exception){
                Log.d("Test", "showResult : ", e)
            }
        }
    }
}