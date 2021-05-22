package com.alan.shop.model

import android.os.Parcelable
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

@Entity
@Parcelize
@IgnoreExtraProperties
data class Item(var title : String,
                var price : Int,
                var imageUrl : String,
                @PrimaryKey
                @get:Exclude var id :String,
                var content : String,
                var category: String,
                var viewCount : Int) : Parcelable{
    constructor() : this("",0,"","","","",0)
}
