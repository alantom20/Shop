package com.alan.shop.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import com.alan.shop.model.Item
import com.alan.shop.view.FirestoreQueryLiveData

class ItemRepositoy(application : Application) {
    val TAG = ItemRepositoy::class.java.simpleName
    private var itemDao : ItemDao
    lateinit var items : LiveData<List<Item>>
    private var firestoreQueryLiveData = FirestoreQueryLiveData()
    private var network = isNetworkAvailable(application)

    init {
        itemDao = ItemDatabase.getDatabase(application).getItemDao()
        items = itemDao.getItems()

    }
    fun getAllItems() : LiveData<List<Item>>{
        if(network){
             items = firestoreQueryLiveData
        }else{
            items = itemDao.getItems()
        }
        return items
    }

    fun setCategory(categoryId: String) {
        if(network){
            firestoreQueryLiveData.setCategory(categoryId)
            Log.d(TAG, "setCategory:${items.value} ")
        }else {
            items = itemDao.getItemsByCategory(categoryId)
            Log.d(TAG, "setCategory:${categoryId} ")
                Log.d(TAG, "setCategory:${items.value} ")

        }
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw     = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}