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
import org.json.JSONArray
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {
    private var IP_ADDRESS: String = "192.168.219.107"
    private lateinit var mJsonString: String
    private lateinit var mEditTextNickname: EditText
    private lateinit var mEditTextEmail: EditText
    private lateinit var mEditTextPasswd: EditText
    private var email: String? = ""
    private var passwd: String? = ""
    private var nickname: String = ""
    private var possible_nickname: Boolean = true
    private var did_nickname_check: Boolean = false
    private var is_signup_btn: Boolean = false
    private var is_nickname_btn: Boolean = false
    private var possible_signup: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        nickname_check_btn.setOnClickListener {
            nickname_check()
        }

        signup_button.setOnClickListener {
            if(did_nickname_check){
                signup()
            }
            else{
                possible_signup = false
                showAlertDialog("FAIL","닉네임 중복 확인해주세요")
            }
        }

        cancle_btn.setOnClickListener {
            val intent = Intent(this@SignupActivity,LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun signup(){
        mEditTextEmail = findViewById(R.id.email_edittext_signup) as EditText
        mEditTextPasswd = findViewById(R.id.password_edittext_signup) as EditText

        if(nickname == mEditTextNickname.text.toString()){
            nickname = mEditTextNickname.text.toString()
            email = mEditTextEmail.text.toString()
            passwd = mEditTextPasswd.text.toString()

            if(email == ""){
                showAlertDialog("FAIL","이메일을 입력해주세요")
                return
            }
            else if(passwd == ""){
                showAlertDialog("FAIL","비밀번호를 입력해주세요")
                return
            }

            is_signup_btn = true

            var task: SignupActivity.InsertData = InsertData()
            task.execute("http://" + IP_ADDRESS + "/insert.php", nickname, email, passwd)
        }
        else{
            possible_signup = false
            is_signup_btn = false
            showAlertDialog("FAIL","닉네임 중복 확인해주세요")
        }
    }

    fun nickname_check(){
        mEditTextNickname = findViewById(R.id.nickname_edittext) as EditText

        nickname = mEditTextNickname.text.toString()
        if(nickname == ""){
            possible_signup = false
            showAlertDialog("FAIL","닉네임을 입력하세요")
            return
        }

        is_nickname_btn = true
        possible_nickname = true

        var task: SignupActivity.InsertData = InsertData()
        task.execute("http://" + IP_ADDRESS + "/getjson.php", "")
    }

    fun showAlertDialog(title: String, message: String){
        val dlg: AlertDialog.Builder = AlertDialog.Builder(this@SignupActivity,
            android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        dlg.setTitle(title)
        dlg.setMessage(message)
        dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            if(possible_signup) {
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                finish()
            }})
        dlg.show()
    }

    inner private class InsertData : AsyncTask<String?, Void, String?>() {
        //lateinit var progressDialog: ProgressDialog
        var errorMessage: String = "Failed!"

        protected override fun onPreExecute(){
            super.onPreExecute()
        }

        protected override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if(is_signup_btn){
                possible_signup = true
                showAlertDialog("Signup Success", "Login and join our services!")
            }
            if(is_nickname_btn && !is_signup_btn){
                if(result != null) {
                    if (result != "") {
                        mJsonString = result
                        checkUser()
                    } else {
                        possible_nickname = true
                        showAlertDialog("SUCCESS", "사용가능한 닉네임입니다!")
                        did_nickname_check = true
                    }
                }
            }
        }

        override fun doInBackground(vararg p0: String?): String? {
            var serverURL: String? = p0[0]
            var nickname: String? = ""
            var email: String? = ""
            var passwd: String? = ""
            var postParameters: String? = ""

            if(is_signup_btn){
                nickname = p0[1]
                email = p0[2]
                passwd = p0[3]
                postParameters = "nickname=" + nickname + "&email=" + email + "&passwd=" + passwd
            }

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

        private fun checkUser() {
            val TAG_JSON: String = "hyuna"
            val TAG_NICKNAME = "nickname"

            try{
                var jsonObject: JSONObject = JSONObject(mJsonString)
                var jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)

                for(i in 0 until jsonArray.length()){
                    val item: JSONObject = jsonArray.getJSONObject(i)

                    val Jnickname: String = item.getString(TAG_NICKNAME)

                    if(nickname == Jnickname){
                        possible_nickname = false
                        possible_signup = false
                        showAlertDialog("FAIL","사용할 수 없는 닉네임입니다")
                        return
                    }
                }

                if(possible_nickname){
                    showAlertDialog("SUCCESS", "사용가능한 닉네임입니다!")
                    did_nickname_check = true
                }

            } catch(e: java.lang.Exception){
                Log.d("Test", "showResult : ", e)
            }
        }

    }

}