package com.wingdom.androidcocktails

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wingdom.androidcocktails.Cocktail

class CocktailDetailActivity : AppCompatActivity() {

    private var isEditMode = false // Variable to track edit mode
    private var firstEdit = 0
    private var cocktailName: String? = null
    private lateinit var db: CocktailDbHelper

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cocktail_detail)
        db = CocktailDbHelper.getInstance(this)

        val cocktailNameTextView = findViewById<TextView>(R.id.textViewCocktailName)
        val ingredientsTextView = findViewById<EditText>(R.id.editTextIngredientsDetails)
        val instructionsTextView = findViewById<EditText>(R.id.editTextInstructionsDetails)
        val editButton = findViewById<FloatingActionButton>(R.id.floating_button_add)

        // Set the initial state of UI elements
        toggleEditMode(isEditMode)

        // Retrieve the cocktail ID from the intent
        val cocktailId = intent.getLongExtra("cocktailId", -1) // Use a default value if not found

        // Use the cocktailId to fetch the corresponding Cocktail from the database
        val cocktail = db.getCocktailById(cocktailId) // Replace with your database retrieval logic

        // Display the cocktail details in the activity
        cocktail?.let {
            cocktailName = it.name //Set the cocktail name here, so it can be included with the update when saving
            cocktailNameTextView.setText(it.name)
            ingredientsTextView.setText(it.ingredients)
            //format the \\n to a real line break in displayed text
            val formattedInstructions = it.instructions.replace("\\n", "\n")
            instructionsTextView.setText(formattedInstructions)
        }

        // Add a click listener to the "Edit" button
        editButton.setOnClickListener {
            if (isEditMode) {
                // Handle the click to save the edits
                // You can save the updated data to the database here
                // After saving, switch back to view mode
                isEditMode = false
                toggleEditMode(isEditMode)
            } else {
                // Switch to edit mode
                isEditMode = true
                toggleEditMode(isEditMode)
            }
        }
    }

    // Function to update UI elements based on edit mode
    private fun toggleEditMode(editMode: Boolean) {
        val ingredientsTextView = findViewById<EditText>(R.id.editTextIngredientsDetails)
        val instructionsTextView = findViewById<EditText>(R.id.editTextInstructionsDetails)
        val editButton = findViewById<FloatingActionButton>(R.id.floating_button_add)

        if (editMode) {
            firstEdit = 1
            // Enable text fields for editing
            ingredientsTextView.isEnabled = true
            instructionsTextView.isEnabled = true
            editButton.setImageResource(R.drawable.ic_save) // Change button icon to "Save"
        } else if(!editMode && firstEdit == 1) {
            // Disable text fields for viewing
            ingredientsTextView.isEnabled = false
            instructionsTextView.isEnabled = false
            editButton.setImageResource(R.drawable.ic_edit) // Change button icon back to "Edit"

            // Save changes to the database
            val cocktailId = intent.getLongExtra("cocktailId", -1)
            if (cocktailId != -1L) {
                val updatedName = cocktailName ?: "Cocktail Name Save Error" // Use the stored name or an empty string if not available
                val ingredients = ingredientsTextView.text.toString()
                val instructions = instructionsTextView.text.toString().replace("\n", "\\n") // Save new lines as "\\n"

                // Update the cocktail in the database
                db.updateCocktail(cocktailId, updatedName, ingredients, instructions)
            }

            // Apply formatting when switching back to view mode
            val formattedInstructions = instructionsTextView.text.toString().replace("\\n", "\n")
            instructionsTextView.setText(formattedInstructions, TextView.BufferType.SPANNABLE)
        } else {
            Log.i("Cocktail Detail Activity", "Something went wrong loading the cocktail to edit, loading in read only mode")
            // Disable text fields for viewing
            ingredientsTextView.isEnabled = false
            instructionsTextView.isEnabled = false
            editButton.setImageResource(R.drawable.ic_edit) // Change button icon back to "Edit"
            // Apply formatting when switching back to view mode
            val formattedInstructions = instructionsTextView.text.toString().replace("\\n", "\n")
            instructionsTextView.setText(formattedInstructions, TextView.BufferType.SPANNABLE)
        }
    }
}