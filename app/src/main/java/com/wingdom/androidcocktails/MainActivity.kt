package com.wingdom.androidcocktails

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wingdom.androidcocktails.Cocktail

class MainActivity : AppCompatActivity() {

    private lateinit var db: CocktailDbHelper
    private lateinit var adapter: CocktailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize database and adapter
        Log.i("Main", "Initialising database and adapter")
        db = CocktailDbHelper.getInstance(this)
        Log.i("Main", "db initialized")
        adapter = CocktailAdapter(this, db.getAllCocktails())
        Log.i("Main", "adapter initialized")

        // Assign the ListView to a variable
        val listView = findViewById<ListView>(R.id.list_view_cocktails)

        // Set adapter on ListView
        listView.adapter = adapter

        // Set an item click listener on the ListView
        listView.setOnItemClickListener { _, _, position, _ ->
            val cocktailId = adapter.getItemId(position)
            val intent = Intent(this, CocktailDetailActivity::class.java)
            intent.putExtra("cocktailId", cocktailId) // Pass the cocktail ID
            startActivity(intent)
        }

        // Set click listener on Floating Action Button to launch AddCocktailActivity
        findViewById<FloatingActionButton>(R.id.floating_button_add).setOnClickListener {
            val intent = Intent(this, AddCocktailActivity::class.java)
            startActivityForResult(intent, ADD_COCKTAIL_REQUEST_CODE)
        }

        val searchView = findViewById<SearchView>(R.id.search_view_cocktails)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Only Using onQueryTextChange
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_nav_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Open the navigation drawer
                findViewById<DrawerLayout>(R.id.drawer_layout).openDrawer(GravityCompat.START)
                return true
            }
            R.id.menu_item_settings -> {
                // Open the SettingsActivity
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_COCKTAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh adapter with new data from database
            adapter.changeCursor(db.getAllCocktails())
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        const val ADD_COCKTAIL_REQUEST_CODE = 1
    }
}

