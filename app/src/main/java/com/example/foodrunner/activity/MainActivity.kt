package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.database.restaurant.ResDatabase
import com.example.foodrunner.database.restaurant.ResEntity
import com.example.foodrunner.fragment.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var frameLayout: FrameLayout
    lateinit var tvPhone: TextView
    lateinit var tvName: TextView
    private var resList = listOf<ResEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)
        frameLayout = findViewById(R.id.frameLayout)

        val sharedPreferences = getSharedPreferences("Food Runner Preferences", MODE_PRIVATE)

        if (!sharedPreferences.getBoolean("is_login", false))
            finish()



        setUpToolbar()

        if (intent != null) {

            openHome()
        } else {
            finish()
        }


        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout, R.string.open_drawer, R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        var previousItem: MenuItem? = null

        val header = navigationView.getHeaderView(0)
        tvPhone = header.findViewById(R.id.drawerPhone)
        tvName = header.findViewById(R.id.drawerName)
        tvName.text = sharedPreferences.getString("name", "Name")
        tvPhone.text = "+91-${sharedPreferences.getString("mobile_number", "9999999999")}"

        navigationView.setNavigationItemSelectedListener {
            if (previousItem != null)
                previousItem?.isChecked = false
            it.isCheckable = true
            it.isChecked = true
            previousItem = it

            when (it.itemId) {
                R.id.item_home -> {
                    openHome()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ProfileFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Profile"
                }
                R.id.favourites -> {
//                    if (!openFav()) {
//                        it.isChecked = false
//                        previousItem?.isCheckable = true
//                        previousItem?.isChecked = true
//                    }
                    openFav()
                }
                R.id.orders -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, OrdersFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Orders"
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FaqFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "FAQ"
                }
                R.id.logout -> {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Confirm")
                    dialog.setMessage("Are you sure to Logout?")
                    dialog.setPositiveButton("Logout") { text, listener ->
                        val sharedPreferences = getSharedPreferences(
                            "Food Runner Preferences",
                            MODE_PRIVATE
                        )
                        sharedPreferences.edit().remove("user_id").apply()
                        sharedPreferences.edit().remove("is_login").apply()
                        sharedPreferences.edit().remove("name").apply()
                        sharedPreferences.edit().remove("email").apply()
                        sharedPreferences.edit().remove("address").apply()
                        sharedPreferences.edit().remove("mobile_number").apply()

                        println("Is login ${sharedPreferences.getBoolean("is_login", false)}")

                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("Cancel") { text, listener ->

                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START)
        return super.onOptionsItemSelected(item)
    }

    fun openHome() {
        navigationView.setCheckedItem(R.id.item_home)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, HomeFragment())
            .commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "Home"
    }

    private fun openFav() {
        navigationView.setCheckedItem(R.id.favourites)
        supportActionBar?.title = "Favourites"
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, FavouritesFragment())
            .commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "Favourites"

    }


    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frameLayout)) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }

    class DBAsyncTask(
        val context: Context,
        private var resList: List<ResEntity>,
        private val mode: Int
    ) :
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