package com.alan.shop.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alan.shop.model.Category
import com.alan.shop.model.Item

@Dao
interface ItemDao {
    @Query("select * from Item order by viewCount DESC ")
    fun getItems() : LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(item: Item)

    @Query("select * from Item where category == :categoryId order by viewCount")
    fun getItemsByCategory(categoryId : String) : LiveData<List<Item>>

}