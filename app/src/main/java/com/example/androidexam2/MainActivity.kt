package com.example.androidexam2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var fragmentContainer: FrameLayout
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        fragmentContainer = findViewById(R.id.fragmentContainer)
        loadFragment()
        bottomNavigationView.setOnItemSelectedListener {menuItem ->
            when (menuItem.itemId) {
                R.id.item_1 -> {
                        loadFragment()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun loadFragment(){
        val listFragment = ListFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, listFragment)
        fragmentTransaction.commit()
    }

}