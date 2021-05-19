package com.alan.shop

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Item(var title : String,
                var price : Int,
                var imageUrl : String,
                @get:Exclude var id :String?,
                var content : String,
                var viewCount : Int) : Parcelable{
    constructor() : this("",0,"","","",0)
}
