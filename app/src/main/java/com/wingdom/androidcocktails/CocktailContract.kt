package com.wingdom.androidcocktails

import android.provider.BaseColumns

object CocktailContract {
    // Inner class that defines the table contents
    object CocktailEntry : BaseColumns {
        const val _ID = BaseColumns._ID
        const val TABLE_NAME = "cocktails"
        const val COLUMN_NAME = "name"
        const val COLUMN_INGREDIENTS = "ingredients"
        const val COLUMN_INSTRUCTIONS = "instructions"
    }

    // SQL statement for creating the table
    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${CocktailEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${CocktailEntry.COLUMN_NAME} TEXT," +
                "${CocktailEntry.COLUMN_INGREDIENTS} TEXT," +
                "${CocktailEntry.COLUMN_INSTRUCTIONS} TEXT)"

    // SQL statement for deleting the table
    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${CocktailEntry.TABLE_NAME}"
}

