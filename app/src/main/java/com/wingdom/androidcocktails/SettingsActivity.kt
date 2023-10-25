package com.wingdom.androidcocktails

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val resetButton = findViewById<Button>(R.id.button_reset_database)
        resetButton.setOnClickListener {
            // Erase the database
            val db = CocktailDbHelper.getInstance(this)
            db.resetDatabase()

            // Start the main activity
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}
