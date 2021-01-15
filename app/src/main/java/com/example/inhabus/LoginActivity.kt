package com.example.inhabus

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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
    private var email: String = ""
    private var passwd: String = ""
    private var nickname: String = ""
    private lateinit var input_nickname: String
    private lateinit var found_passwd: String
    private var isUser: Boolean = false
    private var isFindingPasswd: Boolean = false
    private var isfound_passwd: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email_login_button.setOnClickListener {
            signin()
        }
        signup_button.setOnClickListener {
            signup()
        }
        find_passwd_button.setOnClickListener {
            isFindingPasswd = true
            find_passwd()
        }
    }

    fun signin(){
        mEditemail = findViewById(R.id.email_edittext)
        mEditpasswd = findViewById(R.id.password_edittext)

        email = mEditemail.text.toString()
        passwd = mEditpasswd.text.toString()

        if(email == ""){
            showAlertDialog("FAIL","이메일을 입력하세요")
            return
        }
        else if(passwd == ""){
            showAlertDialog("FAIL","비밀번호를 입력하세요")
            return
        }

        var task: GetData = GetData()
        task.execute("http://" + IP_ADDRESS + "/getjson.php", "")
    }

    fun signup(){
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    fun find_passwd(){
        val vi = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val find_passwd_layout = vi.inflate(R.layout.dialog_nickname, null) as LinearLayout

        AlertDialog.Builder(this).setTitle("닉네임을 입력하세요").setView(find_passwd_layout)
            .setPositiveButton("확인", DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                input_nickname = (find_passwd_layout.findViewById(R.id.nickname_id) as EditText).text.toString()
                var task: GetData = GetData()
                task.execute("http://" + IP_ADDRESS + "/getjson.php", "")
            }).show()
    }

    fun showAlertDialog(title: String, message: String){
        val dlg: AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity,
            android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        dlg.setTitle(title)
        dlg.setMessage(message)
        dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            startActivity(Intent(this@LoginActivity,LoginActivity::class.java))
            finish()})
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
                mTextViewResult.text = errorMessage
            }
            else{
                if(result!=""){
                    mJsonString = result
                    checkUser()
                }
                else{
                    showAlertDialog("Login Fail", "Try again or create new account!")
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

                    //find user
                    if(!isFindingPasswd && email == Jemail && passwd == Jpasswd){
                        isUser = true
                        break
                    }
                    //find password
                    if(isFindingPasswd && input_nickname == Jnickname){
                        isfound_passwd = true
                        found_passwd = Jpasswd
                        break
                    }
                }

                if(isUser){
                    Log.d("Test", "Login Success!")
                    val intent = Intent(this@LoginActivity,MainActivity::class.java)
                    intent.putExtra("nickname",nickname)
                    startActivity(intent)
                    return
                }
                if(!isFindingPasswd){
                    showAlertDialog("Login Fail", "Try again or create new account!")
                    return
                }

                //비번을 찾았으면
                if(isfound_passwd){
                    showAlertDialog("Password Found", found_passwd)
                    return
                }

                showAlertDialog("No Account", "해당 닉네임과 일치하는 계정이 없습니다")
                isFindingPasswd = false
            } catch(e: java.lang.Exception){
                Log.d("Test", "showResult : ", e)
            }
        }
    }
}