package com.example.androidexam2


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.FrameLayout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class MainActivity : AppCompatActivity() {

    lateinit var fragmentContainer: FrameLayout
    lateinit var bottomNavigationView: BottomNavigationView
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.addOnBackStackChangedListener(fragmentManagerListener)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        fragmentContainer = findViewById(R.id.fragmentContainer)

        loadFragment(ListFragment(), false)

        setUserNameInMenu()
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_1 -> {
                    val listFragment = ListFragment()
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    loadFragment(listFragment, false)
                    true
                }

                R.id.item_2 -> {
                    val mapFragment = MapsFragment()
                    loadFragment(mapFragment, false)
                    true
                }

                R.id.item_3 -> {
                    if (auth.currentUser != null) {
                        val userFragment = UserComposeFragment()
                        loadFragment(userFragment, false)
                        true
                    } else {
                        val dialogFragment = SignInDialogFragment()
                        dialogFragment.show(supportFragmentManager, "signIn")

                        false
                    }
                }

                else -> {
                    false
                }
            }
        }
    }

    private val fragmentManagerListener = FragmentManager.OnBackStackChangedListener {
// Makes sure the right indicator is shown in the bottom navigation
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment is ListFragment) {
            bottomNavigationView.selectedItemId = R.id.item_1
        }
    }


    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    fun onLoginSuccess() {
        Snackbar.make(this, fragmentContainer, getString(R.string.signed_in), Snackbar.LENGTH_SHORT)
            .setAnchorView(fragmentContainer.findViewById(R.id.createMealButtonFloatingActionButton))
            .apply {
                setAction(getString(R.string.dismiss)) {
                    dismiss()
                }
            }.show()
        setUserNameInMenu()
    }

    private fun setUserNameInMenu() {

        val db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get().addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject<User>()
                    bottomNavigationView.menu.findItem(R.id.item_3).title = user?.userName

                }
            }
        }
    }


    fun onLogOut() {
        bottomNavigationView.menu.findItem(R.id.item_3).title = "User"
        bottomNavigationView.menu.findItem(R.id.item_1)?.isChecked = true

    }

    private fun goToListFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val listFragment = ListFragment()
        transaction.replace(R.id.fragmentContainer, listFragment).commit()

    }



}