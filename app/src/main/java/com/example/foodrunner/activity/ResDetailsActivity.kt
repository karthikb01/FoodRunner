package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.ResMenuAdapter
import com.example.foodrunner.database.cart.CartDatabase
import com.example.foodrunner.model.ResMenu
import org.json.JSONException

class ResDetailsActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: ResMenuAdapter
    val itemsList = arrayListOf<ResMenu>()
    private lateinit var toolbar: Toolbar
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    private lateinit var btnProceedToCart: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_res_details)

        recyclerView = findViewById(R.id.recyclerResDetails)
        layoutManager = LinearLayoutManager(this@ResDetailsActivity)
        toolbar = findViewById(R.id.toolbar)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        btnProceedToCart = findViewById(R.id.btnGoToCart)

        val sharedPreferences = getSharedPreferences("Food Runner Preferences", MODE_PRIVATE)

        if (!sharedPreferences.getBoolean("is_login", false))
            finish()

        println("Is login ${sharedPreferences.getBoolean("is_login", false)}")

        adapter = ResMenuAdapter(this@ResDetailsActivity, itemsList)


        if (intent == null)
            finish()
        val resId = intent.getStringExtra("res_id")

        setUpToolbar()
        supportActionBar?.title = intent.getStringExtra("name")

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/${resId}"

        val queue = Volley.newRequestQueue(this@ResDetailsActivity)

        val jsonObjectRequest =
            object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                try {
                    val jsonResponse = it.getJSONObject("data")
                    if (jsonResponse.getBoolean("success")) {

                        progressBar.visibility = View.GONE
                        progressLayout.visibility = View.GONE

                        val menu = jsonResponse.getJSONArray("data")

                        for (i in 0 until menu.length()) {
                            val item = menu.getJSONObject(i)

                            val menuItem = ResMenu(
                                item.getString("id").toString(),
                                item.getString("name").toString(),
                                item.getString("cost_for_one").toString(),
                                item.getString("restaurant_id").toString()
                            )

                            itemsList.add(menuItem)

                        }

                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = layoutManager


                    } else {
                        Toast.makeText(
                            this@ResDetailsActivity,
                            "Error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        this@ResDetailsActivity,
                        "Error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(
                    this@ResDetailsActivity,
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

        btnProceedToCart.setOnClickListener {
            if (DBAsyncTask(this@ResDetailsActivity, 2).execute().get()) {
                Toast.makeText(
                    this@ResDetailsActivity,
                    "Add items to the cart!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this@ResDetailsActivity, CartPageActivity::class.java)
                intent.putExtra("res_id", resId)
                startActivity(intent)
            }
        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onBackPressed() {
        if (DBAsyncTask(this@ResDetailsActivity, 1).execute().get()) {
            super.onBackPressed()
        } else {
            Toast.makeText(this@ResDetailsActivity, "Error occurred!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (DBAsyncTask(this@ResDetailsActivity, 1).execute().get()) {
                finish()
            } else {
                Toast.makeText(this@ResDetailsActivity, "Error occurred!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        DBAsyncTask(this@ResDetailsActivity, 1).execute()
        super.onStop()
    }

    class DBAsyncTask(val context: Context, private val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, CartDatabase::class.java, "cart_items").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.CartDao().deleteAllItems()
                    db.close()
                    return true
                }
                2 -> {
                    val list = db.CartDao().getAllItems()
                    db.close()
                    return list.isEmpty()
                }
            }
            return false
        }

    }
}