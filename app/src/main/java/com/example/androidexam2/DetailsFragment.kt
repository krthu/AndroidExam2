package com.example.androidexam2

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage


class DetailsFragment : Fragment() {
    private var meal: Meal? = null
    private var uri: Uri? = null
    private lateinit var detailsImageView: ImageView
    private lateinit var ratingTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            meal = it.getSerializable("meal") as Meal?
            val uriString = it.getString("uri")
            if (uriString != null) {
                uri = Uri.parse(uriString)
            } else {
                downloadImage()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        detailsImageView = view.findViewById(R.id.detailsImage)
        val descriptionTextView = view.findViewById<TextView>(R.id.detailsDescriptionTextView)
        val headerTextView = view.findViewById<TextView>(R.id.detailsHeaderTextView)
        val backButton =
            view.findViewById<FloatingActionButton>(R.id.detailsBackFloatingActionButton)
        val creatorTextView = view.findViewById<TextView>(R.id.creatorTextView)
        val editImageView = view.findViewById<ImageView>(R.id.detailsEditImageView)
        val editImageBackground = view.findViewById<View>(R.id.detailsEditIconBackground)

        ratingTextView = view.findViewById(R.id.detailRatingTextView)

        editImageView.isVisible = auth.currentUser?.uid == meal?.creator
        editImageBackground.isVisible = auth.currentUser?.uid == meal?.creator

        editImageView.setOnClickListener { gotToCreateFragment() }
        editImageBackground.setOnClickListener { gotToCreateFragment() }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<Button>(R.id.rateButton).setOnClickListener {

            startRateDialog()
        }

        descriptionTextView.text = meal?.description
        headerTextView.text = meal?.name
        setImage()

        val db = FirebaseFirestore.getInstance()

        val creatorId = meal?.creator
        if (creatorId != null) {
            db.collection("users").document(creatorId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val creator = document.toObject<User>()

                        creatorTextView.text = creator?.userName
                    }
                }


        }
        val locationImageView = view.findViewById<ImageView>(R.id.detailsLocationImageView)
        locationImageView.setOnClickListener {
            goToMapsFragment()
        }
        setRatingTextView()
        parentFragmentManager.setFragmentResultListener("ratingsBundle", this) { key, bundle ->
            val newRating = bundle.getDouble("newRating")
            updateRating(newRating)
        }

    }

    private fun updateRating(newRating: Double) {
        // Update the already downloaded meal ratings instead of making another fetch.
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (meal?.ratings == null) {
            meal?.ratings = mutableMapOf()
        }
        meal?.ratings?.set(userId, newRating)
        setRatingTextView()

    }


    private fun setRatingTextView() {
        val rating = meal?.getAverageRating()
        var ratingString = ""
        if (rating != 0.0) {
            ratingString = String.format("%.1f", rating)
        }
        ratingTextView.text = ratingString


    }


    private fun setImage() {
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(detailsImageView)
    }

    private fun downloadImage() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = meal?.imageURI?.let { storage.getReferenceFromUrl(it) }
        storageRef?.downloadUrl?.addOnSuccessListener { downloadedUri ->
            uri = downloadedUri
            setImage()
        }
    }


    private fun gotToCreateFragment() {
        val bundle = Bundle()
        bundle.putSerializable("meal", meal)
        val createMealFragment = CreateMealFragment()
        createMealFragment.arguments = bundle
        changeFragment(createMealFragment)

    }

    private fun goToMapsFragment() {
        val bundle = Bundle()
        bundle.putSerializable("meal", meal)
        val mapFragment = MapsFragment()
        mapFragment.arguments = bundle
        changeFragment(mapFragment)
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    private fun startRateDialog() {

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {

            val rateDialogFragment = RateDialogFragment(meal)
            rateDialogFragment.show(parentFragmentManager, "rateDialog")

        } else {
            val signInDialogFragment = SignInDialogFragment()
            signInDialogFragment.show(parentFragmentManager, "signInDialog")
        }

    }
}