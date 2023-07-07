package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.example.foodrunner.adapter.CartAdapter
import com.example.foodrunner.database.cart.CartDatabase
import com.example.foodrunner.database.cart.CartEntity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartPageActivity : AppCompatActivity() {

    lateinit var tvInfo: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var btnPlaceOrder: Button
    lateinit var adapter: CartAdapter
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayout: RelativeLayout

    var cartList = listOf<CartEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_page)

        if (intent == null)
            finish()

//        if (!sharedPreferences.getBoolean("is_login", false))
//            finish()

        val res = intent.getStringExtra("res_id")


        tvInfo = findViewById(R.id.tvRestaurantName)
        recyclerView = findViewById(R.id.recyclerCart)
        layoutManager = LinearLayoutManager(this@CartPageActivity)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        toolbar = findViewById(R.id.toolbar)
        sharedPreferences = getSharedPreferences("Food Runner Preferences", MODE_PRIVATE)
        progressLayout = findViewById(R.id.progressLayout)

        if (!sharedPreferences.getBoolean("is_login", false))
            finish()

        progressLayout.visibility = View.GONE

        setUpToolbar()

        cartList = DBASyncTask(this@CartPageActivity, 1).execute().get()


        adapter = CartAdapter(this@CartPageActivity, cartList)

        var total = 0
        for (i in 0 until cartList.size)
            total += cartList[i].itemCostForOne.toInt()

        btnPlaceOrder.text = "Place Order(Total: Rs.${total})"

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        btnPlaceOrder.setOnClickListener {
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            val queue = Volley.newRequestQueue(this@CartPageActivity)

            val jsonParams = JSONObject()
            jsonParams.put("user_id", sharedPreferences.getString("user_id", null).toString())
            jsonParams.put("restaurant_id", res.toString())
            jsonParams.put("total_cost", total.toString())

            val itemsList = JSONArray()
            for (i in 0 until cartList.size) {
                val item = JSONObject()
                item.put("food_item", cartList[i].itemId)

                itemsList.put(item)
            }

            jsonParams.put("food", itemsList)
            println("jsonParams $jsonParams")


            val jsonRequestObject =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val jsonResponse = it.getJSONObject("data")
                        if (jsonResponse.getBoolean("success")) {
//                            Toast.makeText(
//                                this@CartPageActivity,
//                                "Order Placed",
//                                Toast.LENGTH_SHORT
//                            ).show()
                            progressLayout.visibility = View.VISIBLE

                            DBASyncTask(this@CartPageActivity, 2).execute()

                            Handler().postDelayed({
                                val intent = Intent(this@CartPageActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, 2000)

                        } else {
                            Toast.makeText(
                                this@CartPageActivity,
                                "Error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@CartPageActivity,
                            "Error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this@CartPageActivity,
                        "Error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers
                    }
                }

            queue.add(jsonRequestObject)
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "My Cart"
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }


    class DBASyncTask(val context: Context, val mode: Int) :
        AsyncTask<Void, Void, List<CartEntity>>() {

        val db = Room.databaseBuilder(context, CartDatabase::class.java, "cart_items").build()

        override fun doInBackground(vararg p0: Void?): List<CartEntity> {
            when (mode) {
                1 -> {
                    val cartList = db.CartDao().getAllItems()
                    db.close()
                    return cartList
                }
                2 -> {
                    db.CartDao().deleteAllItems()
                    db.close()
                    return emptyList()
                }
            }
            return emptyList()
        }

    }
}