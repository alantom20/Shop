package com.alan.shop.view

import androidx.lifecycle.LiveData
import com.alan.shop.model.Item
import com.google.firebase.firestore.*

class FirestoreQueryLiveData : LiveData<List<Item>>(), EventListener<QuerySnapshot> {
    private lateinit var registered: ListenerRegistration
    var query = FirebaseFirestore.getInstance()
        .collection("items")
        .orderBy("viewCount", Query.Direction.DESCENDING)
        .limit(10)
    var isRegistered = false

    override fun onActive() {
        registered = query.addSnapshotListener(this)
        isRegistered =true
    }

    override fun onInactive() {
        super.onInactive()
        if(isRegistered){
            registered.remove()
        }

    }

    override fun onEvent(querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if(querySnapshot != null  && !querySnapshot.isEmpty){
            val list = mutableListOf<Item>()
            for (doc in querySnapshot.documents) {
                val item = doc.toObject(Item::class.java) ?: Item()
                item.id = doc.id
                list.add(item)
            }
            value = list
        }
    }

    fun setCategory(categoryId: String) {
        if (isRegistered){
            registered.remove()
            isRegistered = false
        }
        if(categoryId.length > 0){
            query = FirebaseFirestore.getInstance()
                .collection("items")
                .whereEqualTo("category",categoryId)
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(10)
        }else{
            query = FirebaseFirestore.getInstance()
                .collection("items")
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(10)
        }
        registered = query.addSnapshotListener(this)
        isRegistered = true
    }
}