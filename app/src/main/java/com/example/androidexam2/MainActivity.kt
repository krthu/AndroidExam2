package com.example.androidexam2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.FrameLayout


class MainActivity : AppCompatActivity() {

    lateinit var fragmentContainer: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentContainer = findViewById(R.id.fragmentContainer)
        val listFragment = ListFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, listFragment)
        fragmentTransaction.commit()
    }

}