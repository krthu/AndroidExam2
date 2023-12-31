package com.example.androidexam2

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment

class RateDialogFragment(): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.rate_dialog, null)
        val ratingBar = view.findViewById<RatingBar>(R.id.rateDialogRatingBar)

        view.findViewById<Button>(R.id.rateDialogButton).setOnClickListener {
            //Update the rating to the db
            Log.d("!!!", ratingBar.rating.toString())

        }

        view.findViewById<Button>(R.id.rateDialogCancel).setOnClickListener {
            dismiss()
        }



        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
        return builder.create()


    }


}