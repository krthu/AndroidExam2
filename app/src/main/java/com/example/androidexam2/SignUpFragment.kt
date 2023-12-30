package com.example.androidexam2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"







/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var passwordEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordTextInputLayout: TextInputLayout
    private lateinit var userNameTextInputLayout: TextInputLayout
    private lateinit var emailTextInputLayout: TextInputLayout


    private lateinit var snackbarAnchor: CoordinatorLayout
    lateinit var auth: FirebaseAuth

    private var mailHasTextListener = false
    private var passwordHasTextListener = false
    private var userNameHasTextListener = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        passwordEditText = view.findViewById(R.id.passwordEditText)
        userNameEditText = view.findViewById(R.id.userNameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordTextInputLayout = view.findViewById(R.id.passwordTextInputLayout)
        userNameTextInputLayout = view.findViewById(R.id.userNameTextInputLayout)
        emailTextInputLayout = view.findViewById(R.id.emailTextInputLayout)

        setOnFocusChangeListeners()

        view.findViewById<Button>(R.id.signUpButton).setOnClickListener {
            signUp()
        }
        snackbarAnchor = view.findViewById(R.id.rootCoordinator)

        view.findViewById<FloatingActionButton>(R.id.backSignUpFloatingActionButton)
            .setOnClickListener {
                parentFragmentManager.popBackStack()
            }


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
        userNameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                val userName = userNameEditText.text.toString()
                isValidUserName(userName)
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

    private fun isValidUserName(userName: String): Boolean {

        if (userName.isNotEmpty()){
            return true
        }
        showUserNameError()
        return false
    }

    private fun showUserNameError() {
        userNameTextInputLayout.error = getString(R.string.userNameEmpty)
        if (!userNameHasTextListener) {
            userNameEditText.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    //Not needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Not needed
                }

                override fun afterTextChanged(s: Editable?) {
                    userNameHasTextListener = true
                    if (s.toString().isNotEmpty()){
                        userNameTextInputLayout.error = ""
                    }
                }

            })
        }
    }


    private fun signUp() {
        val email = emailEditText.text.toString()
        val userName = userNameEditText.text.toString()
        val password = passwordEditText.text.toString()

        val validEmail = isValidEmail(email)
        val validPassword = isValidPassword(password)
        val validUserName = isValidUserName(userName)

        if (!validEmail || !validPassword || !validUserName){
            isValidPassword(password)
            isValidUserName(userName)
            return

        } else {
            Log.d("!!!", "Nothing is empty")
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { signup ->
                    if (signup.isSuccessful) {
                        Log.d("!!!", "User SignedUp success")
                        val user = User(userId = auth.currentUser?.uid, userName = userName)
                        val db = FirebaseFirestore.getInstance()
//                    db.collection("users").add(user)
                        user.userId?.let { db.collection("users").document(it).set(user) }
                        notifyLoginSuccess()
                        parentFragmentManager.popBackStack()
                    } else {
                        val errorCode = (signup.exception as FirebaseAuthException).errorCode
                        //showMessage(snackbarAnchor, error.message)
                        when (errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                showMessage(snackbarAnchor, R.string.email_formatted_badly)
                            }
                            "ERROR_WEAK_PASSWORD" ->  {
                                showMessage(snackbarAnchor, R.string.weak_password)
                            }

                            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                showMessage(snackbarAnchor, R.string.email_in_use)
                            }

                        }


                        Log.d("!!!", "User not created ${errorCode}")

                    }
                }
        }
    }



    private fun notifyLoginSuccess(){
        val activity = requireActivity()
        if (activity is MainActivity){
            activity.onLoginSuccess()
        }
    }

    private fun showMessage(view: View, stringID: Int) {
        val message = getString(stringID)
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAnchorView(snackbarAnchor).apply {
            setAction("Dismiss") {
                dismiss()
            }
        }.show()
    }

    private fun showMessage(view: View, message: String?) {
        //val message = getString(showMessage())
        Snackbar.make(view, message.toString(), Snackbar.LENGTH_SHORT).setAnchorView(snackbarAnchor).apply {
            setAction("Dismiss") {
                dismiss()
            }
        }.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}