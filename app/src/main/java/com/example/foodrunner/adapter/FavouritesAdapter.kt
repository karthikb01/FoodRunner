package com.example.foodrunner.adapter


import android.app.AlertDialog
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
import com.example.foodrunner.activity.MainActivity
import com.example.foodrunner.activity.ResDetailsActivity
import com.example.foodrunner.database.restaurant.ResDatabase
import com.example.foodrunner.database.restaurant.ResEntity
import com.squareup.picasso.Picasso

class FavouritesAdapter(val context: Context, private var itemList: MutableList<ResEntity>) :
    RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {

    class FavouritesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val resName: TextView = view.findViewById(R.id.tvRestaurantName)
        val resPrice: TextView = view.findViewById(R.id.tvPrice)
        val resRating: TextView = view.findViewById(R.id.tvRating)
        val resAddToFav: ImageView = view.findViewById(R.id.tvAddToFav)
        val liContent: RelativeLayout = view.findViewById(R.id.liContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_fav_single_row, parent, false)

        return FavouritesViewHolder(view)
    }


    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        val res = itemList[position]
        holder.resName.text = res.resName
        holder.resRating.text = res.resRating
        holder.resPrice.text = "$ ${res.resCostForOne}"


        Picasso.get().load(res.resImage).error(R.drawable.app_logo).into(holder.resImage)

        holder.resAddToFav.setOnClickListener {

            val resEntity = ResEntity(
                res.resId.toInt(),
                res.resName,
                res.resRating,
                res.resCostForOne,
                res.resImage
            )


            if (DBAsyncTask(context, resEntity).execute().get()) {

                val favColor = ContextCompat.getColor(context, R.color.purple_200)
                holder.resAddToFav.setImageResource(R.drawable.ic_fav)

                notifyItemRemoved(position)
                itemList.removeAt(position)
                notifyItemRangeChanged(position, itemList.size)

                if (itemList.isEmpty()) {
                    val dialog = AlertDialog.Builder(context)
                    dialog.setTitle("Foor Runner")
                    dialog.setMessage("Favourites is Empty!!")
                    dialog.setPositiveButton("Ok") { text, listener ->
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                    dialog.setOnDismissListener {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                    dialog.create()
                    dialog.show()
                }

            } else {
                Toast.makeText(context, "Could not remove", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        holder.liContent.setOnClickListener {
            //click listener for restaurants
            val intent = Intent(context, ResDetailsActivity::class.java)
            intent.putExtra("res_id", res.resId.toString())
            intent.putExtra("name", res.resName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    class DBAsyncTask(val context: Context, val resEntity: ResEntity?) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, ResDatabase::class.java, "res_list").build()
        override fun doInBackground(vararg p0: Void?): Boolean {


            db.resDao().deleteRes(resEntity as ResEntity)
            db.close()
            return true


            return false
        }

    }
}