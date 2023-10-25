package com.wingdom.androidcocktails

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import java.util.*

class CocktailAdapter(context: Context, cursor: Cursor) :
    CursorAdapter(context, cursor, 0), Filterable {

    private var filteredCursor: Cursor? = null
    private val db: CocktailDbHelper = CocktailDbHelper.getInstance(context)

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.list_item_cocktail, parent, false)
    }

    @SuppressLint("Range")
    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        view?.findViewById<TextView>(R.id.text_view_cocktail_name)?.text =
            cursor?.getString(cursor.getColumnIndex(CocktailContract.CocktailEntry.COLUMN_NAME))
        view?.findViewById<TextView>(R.id.text_view_cocktail_ingredients)?.text =
            cursor?.getString(cursor.getColumnIndex(CocktailContract.CocktailEntry.COLUMN_INGREDIENTS))
        view?.findViewById<TextView>(R.id.text_view_cocktail_instructions)?.text =
            cursor?.getString(cursor.getColumnIndex(CocktailContract.CocktailEntry.COLUMN_INSTRUCTIONS))?.replace("\\n", "\n")
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint.isNullOrEmpty()) {
                    // Return the original list if search query is empty
                    filteredCursor = cursor
                } else {
                    // Perform database query for matching cocktails
                    filteredCursor = db.searchCocktails(constraint.toString())
                }
                results.values = filteredCursor
                results.count = filteredCursor?.count ?: 0
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Update the adapter's cursor with the filtered results
                changeCursor(results?.values as Cursor?)
                // Reload if the search is empty
                if (constraint.isNullOrEmpty()) {
                    //cursor = db.getAllCocktails()
                    changeCursor(db.getAllCocktails() as Cursor?)
                }
            }
        }
    }

    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(position: Int) {
        cursor.moveToPosition(position)
        return // Return the Cocktail object at the specified position
    }

    @SuppressLint("Range")
    override fun getItemId(position: Int): Long {
        return if (cursor.moveToPosition(position)) {
            cursor.getLong(cursor.getColumnIndex(CocktailContract.CocktailEntry._ID))
        } else {
            super.getItemId(position)
        }
    }

    companion object {
        private val COLUMNS = arrayOf(CocktailContract.CocktailEntry.COLUMN_NAME, CocktailContract.CocktailEntry.COLUMN_INGREDIENTS)
    }
}


