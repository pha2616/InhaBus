package com.example.inhabus

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    private var IP_ADDRESS: String = "192.168.56.1"

    private lateinit var mEditTextNickname: EditText
    private lateinit var mEditTextEmail: EditText
    private lateinit var mEditTextPasswd: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        nickname_check_btn.setOnClickListener {

        }

        signup_button.setOnClickListener {
            signup()
        }

        cancle_btn.setOnClickListener {
            val intent = Intent(this@SignupActivity,LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun signup(){
        mEditTextNickname = findViewById(R.id.nickname_edittext)
        mEditTextEmail = findViewById(R.id.email_edittext_signup)
        mEditTextPasswd = findViewById(R.id.password_edittext_signup)

        val nickname: String? = mEditTextNickname.text.toString()
        val email: String? = mEditTextEmail.text.toString()
        val passwd: String? = mEditTextPasswd.text.toString()

        var task: SignupActivity.InsertData = InsertData()
        task.execute("http://" + IP_ADDRESS + "/insert.php", nickname, email, passwd)
    }

    inner private class InsertData : AsyncTask<String?, Void, String?>() {
        //lateinit var progressDialog: ProgressDialog
        var errorMessage: String = "Failed!"

        protected override fun onPreExecute(){
            super.onPreExecute()
        }

        protected override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val dlg: AlertDialog.Builder = AlertDialog.Builder(this@SignupActivity,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            dlg.setTitle("Signup Success")
            dlg.setMessage("Login and join our services!")
            dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                startActivity(Intent(this@SignupActivity,LoginActivity::class.java))
                finish()})
            dlg.show()
        }

        override fun doInBackground(vararg p0: String?): String? {
            var serverURL: String? = p0[0]
            var nickname: String? = p0[1]
            var email: String? = p0[2]
            var passwd: String? = p0[3]
            var postParameters: String? = "nickname=" + nickname + "&email=" + email + "&passwd=" + passwd

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

    }

}