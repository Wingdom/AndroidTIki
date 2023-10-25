package com.wingdom.androidcocktails

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

class CocktailDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val mContext = context

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_COCTAIL_TABLE =
            "CREATE TABLE IF NOT EXISTS ${CocktailContract.CocktailEntry.TABLE_NAME} (" +
                    "${CocktailContract.CocktailEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${CocktailContract.CocktailEntry.COLUMN_NAME} TEXT NOT NULL," +
                    "${CocktailContract.CocktailEntry.COLUMN_INGREDIENTS} TEXT NOT NULL," +
                    "${CocktailContract.CocktailEntry.COLUMN_INSTRUCTIONS} TEXT NOT NULL);"
        Log.i("DbHelper", "On Create, query made")
        db?.execSQL(SQL_CREATE_COCTAIL_TABLE)
        Log.i("DbHelper", "Create Table Run")
        initializeDatabase(db)
        Log.i("DbHelper", "initialize db run")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${CocktailContract.CocktailEntry.TABLE_NAME}")
        onCreate(db)
    }

    fun addCocktail(name: String, ingredients: String, instructions: String) {
        Log.i("DbHelper", "Add Cocktail Function Running")
        val db = writableDatabase

        try {
            val values = ContentValues().apply {
                put(CocktailContract.CocktailEntry.COLUMN_NAME, name)
                put(CocktailContract.CocktailEntry.COLUMN_INGREDIENTS, ingredients)
                put(CocktailContract.CocktailEntry.COLUMN_INSTRUCTIONS, instructions)
            }
            db.insert(CocktailContract.CocktailEntry.TABLE_NAME, null, values)
        } catch(e: Exception){
            Log.e("Add Cocktail", "Error when trying to add new cocktail", e)
        } finally {
            db.close()
        }
    }

    fun updateCocktail(id: Long, name: String, ingredients: String, instructions: String) {
        val db = writableDatabase

        try {
            val values = ContentValues().apply {
                put(CocktailContract.CocktailEntry.COLUMN_NAME, name)
                put(CocktailContract.CocktailEntry.COLUMN_INGREDIENTS, ingredients)
                put(CocktailContract.CocktailEntry.COLUMN_INSTRUCTIONS, instructions)
            }
            val selection = "${CocktailContract.CocktailEntry._ID} = ?"
            val selectionArgs = arrayOf(id.toString())
            db.update(CocktailContract.CocktailEntry.TABLE_NAME, values, selection, selectionArgs)
        } catch (e: Exception) {
            Log.e("Update Cocktail", "Error when trying to update cocktail", e)
        } finally {
            db.close()
        }
    }

    fun getAllCocktails(): Cursor {
        Log.i("DbHelper", "Get All Cocktails Running")
        val db = readableDatabase
        return db.query(
            CocktailContract.CocktailEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        db.endTransaction()
        db.close()
    }

    fun searchCocktails(query: String): Cursor {
        Log.i("DbHelper", "Search Cocktails Function Called")
        val db = readableDatabase
        val selection = "${CocktailContract.CocktailEntry.COLUMN_NAME} LIKE ? OR ${CocktailContract.CocktailEntry.COLUMN_INGREDIENTS} LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%")
        return db.query(
            CocktailContract.CocktailEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "${CocktailContract.CocktailEntry.COLUMN_NAME} ASC"
        )
    }

    fun resetDatabase() {
        writableDatabase.execSQL("DROP TABLE IF EXISTS ${CocktailContract.CocktailEntry.TABLE_NAME}")
        onCreate(writableDatabase)
    }

    fun initializeDatabase(db: SQLiteDatabase) {
        Log.i("CocktailInitializer", "Adding Initial Cocktails To DB")

        // Read the cocktails data from the CSV file
        val inputStream = mContext.assets.open("default_drinks.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        reader.readLine() // Skip the header row
        line = reader.readLine()
        while (line != null) {
            val values = line.split(";").map { it.trim() }
            val name = values[0]
            val ingredients = values[1]
            val instructions = values[2]
            addInitialCocktail(db, name, ingredients, instructions)
            line = reader.readLine()
        }
        reader.close()
    }

    fun addInitialCocktail(db: SQLiteDatabase?, name: String, ingredients: String, instructions: String) {
        Log.i("DbHelper", "Add Cocktail Function Running")
        try {
            val values = ContentValues().apply {
                put(CocktailContract.CocktailEntry.COLUMN_NAME, name)
                put(CocktailContract.CocktailEntry.COLUMN_INGREDIENTS, ingredients)
                put(CocktailContract.CocktailEntry.COLUMN_INSTRUCTIONS, instructions)
            }
            db?.insert(CocktailContract.CocktailEntry.TABLE_NAME, null, values)
        } catch(e: Exception){
            Log.e("Add Cocktail", "Error when trying to add new cocktail", e)
        } /*finally {
            db?.close()
        }*/
    }

    fun getCocktailById(cocktailId: Long): Cocktail? {
        val db = readableDatabase
        val selection = "${CocktailContract.CocktailEntry._ID} = ?"
        val selectionArgs = arrayOf(cocktailId.toString())

        val cursor = db.query(
            CocktailContract.CocktailEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var cocktail: Cocktail? = null

        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(CocktailContract.CocktailEntry.COLUMN_NAME)
            val ingredientsIndex = cursor.getColumnIndex(CocktailContract.CocktailEntry.COLUMN_INGREDIENTS)
            val instructionsIndex = cursor.getColumnIndex(CocktailContract.CocktailEntry.COLUMN_INSTRUCTIONS)

            if (nameIndex >= 0 && ingredientsIndex >= 0 && instructionsIndex >= 0) {
                val name = cursor.getString(nameIndex)
                val ingredients = cursor.getString(ingredientsIndex)
                val instructions = cursor.getString(instructionsIndex)

                cocktail = Cocktail(cocktailId, name, ingredients, instructions, R.drawable.ic_cocktails)
            }
        }

        cursor.close()
        db.close()

        return cocktail
    }

    companion object {
        private const val DATABASE_NAME = "cocktails.db"
        private const val DATABASE_VERSION = 1

        private var instance: CocktailDbHelper? = null

        @Synchronized
        fun getInstance(context: Context): CocktailDbHelper {
            if (instance == null) {
                instance = CocktailDbHelper(context)
            }
            return instance as CocktailDbHelper
        }
    }
}


