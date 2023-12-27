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
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [CreateMealFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CreateMealFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mealToEdit: Meal? = null
    private val auth = Firebase.auth


    lateinit var publishedSwitch: Switch
    lateinit var mealNameEditText: EditText
    lateinit var descriptionEditText: EditText
    lateinit var mealImageView: ImageView
    lateinit var gpsTextView: TextView

    val PERMISSION_REQUESTCODE = 1
    private lateinit var pickImage: ActivityResultLauncher<String>
    var imageURI: Uri? = null
    var gpsArray = mutableListOf<Double>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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


        publishedSwitch = view.findViewById(R.id.publishedSwitch)
        mealNameEditText = view.findViewById(R.id.mealNameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        mealImageView = view.findViewById(R.id.mealImageView)
        gpsTextView = view.findViewById(R.id.gpsTextView)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
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

        deleteButton.setOnClickListener {
            if (mealToEdit?.creator == auth.currentUser?.uid) {
                deleteMeal()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.backCreatMealFloatingActionButton)
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
                }

            }
            parentFragmentManager.popBackStack()
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


//        if (mealToEdit?.gpsArray?.size != 0){
//            if (mealToEdit?.gpsArray != null){
//                gpsArray.add(mealToEdit!!.gpsArray!!.get(0))
//            }
//            setGpsTextView(mealToEdit?.gpsArray)
//            setSmallMapFragment(gpsArray[0], gpsArray[1])
//        }

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
        val published = publishedSwitch.isChecked

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        val meal = Meal(
            name = name,
            description = description,
            published = published,
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
        parentFragmentManager.popBackStack()

    }

    private fun deleteImageFromStorage(urlToDelete: String?) {
        if (urlToDelete != null) {
            val filename = urlToDelete.substringAfterLast("/")
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child("images/$filename")
            storageRef.delete().addOnSuccessListener {
                Log.d("!!!", "deleted")
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
            Toast.makeText(requireContext(), "Please select a image", Toast.LENGTH_SHORT).show()

        }

    }

    fun onMapClick(latitude: Double, longitude: Double) {
        updateGpsInfo(latitude, longitude)
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