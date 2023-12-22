package com.example.androidexam2

import android.content.ClipData.Item
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem

import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var fragmentContainer: FrameLayout
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.addOnBackStackChangedListener(fragmentManagerListener)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        fragmentContainer = findViewById(R.id.fragmentContainer)
        loadFragment(ListFragment(), false)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_1 -> {
                    val listFragment = ListFragment()
                    loadFragment(listFragment, false)
                    true
                }
                R.id.item_2 -> {
                    val mapFragment = MapsFragment()
                    loadFragment(mapFragment, true)
                    true
                }

                R.id.item_3 -> {
                    intent = Intent(this, AddLocationActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private val fragmentManagerListener  = FragmentManager.OnBackStackChangedListener {
        Log.d("!!!", "Fragment Changed")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment is ListFragment){
            Log.d("!!!", "Fragment Changed it is List")
            bottomNavigationView.selectedItemId = R.id.item_1
        } else if (currentFragment is DetailsFragment){
            Log.d("!!!", "Fragment Changed it is Details")
            bottomNavigationView.selectedItemId = 0
        }
    }


    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (addToBackStack){
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

}