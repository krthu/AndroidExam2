package com.example.androidexam2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var passwordEditText: EditText
private lateinit var userNameEditText: EditText
private lateinit var emailEditText: EditText
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

        passwordEditText = view.findViewById(R.id.passwordEditText)
        userNameEditText = view.findViewById(R.id.userNameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        view.findViewById<Button>(R.id.signUpButton).setOnClickListener {
            signUp()
        }
        view.findViewById<Button>(R.id.signInButton).setOnClickListener {
            signIn()
        }

        view.findViewById<FloatingActionButton>(R.id.backSignUpFloatingActionButton)
            .setOnClickListener {
                parentFragmentManager.popBackStack()
            }

        auth = Firebase.auth
    }

    private fun signUp() {
        val email = emailEditText.text.toString()
        val userName = userNameEditText.text.toString()
        val password = passwordEditText.text.toString()
        Log.d("!!!", "${email.isEmpty()} ${userName.isEmpty()} ${password.isEmpty()}")

        if (email.isEmpty() || userName.isEmpty() || password.isEmpty()) {

            when {
                email.isEmpty() -> {
                    Toast.makeText(requireContext(), R.string.emailEmpty, Toast.LENGTH_LONG).show()
                    return
                }

                userName.isEmpty() -> {
                    Toast.makeText(requireContext(), R.string.userNameEmpty, Toast.LENGTH_LONG)
                        .show()
                    return
                }

                password.isEmpty() -> {
                    Toast.makeText(requireContext(), R.string.passwordEmpty, Toast.LENGTH_LONG)
                        .show()
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
                        parentFragmentManager.popBackStack()
                    } else {
                        Log.d("!!!", "User not created ${signup.exception}")
                    }
                }
        }
    }

    fun signIn() {
        val email = emailEditText.text.toString()

        val password = passwordEditText.text.toString()
        if (email.isEmpty() || password.isEmpty()) {

            when {
                email.isEmpty() -> {
                    Toast.makeText(requireContext(), R.string.emailEmpty, Toast.LENGTH_LONG).show()
                    return
                }

                password.isEmpty() -> {
                    Toast.makeText(requireContext(), R.string.passwordEmpty, Toast.LENGTH_LONG)
                        .show()
                    return
                }
            }
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { signIn ->
                    if (signIn.isSuccessful) {
                        parentFragmentManager.popBackStack()
                    }
                }
        }
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