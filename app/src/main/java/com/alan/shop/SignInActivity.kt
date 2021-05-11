package com.alan.shop

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private val RC_GOOGLE_SIGN_IN: Int = 200
    private lateinit var googleSignInClient: GoogleSignInClient
    val TAG = SignInActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        google_sign_in.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent,RC_GOOGLE_SIGN_IN)
        }
        signUp.setOnClickListener {
            signUp()
        }
        login.setOnClickListener {
            logIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "onActivityResult: ${account?.id}")
            val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
            FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        setResult(RESULT_OK)
                        finish()
                    }else{
                        Log.d(TAG, "onActivityResult: ${task.exception?.message}")
                        Snackbar.make(main_signin,"Firebase authentication failed",Snackbar.LENGTH_LONG ).show()
                    }

                }
        }
    }

    private fun logIn() {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email.text.toString(), passwd.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    setResult(RESULT_OK)
                    finish()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Log in")
                        .setMessage(it.exception?.message)
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
    }

    private fun signUp() {
        val sEmail = email.text.toString()
        val sPasswd = passwd.text.toString()
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(sEmail, sPasswd)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    AlertDialog.Builder(this)
                        .setTitle("Sign up")
                        .setMessage("Account created")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            setResult(RESULT_OK)
                            finish()
                        }).show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Sign up")
                        .setMessage(task.exception?.message)
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
    }
}