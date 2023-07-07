package com.example.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.R
import com.example.foodrunner.model.Order

class OrdersAdapter(val context: Context, private val itemList: List<Order>) :
    RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    class OrdersViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvRestaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val recyclerItems: RecyclerView = view.findViewById(R.id.recyclerItems)
        val tvTotalPrice: TextView = view.findViewById(R.id.tvTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_orders_single_row, parent, false)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = itemList[position]
        val layoutManager = LinearLayoutManager(context)
        holder.tvRestaurantName.text = order.resName
        holder.tvOrderDate.text = order.date

        holder.recyclerItems.adapter = ItemsAdapter(context, order.foodItems)
        holder.recyclerItems.layoutManager = layoutManager

        holder.tvTotalPrice.text = "Total: ${order.totalCost}"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}