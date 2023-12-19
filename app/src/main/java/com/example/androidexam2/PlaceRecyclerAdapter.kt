package com.example.androidexam2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class PlaceRecyclerAdapter(val context: Context, val places: MutableList<Place>): RecyclerView.Adapter<PlaceRecyclerAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val placeNameTextView = itemView.findViewById<TextView>(R.id.placeNameTextView)
        val rowPlaceImageView = itemView.findViewById<ImageView>(R.id.rowPlaceImageView)
    }

    val inflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var itemView = inflater.inflate(R.layout.place_row_item, parent, false)
        var viewHolder = ItemViewHolder(itemView)
        return viewHolder
    }

    override fun getItemCount() = places.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val place = places[position]

        holder.placeNameTextView.text = place.name

        val storageRef = place.imageURI?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
        Log.d("!!!", storageRef.toString())
        storageRef?.downloadUrl?.addOnSuccessListener { uri ->
            //holder.rowPlaceImageView.layout(0,0,0,0)
            Glide.with(context)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.baseline_question_mark_24)
                .override(200, 200)
                .into(holder.rowPlaceImageView)
        }

    }


}