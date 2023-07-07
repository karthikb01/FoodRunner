package com.example.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.HomeAdapter
import com.example.foodrunner.model.Restaurants
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*

class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var homeAdapter: HomeAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    val resList = arrayListOf<Restaurants>()


    private val costComparator = Comparator<Restaurants> { res1, res2 ->
//            res1.resCostForOne.compareTo(res2.resCostForOne,ignoreCase = true)
        if (res1.resCostForOne.compareTo(res2.resCostForOne, ignoreCase = true) == 0) {
            res1.resName.compareTo(res2.resName, true)
        } else {
            res1.resCostForOne.compareTo(res2.resCostForOne, true)
        }
    }

    private val ratingComparator = Comparator<Restaurants> { res1, res2 ->
        res1.resRating.compareTo(res2.resRating, ignoreCase = true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayoutHome)
        progressBar = view.findViewById(R.id.progressBarHome)

        setHasOptionsMenu(true)


//        homeAdapter = HomeAdapter(activity as Context, resList)
//        recyclerView.adapter = homeAdapter
//        recyclerView.layoutManager = layoutManager

        if (ConnectionManager().checkConnection(activity as Context)) {
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {
                        val jsonObjectResponse = it.getJSONObject("data")
                        if (jsonObjectResponse.getBoolean("success")) {

                            progressBar.visibility = View.GONE
                            progressLayout.visibility = View.GONE


                            val jsonArray = jsonObjectResponse.getJSONArray("data")

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                val res = Restaurants(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("name"),
                                    jsonObject.getString("rating"),
                                    jsonObject.getString("cost_for_one"),
                                    jsonObject.getString("image_url")
                                )

                                resList.add(res)

                                val imageUrl = jsonObject.getString("image_url").toString()


                            }

                            homeAdapter = HomeAdapter(activity as Context, resList)
                            recyclerView.adapter = homeAdapter
                            recyclerView.layoutManager = layoutManager

                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(activity as Context, "Error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(activity as Context, "Error occurred", Toast.LENGTH_SHORT).show()
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection!!")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context?.startActivity(intent)
                activity?.finish()
            }
            dialog.setNegativeButton("EXIT") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.setOnDismissListener {
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_restaurant_sort, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.restaurant_sort_asc -> {
                Collections.sort(resList, costComparator)
            }
            R.id.restaurant_sort_desc -> {
                Collections.sort(resList, costComparator)
                resList.reverse()
            }
            R.id.restaurant_sort_rating -> {
                Collections.sort(resList, ratingComparator)
                resList.reverse()
            }
        }

        homeAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}