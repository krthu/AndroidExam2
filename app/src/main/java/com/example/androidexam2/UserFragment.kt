package com.example.androidexam2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth



class UserFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.signOutButton). setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val activity = requireActivity()
            if (activity is MainActivity){
                activity.onLogOut()
            }

            auth.signOut()
            goToListFragment()
        }

    }

    private fun goToListFragment(){
        val transaction = parentFragmentManager.beginTransaction()
        val listFragment = ListFragment()
        transaction.replace(R.id.fragmentContainer, listFragment).commit()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}