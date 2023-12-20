package com.example.androidexam2

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import android.Manifest
import android.view.Menu
import android.view.MenuItem
import android.widget.GridLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    //lateinit var createPlaceButton: Button
    lateinit var createPlaceFloatingActionButton: FloatingActionButton
    lateinit var auth: FirebaseAuth
    val places = mutableListOf<Place>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createPlaceFloatingActionButton = findViewById(R.id.createPlaceButtonFloatingActionButton)
        findViewById<Button>(R.id.profileButton).setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

//        val menu: Menu = bottomNav.menu
//        val menuItem: MenuItem? = menu.findItem(R.id.item_3)
//        menuItem?.title = "Test"
        val adapter = PlaceRecyclerAdapter(this, places)
        val recycler = findViewById<RecyclerView>(R.id.placeRecyclerView)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = adapter
        auth = Firebase.auth
//        if (auth.currentUser != null){
//            bottomNav.menu.findItem(R.id.item_3).title = "Test 3"
//        } else
//        {
//            bottomNav.menu.findItem(R.id.item_3).title = "User"
//        }
        val db = FirebaseFirestore.getInstance()
        db.collection("places").addSnapshotListener { snapshot, error ->
            if (snapshot != null) {
                places.clear()
                for (document in snapshot.documents) {
                    if (document != null) {
                        val place = document.toObject<Place>()
                        if (place != null) {
                            places.add(place)
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
        //createPlaceButton = findViewById<Button>(R.id.createPlaceButton)

//        val auth = Firebase.auth
         //auth.signOut()
//        if (auth.currentUser != null){
//            createPlaceButton.isVisible = true
//        }
        createPlaceFloatingActionButton.setOnClickListener {
            val intent = Intent(this, CreatePlaceActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // auth.signOut()
        if (auth.currentUser != null) {
            createPlaceFloatingActionButton.isVisible = true
        }
    }


}