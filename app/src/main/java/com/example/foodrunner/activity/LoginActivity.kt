package com.example.foodrunner.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var tvForgotPassword: TextView
    lateinit var tvSignUpNow: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        sharedPreferences = getSharedPreferences("Food Runner Preferences", MODE_PRIVATE)
        etMobileNumber = findViewById(R.id.etMobileNumberLogin)
        etPassword = findViewById(R.id.etPasswordLogin)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvSignUpNow = findViewById(R.id.tvSignUp)
        progressLayout = findViewById(R.id.progressLayoutLogin)
        progressBar = findViewById(R.id.progressBarLogin)

        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE

        if (!ConnectionManager().checkConnection(this@LoginActivity)) {
            val dialog = AlertDialog.Builder(this@LoginActivity)
            dialog.setTitle("FoodRunner")
            dialog.setMessage("No Internet Connection")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
                finish()
            }
            dialog.setNegativeButton("EXIT") { text, listener ->
                ActivityCompat.finishAffinity(this@LoginActivity)
            }
            dialog.setOnDismissListener {
                finish()
            }
            dialog.create()
            dialog.show()
        }

        if (sharedPreferences.getBoolean("is_login", false)) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("mobile", sharedPreferences.getString("mobile_number", null))
            intent.putExtra("password", sharedPreferences.getString("password", null))
            startActivity(intent)
            finish()
        } else {
            //do nothing
        }



        btnLogin.setOnClickListener {

            if (etMobileNumber.text.toString().length != 10) {
                Toast.makeText(
                    this@LoginActivity,
                    "Mobile number should be 10 digits!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (etPassword.text.toString().length < 4) {
                Toast.makeText(
                    this@LoginActivity,
                    "Password number should longer than 4 characters!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val url = "http://13.235.250.119/v2/login/fetch_result/"
                val queue = Volley.newRequestQueue(this@LoginActivity)

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etMobileNumber.text.toString())
                jsonParams.put("password", etPassword.text.toString())

                val jsonObjectRequest = object :
                    JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                        try {

                            progressLayout.visibility = View.VISIBLE
                            progressBar.visibility = View.VISIBLE

                            val jsonResponse = it.getJSONObject("data")
                            if (jsonResponse.getBoolean("success")) {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)

                                val data = jsonResponse.getJSONObject("data")
                                sharedPreferences.edit().putBoolean("is_login", true).apply()
                                sharedPreferences.edit()
                                    .putString("user_id", data.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name", data.getString("name"))
                                    .apply()
                                sharedPreferences.edit().putString("email", data.getString("email"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("mobile_number", data.getString("mobile_number"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("address", data.getString("address")).apply()

                                startActivity(intent)
                                finish()
                            } else {

                                progressLayout.visibility = View.GONE
                                progressBar.visibility = View.GONE

                                Toast.makeText(
                                    this@LoginActivity,
                                    "Enter valid credentials",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers
                    }
                }

                queue.add(jsonObjectRequest)
            }


        }

        tvForgotPassword.setOnClickListener {
            //intent to forgot password
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        tvSignUpNow.setOnClickListener {
            //intent to sign up
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        etMobileNumber.text = null
        etPassword.text = null
    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this@LoginActivity)
        super.onBackPressed()
    }

}