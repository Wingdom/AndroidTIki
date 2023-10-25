package com.wingdom.androidcocktails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddCocktailActivity : AppCompatActivity() {

    private lateinit var db: CocktailDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cocktail)

        db = CocktailDbHelper.getInstance(applicationContext)

        findViewById<Button>(R.id.button_save).setOnClickListener {
            saveCocktail()
        }
    }

    private fun saveCocktail() {
        val name = findViewById<EditText>(R.id.edit_text_name).text.toString().trim()
        val ingredients = findViewById<EditText>(R.id.edit_text_ingredients).text.toString().trim()
        val instructions = findViewById<EditText>(R.id.edit_text_instructions).text.toString().trim()

        if (name.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        db.addCocktail(name, ingredients, instructions)

        Toast.makeText(this, "Cocktail saved", Toast.LENGTH_SHORT).show()
        // Retrieve updated list of cocktails from the database
        val db = CocktailDbHelper.getInstance(applicationContext)

        // Pass the updated list of cocktails back to MainActivity
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
