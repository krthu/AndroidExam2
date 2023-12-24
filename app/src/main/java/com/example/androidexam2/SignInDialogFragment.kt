package com.example.androidexam2

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth


class SignInDialogFragment(): DialogFragment() {



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.signin_dialog_fragment, null)
        val emailEditText = view.findViewById<EditText>(R.id.mailInputTextView)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordInputTexttView)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val signUpButton = view.findViewById<Button>(R.id.signUpButton)


        view.findViewById<Button>(R.id.dialogFragmentSignInButton).setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Signed In", Toast.LENGTH_SHORT).show()
                    notifyLoginSuccess()
                    dismiss()
                }
                    .addOnFailureListener{e ->
                        Toast.makeText(requireContext(), "Invalid credentials!", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Email or password missing", Toast.LENGTH_SHORT).show()
            }
        }
        cancelButton.setOnClickListener{
            dismiss()
        }

        signUpButton.setOnClickListener{

            val signUpFragment = SignUpFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, signUpFragment)
            transaction.addToBackStack("signIn")
            transaction.commit()
            dismiss()
        }

        val builder = AlertDialog.Builder(requireActivity())


        builder.setView(view)

        return builder.create()
    }

    // If i should navigate to new fragment after finished logging in perhaps use this
    private fun goToFragment(fragment: Fragment, addToBackStack: Boolean){
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        if (addToBackStack){
            transaction.addToBackStack("signIn")
        }
        transaction.commit()
        dismiss()
    }


    private fun notifyLoginSuccess(){
        val activity = requireActivity()
        if (activity is MainActivity){
            activity.onLoginSuccess()
        }
    }

}