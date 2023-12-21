package com.example.androidexam2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

lateinit var publishedSwitch: Switch
lateinit var mealNameEditText: EditText
lateinit var descriptionEditText: EditText
lateinit var mealImageView: ImageView
lateinit var gpsTextView: TextView
val PERMISSION_REQUESTCODE = 1
private lateinit var pickImage: ActivityResultLauncher<String>
var imageURI: Uri? = null
var gpsArray = mutableListOf<Double>()

/**
 * A simple [Fragment] subclass.
 * Use the [CreateMealFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateMealFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        publishedSwitch = view.findViewById(R.id.publishedSwitch)
        mealNameEditText = view.findViewById(R.id.mealNameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        mealImageView = view.findViewById(R.id.mealImageView)
        gpsTextView = view.findViewById(R.id.gpsTextView)
        view.findViewById<Button>(R.id.addLocationButton).setOnClickListener {
           // intent = Intent(this, AddLocationActivity::class.java)
           // startActivity(intent)
        }
        pickImage =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startGallery()
                    Log.d("!!!", isGranted.toString())
                } else {

                }

            }
        view.findViewById<Button>(R.id.saveMealButton).setOnClickListener {
            saveImageToStorage()
        }

        view.findViewById<FloatingActionButton>(R.id.backCreatMealFloatingActionButton).setOnClickListener {
            parentFragmentManager.popBackStack()
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
                requireContext(),
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
        if (requestCode == PERMISSION_REQUESTCODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            imageURI = data.data
            mealImageView.setImageURI(imageURI)
            //val coordinates = getGPSFromUri()
            val coordinates = getGPSFromUri()

            if (coordinates != null){
                gpsArray.add(coordinates[0])
                gpsArray.add(coordinates[1])
                val formatLat = String.format("%.5f", gpsArray[0])
                val formatLong = String.format("%.5f", gpsArray[1])
                gpsTextView.text = "Lat: $formatLat Long: $formatLong"
            }

            if (coordinates != null) {
                Log.d("!!!", "Lat = ${coordinates[0]} Long = ${coordinates[1]}")
            }
        }
    }

    private fun getGPSFromUri(): DoubleArray? {

        imageURI?.let {
            requireContext().contentResolver.openInputStream(it).use { stream ->
                if (stream != null) {
                    ExifInterface(stream).let { exif ->
                        return exif.latLong
                    }
                }
            }
        }
        return null
    }


    private fun savePlace(storageRef: StorageReference) {
        val name = mealNameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val published = publishedSwitch.isChecked
        Log.d("!!!", "Inside SavePlace")
        if (name.isEmpty() || description.isEmpty()) {
            Log.d("!!!", "Inside failed Toast ")
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        val auth = Firebase.auth
        val meal = Meal(
            name = name,
            description = description,
            published = published,
            creator = auth.currentUser?.uid,
            imageURI = storageRef.toString(),
            gpsArray = gpsArray
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("meals").add(meal).addOnSuccessListener {
            parentFragmentManager.popBackStack()
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
            Toast.makeText(requireContext(), "Please select a image", Toast.LENGTH_SHORT).show()
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateMealFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateMealFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}