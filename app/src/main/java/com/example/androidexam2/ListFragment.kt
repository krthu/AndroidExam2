package com.example.androidexam2

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class ListFragment : Fragment(), MealAdapter.onItemClickListner {

    lateinit var createMealFloatingActionButton: FloatingActionButton
    lateinit var auth: FirebaseAuth
    val meals = mutableListOf<Meal>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createMealFloatingActionButton =
            view.findViewById(R.id.createMealButtonFloatingActionButton)


        val adapter = MealAdapter(requireContext(), meals, this)
        val recycler = view.findViewById<RecyclerView>(R.id.mealRecyclerView)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
        auth = Firebase.auth

        val db = FirebaseFirestore.getInstance()
        db.collection("meals").addSnapshotListener { snapshot, error ->
            if (snapshot != null) {
                meals.clear()
                for (document in snapshot.documents) {
                    if (document != null) {
                        val meal = document.toObject<Meal>()
                        if (meal != null) {
                            meals.add(meal)
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }

        createMealFloatingActionButton.setOnClickListener {
            if (auth.currentUser == null) {
                val dialogFragment = SignInDialogFragment()
                dialogFragment.show(requireActivity().supportFragmentManager, "signIn")


            } else {
                goToCreateMealFragment()
            }

        }

    }

    fun goToCreateMealFragment() {
        val createMealFragment = CreateMealFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.replace(R.id.fragmentContainer, createMealFragment, "createMealFragment")
        transaction.commit()
    }

    override fun onResume() {
        super.onResume()
        activity?.supportFragmentManager?.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onItemClick(meal: Meal, setUri: Uri?) {
        val bundle = Bundle()
        bundle.putSerializable("meal", meal)

        bundle.putString("uri", setUri.toString())
        val detailsFragment = DetailsFragment()
        startTransaction(bundle, detailsFragment)

    }

    override fun onEditItemClick(meal: Meal) {
        val bundle = Bundle()
        bundle.putSerializable("meal", meal)
        val createMealFragment = CreateMealFragment()
        startTransaction(bundle, createMealFragment)
    }

    fun startTransaction(bundle: Bundle, fragment: Fragment) {

        fragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        if (fragment is CreateMealFragment) {
            transaction.replace(R.id.fragmentContainer, fragment, "createMealFragment")
        } else {
            transaction.replace(R.id.fragmentContainer, fragment)
        }
        transaction.commit()
    }


}