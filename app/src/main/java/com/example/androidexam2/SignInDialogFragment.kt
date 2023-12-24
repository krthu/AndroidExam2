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

class SignInDialogFragment(): DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.signin_dialog_fragment, null)


        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)




            .setNegativeButton("Cancel"){ dialog, which ->


            }
        return builder.create()
    }
}