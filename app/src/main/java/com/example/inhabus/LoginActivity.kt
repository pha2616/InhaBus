package com.example.inhabus

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {
    private var IP_ADDRESS: String = "192.168.56.1"
    private lateinit var mEditemail: EditText
    private lateinit var mEditpasswd: EditText
    private lateinit var mTextViewResult: TextView
    private lateinit var mJsonString: String
    private lateinit var email: String
    private lateinit var passwd: String
    private lateinit var nickname: String
    private var isUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email_login_button.setOnClickListener {
            signin()
        }
        signup_btn.setOnClickListener {
            signup()
        }
        google_signin_button.setOnClickListener {
            //First step
            //gooleLogin()
        }
    }

    fun signin(){
        mEditemail = findViewById(R.id.email_edittext)
        mEditpasswd = findViewById(R.id.password_edittext)

        email = mEditemail.text.toString()
        passwd = mEditpasswd.text.toString()

        var task: GetData = GetData()
        task.execute("http://" + IP_ADDRESS + "/getjson.php", "")
    }

    fun signup(){
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
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
                mTextViewResult.text = errorMessage
            }
            else{
                if(result!=""){
                    mJsonString = result
                    checkUser()
                }
                else{
                    showAlertDialog()
                }
            }
        }

        override fun doInBackground(vararg p0: String?): String? {
            var serverURL: String? = p0[0]
            var postParameters: String? = p0[1]

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

        private fun showAlertDialog(){
            val dlg: AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            dlg.setTitle("Login Fail")
            dlg.setMessage("Try again or create new account!")
            dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                startActivity(Intent(this@LoginActivity,LoginActivity::class.java))
                finish()})
            dlg.show()
            Log.d("test","Login Fail")
        }

        private fun checkUser() {
            val TAG_JSON: String = "hyuna"
            val TAG_NICKNAME = "nickname"
            val TAG_EMAIL = "email"
            val TAG_PASSWD = "passwd"

            try{
                var jsonObject: JSONObject = JSONObject(mJsonString)
                var jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)

                for(i in 0 until jsonArray.length()){
                    val item: JSONObject = jsonArray.getJSONObject(i)

                    val Jnickname: String = item.getString(TAG_NICKNAME)
                    val Jemail: String = item.getString(TAG_EMAIL)
                    val Jpasswd: String = item.getString(TAG_PASSWD)

                    nickname = Jnickname

                    if(email == Jemail && passwd == Jpasswd){
                        isUser = true
                        break
                    }
                }

                if(isUser){
                    Log.d("Test", "Login Success!")
                    val intent = Intent(this@LoginActivity,MainActivity::class.java)
                    intent.putExtra("nickname",nickname)
                    startActivity(intent)
                }
                else{
                    showAlertDialog()
                }
            } catch(e: java.lang.Exception){
                Log.d("Test", "showResult : ", e)
            }
        }
    }
}