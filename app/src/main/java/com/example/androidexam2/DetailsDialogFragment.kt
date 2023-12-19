package com.example.androidexam2

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide

class DetailsDialogFragment(val item: Place,val uri: Uri?): DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.details_fragment, null)
        val headline = view.findViewById<TextView>(R.id.detailsHeadLineTextView)
        val descriptionTextView = view.findViewById<TextView>(R.id.desriptionTextView)
        val detailsImageView = view.findViewById<ImageView>(R.id.detailsImageView)
        headline.text = item.name
        descriptionTextView. text = item.description
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.baseline_question_mark_24)
            .override(200, 200)
            .into(detailsImageView)



        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
            .setNegativeButton("Close"){ dialog, which ->


            }
        return builder.create()
    }
}