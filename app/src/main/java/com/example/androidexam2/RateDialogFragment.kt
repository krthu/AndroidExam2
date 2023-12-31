package com.example.androidexam2

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RateDialogFragment(val meal: Meal?): DialogFragment() {
    lateinit var ratingBar: RatingBar
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.rate_dialog, null)
        ratingBar = view.findViewById<RatingBar>(R.id.rateDialogRatingBar)


        view.findViewById<Button>(R.id.rateDialogButton).setOnClickListener {
            //Update the rating to the db
            Log.d("!!!", ratingBar.rating.toString())
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
        val dbRef = meal?.mealId?.let {mealId ->
            FirebaseFirestore.getInstance().collection("meals").document(
                mealId
            )
        }
        val rating = ratingBar.rating
        val auth = FirebaseAuth.getInstance()

        val userId = auth.currentUser?.uid

        dbRef?.update("ratings.$userId", rating)


    }


}