package com.wingdom.androidcocktails

import android.os.Parcel
import android.os.Parcelable

data class Cocktail(
    val id: Long?,
    val name: String,
    val ingredients: String,
    val instructions: String,
    val iconResId: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as Long?,
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(ingredients)
        parcel.writeString(instructions)
        parcel.writeInt(iconResId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Cocktail> = object : Parcelable.Creator<Cocktail> {
            override fun createFromParcel(parcel: Parcel): Cocktail {
                return Cocktail(parcel)
            }

            override fun newArray(size: Int): Array<Cocktail?> {
                return arrayOfNulls(size)
            }
        }
    }
}
