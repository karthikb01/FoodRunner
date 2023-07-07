package com.example.foodrunner.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import org.json.JSONException
import org.json.JSONObject

class ResetPassword : AppCompatActivity() {

    lateinit var etOtp: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOtp = findViewById(R.id.etOtp)
        etPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmNewPassword)
        btnSubmit = findViewById(R.id.btnSubmit)
        sharedPreferences = getSharedPreferences("Food Runner Preferences", MODE_PRIVATE)

        btnSubmit.setOnClickListener {
            if (etPassword.text.toString().length < 6) {
                Toast.makeText(
                    this@ResetPassword,
                    "Password should be longer 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!etPassword.text.toString().equals(etConfirmPassword.text.toString())) {
                Toast.makeText(
                    this@ResetPassword,
                    "Passwords should match",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val queue = Volley.newRequestQueue(this@ResetPassword)

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", intent.getStringExtra("mobile_number"))
                jsonParams.put("password", etPassword.text.toString())
                jsonParams.put("otp", etOtp.text.toString())

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParams,
                    Response.Listener {
                        try {
                            val jsonResponse = it.getJSONObject("data")
                            if (jsonResponse.getBoolean("success")) {
//                                sharedPreferences.edit().putString("password",etPassword.text.toString()).apply()
                                Toast.makeText(
                                    this@ResetPassword,
                                    jsonResponse.getString("successMessage"),
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this@ResetPassword, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@ResetPassword,
                                    "Error occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@ResetPassword,
                                "Error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@ResetPassword,
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
    }
}