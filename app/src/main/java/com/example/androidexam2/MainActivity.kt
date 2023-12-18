package com.example.androidexam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    //lateinit var createPlaceButton: Button
    lateinit var createPlaceFloatingActionButton: FloatingActionButton
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createPlaceFloatingActionButton = findViewById(R.id.createPlaceButtonFloatingActionButton)
        findViewById<Button>(R.id.profileButton).setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        auth = Firebase.auth
        //createPlaceButton = findViewById<Button>(R.id.createPlaceButton)

//        val auth = Firebase.auth
//         auth.signOut()
//        if (auth.currentUser != null){
//            createPlaceButton.isVisible = true
//        }
        createPlaceFloatingActionButton.setOnClickListener {
            val  intent = Intent(this, CreatePlaceActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // auth.signOut()
        if (auth.currentUser != null){
            createPlaceFloatingActionButton.isVisible = true
        }
    }
}