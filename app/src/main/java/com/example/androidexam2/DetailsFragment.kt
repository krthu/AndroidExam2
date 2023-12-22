package com.example.androidexam2

import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var meal: Meal? = null
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            meal = it.getSerializable("meal") as Meal?
            val uriString = it.getString("uri")
            uri = Uri.parse(uriString)

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
        val detailsImageView = view.findViewById<ImageView>(R.id.detailsImage)
        val descriptionTextView = view.findViewById<TextView>(R.id.detailsDescriptionTextView)
        val headerTextView = view.findViewById<TextView>(R.id.detailsHeaderTextView)
        val backButton =
            view.findViewById<FloatingActionButton>(R.id.detailsBackFloatingActionButton)
        val creatorTextView = view.findViewById<TextView>(R.id.creatorTextView)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        descriptionTextView.text = meal?.description
        headerTextView.text = meal?.name
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(detailsImageView)

        val db = FirebaseFirestore.getInstance()

        val creatorId = meal?.creator
        if (creatorId != null) {
            db.collection("users").document(creatorId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val creator = document.toObject<User>()
                        Log.d("!!!", document.toString())
                        creatorTextView.text = creator?.userName
                    }
                }


        }
        val locationImageView = view.findViewById<ImageView>(R.id.detailsLocationImageView)
        locationImageView.setOnClickListener {
            goToMapsFragment()
        }

    }

    private fun goToMapsFragment(){
        val bundle = Bundle()
        bundle.putSerializable("meal", meal)
        val mapFragment = MapsFragment()
        mapFragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.replace(R.id.fragmentContainer, mapFragment)
        transaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}