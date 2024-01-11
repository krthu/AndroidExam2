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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class CreateMealFragment : Fragment() {
    private var mealToEdit: Meal? = null
    private val auth = Firebase.auth
    lateinit var mealNameEditText: EditText
    lateinit var descriptionEditText: EditText
    lateinit var mealImageView: ImageView
    lateinit var gpsTextView: TextView
    lateinit var saveButton: Button

    private val PERMISSION_REQUESTCODE = 1
    private lateinit var pickImage: ActivityResultLauncher<String>
    var imageURI: Uri? = null
    var gpsArray = mutableListOf<Double>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mealToEdit = it.getSerializable("meal") as Meal
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

        mealNameEditText = view.findViewById(R.id.mealNameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        mealImageView = view.findViewById(R.id.mealImageView)
        gpsTextView = view.findViewById(R.id.gpsTextView)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)

        pickImage =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startGallery()
                    Log.d("!!!", isGranted.toString())
                } else {

                }

            }
        saveButton = view.findViewById<Button>(R.id.saveMealButton)
        saveButton.setOnClickListener {
            if (mealNameEditText.text.isEmpty() || descriptionEditText.text.isEmpty()) {
                Snackbar.make(
                    requireContext(), saveButton,
                    getString(R.string.please_fill_in_all_fields), Snackbar.LENGTH_SHORT
                ).setAnchorView(saveButton).show()
            } else {
                saveImageToStorage()
            }
        }

        deleteButton.setOnClickListener {
            if (mealToEdit?.creator == auth.currentUser?.uid) {
                deleteMeal()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.backCreateMealFloatingActionButton)
            .setOnClickListener {
                parentFragmentManager.popBackStack()
            }

        mealImageView.setOnClickListener {
            checkAndRequestPermission()
        }
        if (mealToEdit != null && mealToEdit!!.creator == auth.currentUser?.uid) {
            deleteButton.visibility = View.VISIBLE
            setMealValuesToViews()
        }


    }

    private fun deleteMeal() {
        val db = FirebaseFirestore.getInstance()
        mealToEdit?.mealId?.let { mealID ->
            val documentRef = db.collection("meals").document(mealID)
            documentRef.delete().addOnSuccessListener {
                mealToEdit?.imageURI?.let { urlToDelete ->
                    deleteImageFromStorage(urlToDelete)
                    showMessage(getString(R.string.itemDeleted))
                    val fragment = ListFragment()
                    parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
                }
            }
        }
    }

    private fun setMealValuesToViews() {
        mealNameEditText.setText(mealToEdit?.name)
        descriptionEditText.setText(mealToEdit?.description)
        val storage = FirebaseStorage.getInstance()

        gpsArray = mealToEdit?.gpsArray ?: mutableListOf()
        if (gpsArray.isNotEmpty()) {
            setGpsTextView(gpsArray)
            setSmallMapFragment(gpsArray[0], gpsArray[1])
        } else {
            setSmallMapFragment(null, null)
        }


        val storageRef = mealToEdit?.imageURI?.let { url ->
            storage.getReferenceFromUrl(url)
        }
        storageRef?.downloadUrl?.addOnSuccessListener { uri ->
            Glide.with(requireContext())
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.baseline_question_mark_24)
                .into(mealImageView)
        }

    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PERMISSION_REQUESTCODE)
    }

    fun checkAndRequestPermission() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            pickImage.launch(Manifest.permission.READ_MEDIA_IMAGES)

        } else {
            startGallery()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUESTCODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            imageURI = data.data
            mealImageView.setImageURI(imageURI)

            val coordinates = getGPSFromUri()
            gpsArray.clear()
            gpsTextView.text = ""
            if (coordinates != null) {
                updateGpsInfo(coordinates[0], coordinates[1])
                setSmallMapFragment(coordinates[0], coordinates[1])
            } else {
                setSmallMapFragment(null, null)
            }
        }
    }

    private fun updateGpsInfo(latitude: Double, longitude: Double) {
        gpsArray.clear()
        gpsArray.add(latitude)
        gpsArray.add(longitude)
        setGpsTextView(gpsArray)
    }

    private fun setSmallMapFragment(lat: Double?, lng: Double?) {
        val smallMapFragment = SmallMapFragment()
        val bundle = Bundle()
        if (lat != null && lng != null) {
            bundle.putDouble("lat", lat)
            bundle.putDouble("lng", lng)
            smallMapFragment.arguments = bundle
        }
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.smallMapFragmentContainer, smallMapFragment)
        transaction.commit()
    }

    private fun setGpsTextView(listOfGPS: MutableList<Double>?) {
        val formatLat = String.format("%.5f", listOfGPS?.get(0))
        val formatLong = String.format("%.5f", listOfGPS?.get(1))
        gpsTextView.text = "Lat: $formatLat Long: $formatLong"
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


    private fun savePlace(storageRef: String) {
        val name = mealNameEditText.text.toString()
        val description = descriptionEditText.text.toString()

        val meal = Meal(
            name = name,
            description = description,
            creator = auth.currentUser?.uid,
            imageURI = storageRef,
            gpsArray = gpsArray
        )
        val db = FirebaseFirestore.getInstance()
        if (mealToEdit == null) {
            db.collection("meals").add(meal).addOnSuccessListener {

            }
        } else {
            val mealId = mealToEdit!!.mealId
            if (mealId != null) {
                db.collection("meals").document(mealId).set(meal)
                if (mealToEdit!!.imageURI != meal.imageURI) {
                    deleteImageFromStorage(mealToEdit!!.imageURI)
                }
            }

        }

        showMessage(getString(R.string.itemSaved))
        parentFragmentManager.popBackStack()

    }

    private fun showMessage(message: String) {
        Snackbar.make(requireContext(), saveButton, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(saveButton).apply {
            setAction(getString(R.string.dismiss)) {
                dismiss()
            }
        }.show()
    }

    private fun deleteImageFromStorage(urlToDelete: String?) {
        if (urlToDelete != null) {
            val filename = urlToDelete.substringAfterLast("/")
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child("images/$filename")
            storageRef.delete().addOnSuccessListener {
            }
        }
    }

    private fun saveImageToStorage() {
        if (imageURI != null) {
            if (mealToEdit != null) {
                deleteImageFromStorage(mealToEdit!!.imageURI)
            }
            val storageRef =
                FirebaseStorage.getInstance().getReference("images/${UUID.randomUUID()}")
            imageURI?.let { storageRef.putFile(it) }
                ?.addOnSuccessListener {
                    savePlace(storageRef.toString())
                }
        } else if (mealToEdit != null) {
            mealToEdit!!.imageURI?.let { savePlace(it) }

        } else {
            Snackbar.make(
                requireContext(),
                saveButton,
                getString(R.string.pleaseSelectImage),
                Snackbar.LENGTH_SHORT
            ).setAnchorView(saveButton).show()
        }

    }

    fun onMapClick(latitude: Double, longitude: Double) {
        updateGpsInfo(latitude, longitude)
    }


    companion object {
        fun newInstance(param1: String, param2: String) =
            CreateMealFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


}