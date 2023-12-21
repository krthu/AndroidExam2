package com.example.androidexam2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment(), MealAdapter.onItemClickListner {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var createMealFloatingActionButton: FloatingActionButton
    lateinit var auth: FirebaseAuth
    val meals = mutableListOf<Meal>()
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
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createMealFloatingActionButton = view.findViewById(R.id.createMealButtonFloatingActionButton)
        view.findViewById<Button>(R.id.profileButton).setOnClickListener {
            // TODO: Fixa intent för fragment
//            val intent = Intent(this, SignUpActivity::class.java)
//            startActivity(intent)
        }


        val adapter = MealAdapter(requireContext(), meals, this)
        val recycler = view.findViewById<RecyclerView>(R.id.mealRecyclerView)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)
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
        //createPlaceButton = findViewById<Button>(R.id.createPlaceButton)

//        val auth = Firebase.auth
        //        auth.signOut()
//        if (auth.currentUser != null){
//            createPlaceButton.isVisible = true
//        }
        createMealFloatingActionButton.setOnClickListener {
            // TODO: Fixa intent för fragment
//            val intent = Intent(this, CreateMealActivity::class.java)
//            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            createMealFloatingActionButton.isVisible = true
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(meal: Meal, setUri: Uri?) {
        val bundle = Bundle()
        bundle.putSerializable("meal", meal)

        bundle.putString("uri", setUri.toString())
        startTransaction(bundle)

    }

    fun startTransaction(bundle: Bundle){
        val detailsFragment = DetailsFragment()
        detailsFragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.replace(R.id.fragmentContainer, detailsFragment)
        transaction.commit()
    }


}