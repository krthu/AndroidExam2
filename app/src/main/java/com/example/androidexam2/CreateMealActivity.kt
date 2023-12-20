package com.example.androidexam2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException


import java.util.UUID

class CreateMealActivity : AppCompatActivity() {
    lateinit var publishedSwitch: Switch
    lateinit var mealNameEditText: EditText
    lateinit var descriptionEditText: EditText
    lateinit var mealImageView: ImageView
    val PERMISSION_REQUESTCODE = 1
    private lateinit var pickImage: ActivityResultLauncher<String>
    var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meal)
        publishedSwitch = findViewById(R.id.publishedSwitch)
        mealNameEditText = findViewById(R.id.mealNameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        mealImageView = findViewById(R.id.mealImageView)
        findViewById<Button>(R.id.addLocationButton).setOnClickListener {
            intent = Intent(this, AddLocationActivity::class.java)
            startActivity(intent)
        }
        pickImage =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startGallery()
                    Log.d("!!!", isGranted.toString())
                } else {

                }

            }
        findViewById<Button>(R.id.saveMealButton).setOnClickListener {
            saveImageToStorage()
        }

        findViewById<FloatingActionButton>(R.id.backCreatMealFloatingActionButton).setOnClickListener {
            finish()
        }

        mealImageView.setOnClickListener {
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
            mealImageView.setImageURI(imageURI)
            val latLangPair = getGPSCoordinates()
            Log.d("!!!", latLangPair.toString())

        }
    }

    private fun getGPSCoordinates(): Pair<Double, Double>? {
        try {
            val filePath = getRealPathFromURI()
            Log.d("!!!", filePath.toString())
            if (filePath != null){
                val exifInterface = ExifInterface(filePath)
                Log.d("!!!", exifInterface.latLong.toString())
                val lat = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
                val long = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
                Log.d("!!!", lat?.toDouble().toString())
                Log.d("!!!", "long ${long?.toDouble()}")
//                if (latLong != null){
//                    Log.d("!!!", "Lat lang not null")
//                    val lat = latLong[0].toDouble()
//                    val lang = latLong[1].toDouble()
//                    return Pair(lat, lang)
//                }
            }

        }catch (e: IOException){
            Log.e("!!!", "Error reading exit", e )
        }
        return null
    }

    fun getRealPathFromURI(): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = imageURI?.let { this.contentResolver.query(it, proj, null, null, null) }
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            return columnIndex?.let { cursor?.getString(it) }
        } finally {
            cursor?.close()
        }
    }

    private fun savePlace(storageRef: StorageReference) {
        val name = mealNameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val published = publishedSwitch.isChecked
        Log.d("!!!", "Inside SavePlace")
        if (name.isEmpty() || description.isEmpty()) {
            Log.d("!!!", "Inside failed Toast ")
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        val auth = Firebase.auth
        val meal = Meal(
            name = name,
            description = description,
            published = published,
            creator = auth.currentUser?.uid,
            imageURI = storageRef.toString()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("meals").add(meal).addOnSuccessListener {
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