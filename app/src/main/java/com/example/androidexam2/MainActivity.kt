package com.example.androidexam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    //lateinit var createPlaceButton: Button
    lateinit var createMealFloatingActionButton: FloatingActionButton
    lateinit var auth: FirebaseAuth
    val meals = mutableListOf<Meal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createMealFloatingActionButton = findViewById(R.id.createMealButtonFloatingActionButton)
        findViewById<Button>(R.id.profileButton).setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

//        val menu: Menu = bottomNav.menu
//        val menuItem: MenuItem? = menu.findItem(R.id.item_3)
//        menuItem?.title = "Test"
        val adapter = MealAdapter(this, meals)
        val recycler = findViewById<RecyclerView>(R.id.mealRecyclerView)
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
        db.collection("meals").addSnapshotListener { snapshot, error ->
            if (snapshot != null) {
                meals.clear()
                for (document in snapshot.documents) {
                    if (document != null) {
                        val meal = document.toObject<Meal>()
                        if (meal != null) {
                            meals.add(meal)
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
        //createPlaceButton = findViewById<Button>(R.id.createPlaceButton)

//        val auth = Firebase.auth
 //        auth.signOut()
//        if (auth.currentUser != null){
//            createPlaceButton.isVisible = true
//        }
        createMealFloatingActionButton.setOnClickListener {
            val intent = Intent(this, CreateMealActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // auth.signOut()
        if (auth.currentUser != null) {
            createMealFloatingActionButton.isVisible = true
        }
    }


}