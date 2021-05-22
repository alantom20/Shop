package com.alan.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.alan.shop.model.Item
import com.alan.shop.model.WatchItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    val TAG = DetailActivity::class.java.simpleName
    lateinit var item: Item
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        item = intent.getParcelableExtra<Item>("ITEM")
        Log.d(TAG, "onCreate:${item.id} / ${item.title} ")
        web.settings.javaScriptEnabled = true
        web.loadUrl(item.content)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseFirestore.getInstance().collection("users")
            .document(uid!!)
            .collection("watchItem")
            .document(item.id)
            .get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val watchItem = task.result?.toObject(WatchItem::class.java)
                    if(watchItem != null){
                        watch.isChecked = true
                    }
                }
            }
        watch.setOnCheckedChangeListener { button, checked ->
            if (checked){
                FirebaseFirestore.getInstance().collection("users")
                    .document(uid!!)
                    .collection("watchItem")
                    .document(item.id)
                    .set(WatchItem(item.id))

            }else{
                FirebaseFirestore.getInstance().collection("users")
                    .document(uid!!)
                    .collection("watchItem")
                    .document(item.id)
                    .delete()

            }

        }


    }

    override fun onStart() {
        super.onStart()
        item.viewCount++
        item.id?.let {
            FirebaseFirestore.getInstance().collection("items")
                .document(it).update("viewCount",item.viewCount)
        }
    }
}