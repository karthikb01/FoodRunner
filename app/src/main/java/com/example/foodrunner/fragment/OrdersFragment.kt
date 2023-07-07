package com.example.foodrunner.fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.OrdersAdapter
import com.example.foodrunner.model.Item
import com.example.foodrunner.model.Order
import org.json.JSONException

class OrdersFragment : Fragment() {

    lateinit var recyclerOrder: RecyclerView
    lateinit var layoutManagerOrder: LinearLayoutManager

    lateinit var ordersAdapter: OrdersAdapter

    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var orderList = arrayListOf<Order>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        sharedPreferences = context?.getSharedPreferences("Food Runner Preferences", MODE_PRIVATE)!!

        recyclerOrder = view.findViewById(R.id.recyclerOrders)
        layoutManagerOrder = LinearLayoutManager(activity)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)


        val url = "http://13.235.250.119/v2/orders/fetch_result/${
            sharedPreferences.getString(
                "user_id",
                null
            )
        }"
        val queue = Volley.newRequestQueue(activity as Context)

        val jsonObjectRequest =
            object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                try {
                    val jsonResponse = it.getJSONObject("data")

                    if (jsonResponse.getBoolean("success")) {

                        progressBar.visibility = View.GONE
                        progressLayout.visibility = View.GONE

                        val jsonArray = jsonResponse.getJSONArray("data")

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            val foodItems = jsonObject.getJSONArray("food_items")

                            val itemsList = arrayListOf<Item>()

                            for (i in 0 until foodItems.length()) {
                                val foodItem = foodItems.getJSONObject(i)
                                val item = Item(
                                    foodItem.getString("food_item_id"),
                                    foodItem.getString("name"),
                                    foodItem.getString("cost")
                                )
                                itemsList.add(item)

                            }


                            val order = Order(
                                jsonObject.getString("order_id"),
                                jsonObject.getString("restaurant_name"),
                                jsonObject.getString("order_placed_at"),
                                jsonObject.getString("total_cost"),
                                itemsList
                            )


                            orderList.add(order)

                            ordersAdapter = OrdersAdapter(activity as Context, orderList)
                            recyclerOrder.adapter = ordersAdapter
                            recyclerOrder.layoutManager = layoutManagerOrder

                            recyclerOrder.addItemDecoration(
                                DividerItemDecoration(
                                    activity as Context,
                                    layoutManagerOrder.orientation
                                )
                            )

                        }

                    } else {
                        Toast.makeText(
                            activity as Context,
                            "Error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        activity as Context,
                        "Error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(
                    activity as Context,
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

        queue.add(jsonObjectRequest)

        return view
    }


}