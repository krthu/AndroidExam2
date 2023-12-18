package com.example.androidexam2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CreatePlaceActivity : AppCompatActivity() {
    lateinit var publishedSwitch: Switch
    lateinit var placeNameEditText: EditText
    lateinit var descriptionEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_place)
        publishedSwitch = findViewById(R.id.publishedSwitch)
        placeNameEditText = findViewById(R.id.placeNameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)

        findViewById<Button>(R.id.savePlaceButton).setOnClickListener {
            savePlace()
        }
    }


    private fun savePlace(){
        val name = placeNameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val published = publishedSwitch.isChecked
        Log.d("!!!", "Inside SavePlace")
        if (name.isEmpty() || description.isEmpty()){
            Log.d("!!!", "Inside failed Toast ")
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        val auth = Firebase.auth
        val place = Place(name = name, description = description, published = published, creator = auth.currentUser?.uid)

        val db = FirebaseFirestore.getInstance()
        db.collection("places").add(place).addOnSuccessListener {
            finish()
        }


    }
}