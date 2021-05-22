package com.alan.shop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alan.shop.data.ItemDatabase
import com.alan.shop.model.Category
import com.alan.shop.model.Item
import com.alan.shop.view.ItemHolder
import com.alan.shop.view.ItemViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity(),FirebaseAuth.AuthStateListener {

    private lateinit var viewModel: ItemViewModel
    private val RC_SIGNIN: Int = 100
    val TAG =  MainActivity::class.java.simpleName
    lateinit var adapter : ItemAdapter
    var categories = mutableListOf<Category>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        verify_email.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Snackbar.make(it,"Verify email sent",Snackbar.LENGTH_LONG).show()
                    }
            }
        }
        FirebaseFirestore.getInstance().collection("categories")
            .get().addOnCompleteListener { task->
                if(task.isSuccessful){
                    categories.add(0, Category("","不分類"))
                    task.result?.let {
                        for (doc in it){
                            categories.add(Category(doc.id,doc.data.get("name").toString()))
                        }
                        spinner.adapter = ArrayAdapter<Category>(
                            this@MainActivity,
                            android.R.layout.simple_spinner_item,
                            categories).apply {
                            setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                        }
                        spinner.setSelection(0,false)
                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {

                                viewModel.setCategory(categories.get(position).id)
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                        }

                    }
                }

            }





        //setupRecyclerView
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(mutableListOf<Item>())
        recycler.adapter = adapter
        viewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        viewModel.getItems().observe(this, androidx.lifecycle.Observer {
            Log.d(TAG, "onCreate:${it.size} ")
            adapter.items = it
            adapter.notifyDataSetChanged()

        })

    }

    inner class ItemAdapter(var items:List<Item>) : RecyclerView.Adapter<ItemHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
           return  ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_row,parent,false))
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bindTo(items.get(position))
            holder.itemView.setOnClickListener {
                itemClicked(items.get(position),position)
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

    }

    private fun setupAdapter() {

    }

    private fun itemClicked(item: Item, position: Int) {
        Log.d(TAG, "itemClicked: " + item.title)
        val intent = Intent(this,DetailActivity::class.java)
        intent.putExtra("ITEM",item)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_signin -> {
                val whiteList = listOf<String>("tw","hk","cn","au")
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                            AuthUI.IdpConfig.EmailBuilder().build(),
                            AuthUI.IdpConfig.GoogleBuilder().build(),
                            AuthUI.IdpConfig.FacebookBuilder().build(),
                            AuthUI.IdpConfig.PhoneBuilder()
                                .setWhitelistedCountries(whiteList)
                                .setDefaultCountryIso("tw")
                                .build()
                        ))
                        .setLogo(R.drawable.shop)
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.SignUp)
                        .build(),
                    RC_SIGNIN)
                true
            }
            R.id.sign_out ->{
                FirebaseAuth.getInstance().signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onAuthStateChanged(auth: FirebaseAuth) {
        val user = auth.currentUser
        Log.d(TAG, "onAuthStateChanged: " + user?.uid)
        if(user !=null){
            user_info.setText("${user.email} / ${user.isEmailVerified}")
         //   verify_email.visibility = if(user.isEmailVerified) View.GONE else View.VISIBLE
        }else{
            user_info.setText("Not Login")
            verify_email.visibility = View.GONE

        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)

    }
}