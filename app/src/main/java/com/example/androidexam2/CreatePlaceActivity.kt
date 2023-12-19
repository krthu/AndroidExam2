package com.example.androidexam2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


import java.util.UUID

class CreatePlaceActivity : AppCompatActivity() {
    lateinit var publishedSwitch: Switch
    lateinit var placeNameEditText: EditText
    lateinit var descriptionEditText: EditText
    lateinit var placeImageView: ImageView
    val PERMISSION_REQUESTCODE = 1
    private lateinit var pickImage: ActivityResultLauncher<String>
    var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_place)
        publishedSwitch = findViewById(R.id.publishedSwitch)
        placeNameEditText = findViewById(R.id.placeNameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        placeImageView = findViewById(R.id.placeImageView)
        pickImage =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startGallery()
                    Log.d("!!!", isGranted.toString())
                } else {

                }

            }
        findViewById<Button>(R.id.savePlaceButton).setOnClickListener {
            saveImageToStorage()
        }

        findViewById<FloatingActionButton>(R.id.backCreatPlaceFloatingActionButton).setOnClickListener {
            finish()
        }

        placeImageView.setOnClickListener {
            checkAndRequestPermission()
        }

    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PERMISSION_REQUESTCODE)
    }


    fun checkAndRequestPermission() {
        Log.d("!!!", "Inside checkPermission")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("!!!", "Inside Dont have permission")

            pickImage.launch(Manifest.permission.READ_MEDIA_IMAGES)
            Log.d("!!!", "After permission")
        } else {
            startGallery()
            Log.d("!!!", "We have permission")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUESTCODE && resultCode == RESULT_OK && data != null) {
            imageURI = data.data
            placeImageView.setImageURI(imageURI)

        }
    }

    private fun savePlace(storageRef: StorageReference) {
        val name = placeNameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val published = publishedSwitch.isChecked
        Log.d("!!!", "Inside SavePlace")
        if (name.isEmpty() || description.isEmpty()) {
            Log.d("!!!", "Inside failed Toast ")
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        val auth = Firebase.auth
        val place = Place(
            name = name,
            description = description,
            published = published,
            creator = auth.currentUser?.uid,
            imageURI = storageRef.toString()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("places").add(place).addOnSuccessListener {
            finish()
        }
    }

    private fun saveImageToStorage() {
        if (imageURI != null) {
            val storageRef =
                FirebaseStorage.getInstance().getReference("images/${UUID.randomUUID()}")
            imageURI?.let { storageRef.putFile(it) }
                ?.addOnSuccessListener {
                    savePlace(storageRef)
                }
        } else {
            Toast.makeText(this, "Please select a image", Toast.LENGTH_SHORT).show()
        }

    }

}