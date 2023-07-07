package com.example.foodrunner.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etMobileNumber: EditText
    lateinit var etEmailAddress: EditText
    lateinit var btnNext: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        title = "Reset Password"

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmailAddress = findViewById(R.id.etEmailAddress)
        btnNext = findViewById(R.id.btnNext)

        progressLayout = findViewById(R.id.progressLayoutForgot)
        progressBar = findViewById(R.id.progressBarForgot)

        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE

        btnNext.setOnClickListener {

            if (etEmailAddress.text.toString().isEmpty() || etMobileNumber.text.toString()
                    .isEmpty()
            ) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Enter all the details",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (etMobileNumber.text.toString().length != 10) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Mobile number must be 10 digits long",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                progressLayout.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE

                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etMobileNumber.text.toString())
                jsonParams.put("email", etEmailAddress.text.toString())

                val jsonObjectRequest = object :
                    JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                        try {
                            val jsonResponse = it.getJSONObject("data")

                            if (jsonResponse.getBoolean("success")) {

                                progressLayout.visibility = View.GONE
                                progressBar.visibility = View.GONE

                                if (!jsonResponse.getBoolean("first_try")) {
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "OTP has already been sent to your email address!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "OTP sent to your email address!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                val intent =
                                    Intent(this@ForgotPasswordActivity, ResetPassword::class.java)
                                intent.putExtra("mobile_number", etMobileNumber.text.toString())
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Enter valid credentials",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
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

    override fun onResume() {
        etEmailAddress.text = null
        etMobileNumber.text = null
        super.onResume()
    }
}