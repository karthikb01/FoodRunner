package com.example.foodrunner.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

class RegisterActivity : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmailAddress: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDeliverAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = "Register Yourself"


        etName = findViewById(R.id.etName)
        etEmailAddress = findViewById(R.id.etEmailAddress)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDeliverAddress = findViewById(R.id.etDeliverAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        if (!ConnectionManager().checkConnection(this@RegisterActivity)) {
            val dialog = AlertDialog.Builder(this@RegisterActivity)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection!!")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
                finish()
            }
            dialog.setNegativeButton("EXIT") { text, listener ->
                ActivityCompat.finishAffinity(this@RegisterActivity)
            }
            dialog.setOnDismissListener {
                finish()
            }
            dialog.create()
            dialog.show()
        }

        btnRegister.setOnClickListener {
            //intent to register info
            if (etConfirmPassword.text.toString().isNotEmpty() && etDeliverAddress.text.toString()
                    .isNotEmpty() && etEmailAddress.text.toString()
                    .isNotEmpty() && etMobileNumber.text.toString()
                    .isNotEmpty() && etName.text.toString()
                    .isNotEmpty() && etPassword.text.toString().isNotEmpty()
            ) {
                if (etName.text.toString().length < 3) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Name should be longer 3 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etPassword.text.toString().length < 4) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Password should be longer 4 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etConfirmPassword.text.toString().equals(etPassword.text.toString())) {
                    //passwords  match

                    if (etMobileNumber.text.toString().length == 10) {
//                            mobile number is 10 digits long

                        val name = etName.text.toString()
                        val email = etEmailAddress.text.toString()
                        val mobile = etMobileNumber.text.toString()
                        val address = etDeliverAddress.text.toString()
                        val password = etPassword.text.toString()

                        val sharedPreferences =
                            getSharedPreferences("Food Runner Preferences", MODE_PRIVATE)


                        val queue = Volley.newRequestQueue(this@RegisterActivity)
                        val url = "http://13.235.250.119/v2/register/fetch_result"

                        val params = JSONObject()
                        params.put("name", name)
                        params.put("mobile_number", mobile)
                        params.put("password", password)
                        params.put("address", address)
                        params.put("email", email)


                        val jsonObjectRequest = object : JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            params,
                            Response.Listener {
                                try {
                                    val jsonResponse = it.getJSONObject("data")
                                    if (jsonResponse.getBoolean("success")) {
                                        val data = jsonResponse.getJSONObject("data")

                                        sharedPreferences.edit()
                                            .putString("user_id", data.getString("user_id")).apply()
                                        sharedPreferences.edit()
                                            .putString("name", data.getString("name")).apply()
                                        sharedPreferences.edit()
                                            .putString("email", data.getString("email"))
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString(
                                                "mobile_number",
                                                data.getString("mobile_number")
                                            ).apply()
                                        sharedPreferences.edit()
                                            .putString("address", data.getString("address"))
                                            .apply()
//                                        sharedPreferences.edit().putString("password", password)
//                                            .apply()


                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Registered",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        sharedPreferences.edit().putBoolean("is_login", true)
                                            .apply()
                                        val intent =
                                            Intent(this@RegisterActivity, MainActivity::class.java)
                                        startActivity(intent)

                                    } else {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Parsing error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "JsonException",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Volley error",
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

                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Mobile number should be 10 digits",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Passwords should match",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            } else {
                Toast.makeText(this@RegisterActivity, "Enter all the details", Toast.LENGTH_SHORT)
                    .show()
            }

        }


    }


//    override fun onBackPressed() {
//        val intent = Intent(this@RegisterActivity,LoginActivity::class.java)
//        startActivity(intent)
//        super.onBackPressed()
//    }

    override fun onResume() {
        etName.text = null
        etConfirmPassword.text = null
        etPassword.text = null
        etDeliverAddress.text = null
        etMobileNumber.text = null
        etEmailAddress.text = null
        super.onResume()
    }

    override fun onPause() {
        finish()
        super.onPause()
    }


}