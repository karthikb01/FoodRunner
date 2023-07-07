package com.example.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.adapter.FavouritesAdapter
import com.example.foodrunner.database.restaurant.ResDatabase
import com.example.foodrunner.database.restaurant.ResEntity

class FavouritesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager

    //     var tvAddToFav : TextView? = null
    private lateinit var favouritesAdapter: FavouritesAdapter

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var tvFavEmpty: TextView

    var resList = listOf<ResEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerView = view.findViewById(R.id.recyclerFav)
        layoutManager = LinearLayoutManager(activity as Context)
        tvFavEmpty = view.findViewById(R.id.tvFavEmpty)

        tvFavEmpty.visibility = View.GONE

        progressLayout = view.findViewById(R.id.progressLayoutFav)
        progressBar = view.findViewById(R.id.progressBarFav)


        resList = DBAsyncTask(activity as Context, resList, 1).execute().get()

        if (resList.isEmpty()) {
            tvFavEmpty.visibility = View.VISIBLE
        }


        favouritesAdapter =
            FavouritesAdapter(activity as Context, resList as MutableList<ResEntity>)

//        tvAddToFav = getView()?.findViewById(R.id.tvAddToFav)
//
//
//        tvAddToFav?.setOnClickListener {
//            Toast.makeText(activity as Context,"Clicked",Toast.LENGTH_SHORT).show()
//        }

        recyclerView.adapter = favouritesAdapter
        recyclerView.layoutManager = layoutManager

        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
        return view
    }


    class DBAsyncTask(val context: Context, private var resList: List<ResEntity>, val mode: Int) :
        AsyncTask<Void, Void, List<ResEntity>>() {
        private val db = Room.databaseBuilder(context, ResDatabase::class.java, "res_list").build()
        override fun doInBackground(vararg p0: Void?): List<ResEntity> {
            when (mode) {
                1 -> {
                    //retrieve all restaurants
                    resList = db.resDao().getAllRes()
                    db.close()
                    return resList
                }
            }
            return resList
        }

    }

}