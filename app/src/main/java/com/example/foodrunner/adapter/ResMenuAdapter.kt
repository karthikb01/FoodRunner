package com.example.foodrunner.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.database.cart.CartDatabase
import com.example.foodrunner.database.cart.CartEntity
import com.example.foodrunner.model.ResMenu

class ResMenuAdapter(val context: Context, private val itemList: List<ResMenu>) :
    RecyclerView.Adapter<ResMenuAdapter.ResMenuViewHolder>() {

    class ResMenuViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.tvItemName)
        val itemPrice: TextView = view.findViewById(R.id.tvItemPrice)
        val slNo: TextView = view.findViewById(R.id.tvSlNo)
        val addToCart: Button = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResMenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_res_details_single_row, parent, false)
        return ResMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResMenuViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemName.text = item.name
        holder.itemPrice.text = item.cost_for_one
        holder.slNo.text = (position+1).toString()

        var selected: Boolean = false

        holder.addToCart.setOnClickListener {


            val cartEntity = CartEntity(
                item.restaurant_id,
                item.id,
                item.name,
                item.cost_for_one
            )

            if (selected) {
                if (DBAsyncTask(context, cartEntity, 2).execute().get())
                    holder.addToCart.text = "Add "
                selected = !selected
            } else {
                if (DBAsyncTask(context, cartEntity, 1).execute().get()) {
                    holder.addToCart.text = "Remove"
                }
                selected = !selected
            }

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class DBAsyncTask(val context: Context, private val cartEntity: CartEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        private val db =
            Room.databaseBuilder(context, CartDatabase::class.java, "cart_items").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.CartDao().insert(cartEntity)
                    db.close()
                    return true
                }
                2 -> {
                    db.CartDao().delete(cartEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}