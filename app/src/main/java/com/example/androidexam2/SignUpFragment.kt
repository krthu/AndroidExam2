package com.example.androidexam2

import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var passwordEditText: EditText
private lateinit var userNameEditText: EditText
private lateinit var emailEditText: EditText
private lateinit var snackbarAnchor: CoordinatorLayout
lateinit var auth: FirebaseAuth

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        view.findViewById<Button>(R.id.signUpButton).setOnClickListener {
            signUp()
        }
        snackbarAnchor = view.findViewById(R.id.rootCoordinator)

        view.findViewById<FloatingActionButton>(R.id.backSignUpFloatingActionButton)
            .setOnClickListener {
                parentFragmentManager.popBackStack()
            }


    }

    private fun signUp() {
        val email = emailEditText.text.toString()
        val userName = userNameEditText.text.toString()
        val password = passwordEditText.text.toString()

        Log.d("!!!", "${email.isEmpty()} ${userName.isEmpty()} ${password.isEmpty()}")

        if (email.isEmpty() || userName.isEmpty() || password.isEmpty()) {

            when {
                email.isEmpty() -> {
                    showMessage(snackbarAnchor, R.string.emailEmpty)
                    return
                }

                userName.isEmpty() -> {
                    showMessage(snackbarAnchor, R.string.userNameEmpty)
                    return
                }

                password.isEmpty() -> {
                    showMessage(snackbarAnchor, R.string.passwordEmpty)
                    return
                }
            }
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