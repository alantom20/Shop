 package com.alan.shop.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.alan.shop.data.ItemRepositoy
import com.alan.shop.model.Item


class ItemViewModel(application : Application) : AndroidViewModel(application){
    private lateinit var itemRepositoy: ItemRepositoy
    init {
        itemRepositoy =  ItemRepositoy(application)
    }

    fun getItems() : LiveData<List<Item>> {
        return itemRepositoy.getAllItems()
    }
    fun setCategory(categoryId : String){
        itemRepositoy.setCategory(categoryId)

    }
}