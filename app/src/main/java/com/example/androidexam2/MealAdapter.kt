package com.example.androidexam2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class MealAdapter(
    val context: Context,
    val meals: MutableList<Meal>,
    val listner: onItemClickListner
) : RecyclerView.Adapter<MealAdapter.ItemViewHolder>() {
    interface onItemClickListner {
        fun onItemClick(meal: Meal, setUri: Uri?)
        fun onEditItemClick(meal: Meal)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealNameTextView = itemView.findViewById<TextView>(R.id.mealNameTextView)
        val rowMealImageView = itemView.findViewById<ImageView>(R.id.rowMealImageView)
        val editImageView = itemView.findViewById<ImageView>(R.id.rowItemEditImageView)

    }

    val inflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var itemView = inflater.inflate(R.layout.meal_row_item, parent, false)
        var viewHolder = ItemViewHolder(itemView)
        return viewHolder
    }

    override fun getItemCount() = meals.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val auth = FirebaseAuth.getInstance()
        val meal = meals[position]
        var setUri: Uri? = null

        holder.editImageView.isVisible = meal.creator == auth.currentUser?.uid
        holder.editImageView.setOnClickListener {
            listner.onEditItemClick(meal)
        }

        holder.mealNameTextView.text = meal.name
        holder.itemView.setOnClickListener {
            //showDetailsDialogFragment(place, setUri)
            //goToDetailsActivity(place, setUri)
            listner.onItemClick(meal, setUri)
        }

        val storageRef =
            meal.imageURI?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
        storageRef?.downloadUrl?.addOnSuccessListener { uri ->
            //holder.rowPlaceImageView.layout(0,0,0,0)
            setUri = uri
            Glide.with(context)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.baseline_question_mark_24)
//                .override(200, 200)
                .into(holder.rowMealImageView)
        }


    }

    private fun showDetailsDialogFragment(meal: Meal, setUri: Uri?) {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val dialogFragment = DetailsDialogFragment(meal, setUri)
        dialogFragment.show(fragmentManager, "DetailsFragment")
    }

//    private fun goToEdit(meal: Meal){
//        val bundle = Bundle()
//        bundle.putSerializable("meal", meal)
//        val detailsFragment = DetailsFragment()
//        detailsFragment.arguments = bundle
//        val transaction =
//    }

}