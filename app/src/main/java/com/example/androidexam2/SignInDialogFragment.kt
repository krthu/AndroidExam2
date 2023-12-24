package com.example.androidexam2

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class SignInDialogFragment(): DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.signin_dialog_fragment, null)
        val emailEditText = view.findViewById<EditText>(R.id.mailInputTextView)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordInputTexttView)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

            .setPositiveButton("Sign in"){ dialog, which ->
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()){
                    val auth = FirebaseAuth.getInstance()
                    auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                        //Toast.makeText(requireContext(), "Signed In", Toast.LENGTH_SHORT).show()
                    }
                }
            }



            .setNegativeButton("Cancel"){ dialog, which ->


            }
        return builder.create()
    }
}