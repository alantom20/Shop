package com.alan.shop

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity(),FirebaseAuth.AuthStateListener {

    private val RC_SIGNIN: Int = 100
    val TAG =  MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        verify_email.setOnClickListener {
            FirebaseAuth.getInstance().currentUser.sendEmailVerification()
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Snackbar.make(it,"Verify email sent",Snackbar.LENGTH_LONG).show()
                    }
            }
        }
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
              /*  startActivityForResult(
                    Intent(this,SignInActivity::class.java),
                    RC_SIGNIN)*/
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
            verify_email.visibility = if(user.isEmailVerified) View.GONE else View.VISIBLE
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