package com.example.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.activity.ResDetailsActivity
import com.example.foodrunner.database.restaurant.ResDatabase
import com.example.foodrunner.database.restaurant.ResEntity
import com.example.foodrunner.model.Restaurants
import com.squareup.picasso.Picasso


class HomeAdapter(val context: Context, private val itemList: List<Restaurants>) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resName: TextView = view.findViewById(R.id.tvRestaurantName)
        val resPrice: TextView = view.findViewById(R.id.tvPrice)
        val resRating: TextView = view.findViewById(R.id.tvRating)
        val resImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val resAddToFav: ImageView = view.findViewById(R.id.tvAddToFavHome)
        val liContent: RelativeLayout = view.findViewById(R.id.liContentHome)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }


    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val res = itemList[position]
        holder.resName.text = res.resName
        holder.resPrice.text = "$ ${res.resCostForOne}"
        holder.resRating.text = res.resRating

        Picasso.get().load(res.resImage).error(R.drawable.app_logo).into(holder.resImage)

        val resEntity = ResEntity(
            res.resId.toInt(),
            res.resName,
            res.resRating,
            res.resCostForOne,
            res.resImage
        )

        if (DBAsyncTask(context, resEntity, 1).execute().get()) {

            val favColor = ContextCompat.getColor(context, R.color.purple_200)
            holder.resAddToFav.setImageResource(R.drawable.ic_fav_fill)
        } else {

            val favColor = ContextCompat.getColor(context, R.color.purple_200)
            holder.resAddToFav.setImageResource(R.drawable.ic_fav)
        }


        holder.resAddToFav.setOnClickListener {
            if (DBAsyncTask(context, resEntity, 1).execute().get()) {
                //in fav
                if (DBAsyncTask(context, resEntity, 3).execute().get()) {
                    Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT)
                        .show()
                    val favColor = ContextCompat.getColor(context, R.color.purple_200)
                    holder.resAddToFav.setImageResource(R.drawable.ic_fav)


                } else {
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT)
                        .show()

                }
            } else {
                //not in fav
                if (DBAsyncTask(context, resEntity, 2).execute().get()) {
                    Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT)
                        .show()

                    val favColor = ContextCompat.getColor(context, R.color.purple_200)
                    holder.resAddToFav.setImageResource(R.drawable.ic_fav_fill)
                } else {
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


        holder.liContent.setOnClickListener {
            val intent = Intent(context, ResDetailsActivity::class.java)
            intent.putExtra("res_id", res.resId)
            intent.putExtra("name", res.resName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    class DBAsyncTask(
        val context: Context,
        private val resEntity: ResEntity,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {
        private val db = Room.databaseBuilder(context, ResDatabase::class.java, "res_list").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val res = db.resDao().getResById(resEntity.resId.toString())
                    db.close()
                    return res != null
                }
                2 -> {
                    db.resDao().insertRes(resEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.resDao().deleteRes(resEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

}