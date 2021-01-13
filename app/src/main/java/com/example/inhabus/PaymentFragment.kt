package com.example.inhabus

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_payment.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL

class PaymentFragment: Fragment() {
    private var IP_ADDRESS: String = "192.168.56.1"
    private lateinit var mJsonString: String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_payment,container,false)

        var pay_btn: Button = view.findViewById(R.id.pay_button)
        pay_btn.setOnClickListener {
            beginPayment()
        }
        return view
    }

    fun beginPayment(){
        var task: PaymentFragment.Payment = Payment()
        task.execute("http://" + IP_ADDRESS + "/sub.php", arguments!!.getString("nickname"), arguments!!.getString("date"), arguments!!.getString("direction"), arguments!!.getString("city"), arguments!!.getString("time"))
    }

    override fun onStart() {
        super.onStart()
        reservation_date_at_payment.setText(arguments!!.getString("date"))
        reservation_detail_at_payment.setText(arguments!!.getString("direction") + arguments!!.getString("city") + arguments!!.getString("time"))
    }

    override fun onResume() {
        super.onResume()
    }

    inner private class Payment : AsyncTask<String?, Void, String?>() {
        //lateinit var progressDialog: ProgressDialog
        var errorMessage: String = "Failed!"

        protected override fun onPreExecute(){
            super.onPreExecute()
        }

        protected override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            showAlertDialog("예약 완료", "예약이 완료되었습니다!")
        }

        override fun doInBackground(vararg p0: String?): String? {
            var serverURL: String? = p0[0]
            var nickname: String? = p0[1]
            var date: String? = p0[2]
            var direction: String? = p0[3]
            var city: String? = p0[4]
            var time: String? = p0[5]
            var postParameters: String? = "nickname=" + nickname + "&date=" + date + "&direction=" + direction + "&city=" + city + "&time=" + time

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

                return sb.toString()

            } catch(e: Exception){
                Log.d("Test","GetData : Error ",e)
                errorMessage = e.toString()
                return null
            }
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
    }
}