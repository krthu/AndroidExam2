package com.example.androidexam2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val uri = intent.data
        val place = intent.getSerializableExtra("item_key") as Place
        val detailsImageView = findViewById<ImageView>(R.id.detailsImage)
        val descriptionTextView = findViewById<TextView>(R.id.detailsDescriptionTextView)
        val headerTextView = findViewById<TextView>(R.id.detailsHeaderTextView)
        descriptionTextView.text = place.description
        headerTextView.text = place.name
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(detailsImageView)
    }
}