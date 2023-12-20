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
        val place = intent.getSerializableExtra("item_key") as Place
        val detailsImageView = findViewById<ImageView>(R.id.detailsImage)
        val descriptionTextView = findViewById<TextView>(R.id.detailsDescriptionTextView)
        val headerTextView = findViewById<TextView>(R.id.detailsHeaderTextView)
        val backButton = findViewById<FloatingActionButton>(R.id.detailsBackFloatingActionButton)
        val creatorTextView = findViewById<TextView>(R.id.creatorTextView)
        backButton.setOnClickListener {
            finish()
        }
        descriptionTextView.text = place.description
        headerTextView.text = place.name
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(detailsImageView)

        val db = FirebaseFirestore.getInstance()

        val creatorId = place.creator
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