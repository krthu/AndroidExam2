package com.example.androidexam2

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RateDialogFragment(private val meal: Meal?) : DialogFragment() {
    private lateinit var ratingBar: RatingBar
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.rate_dialog, null)
        ratingBar = view.findViewById(R.id.rateDialogRatingBar)
        val userRating = meal?.ratings?.get(userId)
        if (userRating != null) {
            ratingBar.rating = userRating.toFloat()
        }


        view.findViewById<Button>(R.id.rateDialogButton).setOnClickListener {
            //Update the rating to the db
            saveRating()
        }

        view.findViewById<Button>(R.id.rateDialogCancel).setOnClickListener {
            dismiss()
        }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
        return builder.create()

    }

    private fun saveRating() {
        val dbRef = meal?.mealId?.let { mealId ->
            FirebaseFirestore.getInstance().collection("meals").document(mealId)
        }
        val rating = ratingBar.rating
        dbRef?.update("ratings.$userId", rating)?.addOnSuccessListener {
            meal?.ratings?.put(userId, rating.toDouble())
            val bundle = Bundle()
            bundle.putDouble("newRating", rating.toDouble())
            setFragmentResult("ratingsBundle", bundle)
            dismiss()
        }
            ?.addOnFailureListener {
                Snackbar.make(ratingBar, getString(R.string.rating_not_set), Snackbar.LENGTH_SHORT)
                    .apply {
                        setAction(getString(R.string.dismiss)) {
                            dismiss()
                        }
                    }
            }
    }
}