package com.example.androidexam2

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment

class RateDialogFragment(): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.rate_dialog, null)



        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
        return builder.create()


    }


}