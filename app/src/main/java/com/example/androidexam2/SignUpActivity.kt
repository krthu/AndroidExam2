package com.example.androidexam2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var passwordEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var emailEditText: EditText
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        passwordEditText = findViewById(R.id.passwordEditText)
        userNameEditText = findViewById(R.id.userNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        findViewById<Button>(R.id.signUpButton).setOnClickListener{
            signUp()
        }

        auth = Firebase.auth



    }

    private fun signUp() {
        val email = emailEditText.text.toString()
        val userName = userNameEditText.text.toString()
        val password = passwordEditText.text.toString()
        Log.d("!!!" ,"${email.isEmpty()} ${userName.isEmpty()} ${password.isEmpty()}")

        if (email.isEmpty() || userName.isEmpty() || password.isEmpty()) {

            when {
                email.isEmpty() -> {
                    Toast.makeText(this, R.string.emailEmpty, Toast.LENGTH_LONG).show()
                    return
                }

                userName.isEmpty() -> {
                    Toast.makeText(this, R.string.userNameEmpty, Toast.LENGTH_LONG).show()
                    return
                }

                password.isEmpty() -> {
                    Toast.makeText(this, R.string.passwordEmpty, Toast.LENGTH_LONG).show()
                    return
                }
            }
        }
        else {
            Log.d("!!!", "Nothing is empty")
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { signup ->
                if (signup.isSuccessful) {
                    Log.d("!!!", "User SignedUp success")
                    val user = User(userId = auth.currentUser?.uid, userName = userName)
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").add(user)
                } else {
                    Log.d("!!!", "User not created ${signup.exception}")
                }
            }
        }
    }
}