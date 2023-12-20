package com.example.androidexam2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val uri = intent.data
        val meal = intent.getSerializableExtra("item_key") as Meal
        val detailsImageView = findViewById<ImageView>(R.id.detailsImage)
        val descriptionTextView = findViewById<TextView>(R.id.detailsDescriptionTextView)
        val headerTextView = findViewById<TextView>(R.id.detailsHeaderTextView)
        val backButton = findViewById<FloatingActionButton>(R.id.detailsBackFloatingActionButton)
        val creatorTextView = findViewById<TextView>(R.id.creatorTextView)
        backButton.setOnClickListener {
            finish()
        }
        descriptionTextView.text = meal.description
        headerTextView.text = meal.name
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(detailsImageView)

        val db = FirebaseFirestore.getInstance()

        val creatorId = meal.creator
        if (creatorId != null){
            db.collection("users").document(creatorId).get()
                .addOnSuccessListener { document ->
                    if (document != null){
                        val creator = document.toObject<User>()
                        Log.d("!!!", document.toString())
                        creatorTextView.text = creator?.userName
                    }
                }



        }

    }

}