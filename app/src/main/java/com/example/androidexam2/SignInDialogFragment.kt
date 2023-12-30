package com.example.androidexam2

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth


class SignInDialogFragment(): DialogFragment() {
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var cancelButton: Button
    lateinit var emailTextInputLayout: TextInputLayout
    lateinit var passwordTextInputLayout: TextInputLayout
    var mailHasTextListener = false
    var passwordHasTextListener = false


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.signin_dialog_fragment, null)
        emailEditText = view.findViewById(R.id.mailInputTextView)
        passwordEditText = view.findViewById(R.id.passwordInputTexttView)
        cancelButton = view.findViewById(R.id.cancelButton)
        val signUpTextView = view.findViewById<Button>(R.id.signUpButton)
        emailTextInputLayout = view.findViewById(R.id.signInEmailTextLayout)
        passwordTextInputLayout = view.findViewById(R.id.signInPasswordTextLayout)

        setOnFocusChangeListeners()


        view.findViewById<Button>(R.id.dialogFragmentSignInButton).setOnClickListener {
            signIn()
        }
        cancelButton.setOnClickListener{
            dismiss()
        }

        signUpTextView.setOnClickListener{

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

    private fun setOnFocusChangeListeners() {
        emailEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                val email = emailEditText.text.toString()
                isValidEmail(email)
            }
        }
        passwordEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                val password = passwordEditText.text.toString()
                isValidPassword(password)
            }
        }

    }

    private fun isValidEmail(email: String): Boolean{
        val regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
        return if (email.matches(regex)){
            true
        } else {
            showMailFormatError()
            false
        }
    }

    private fun showMailFormatError(){
        emailTextInputLayout.error = getString(R.string.email_formatted_badly)
        if (!mailHasTextListener) {
            emailEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    // Kod som körs innan texten ändras
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // Kod som körs när texten ändras
                }

                override fun afterTextChanged(emailEditable: Editable) {
                    mailHasTextListener = true
                    val email = emailEditText.text.toString()
                    if (isValidEmail(email)){
                        emailTextInputLayout.error = ""
                    } else{
                        emailTextInputLayout.error = getString(R.string.email_formatted_badly)
                    }
                }
            })
        }
    }
    private fun isValidPassword(password: String): Boolean{
        if (password.length >= 6){
            return true
        }
        showPasswordError()
        return false
    }

    private fun showPasswordError() {
        passwordTextInputLayout.error = getString(R.string.weak_password)

        if (!passwordHasTextListener){
            passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    //Not needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Not needed
                }

                override fun afterTextChanged(passwordEditable: Editable?) {
                    passwordHasTextListener = true
                    if (passwordEditable.toString().length >= 6){
                        passwordTextInputLayout.error = ""
                    } else {
                        passwordTextInputLayout.error = getString(R.string.weak_password)
                    }
                }
            })
        }
    }



    private fun signIn(){
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val validEmail = isValidEmail(email)
        val validPassword = isValidPassword(password)

        if (validEmail && validPassword){
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                notifyLoginSuccess()
                dismiss()
            }
                .addOnFailureListener{e ->

                    showErrorMessage(cancelButton, "Invalid credentials!")
                }
        }
    }

    private fun showErrorMessage(view: View, message: String) {
        Snackbar.make(requireContext(), view, message, Snackbar.LENGTH_SHORT).setAnchorView(view).apply {
            setAction("Dismiss") {
                dismiss()
            }
        }.show()
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