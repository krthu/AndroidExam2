package com.example.androidexam2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaceRecyclerAdapter(context: Context, val places: MutableList<Place>): RecyclerView.Adapter<PlaceRecyclerAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val placeNameTextView = itemView.findViewById<TextView>(R.id.placeNameTextView)
    }

    val inflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = places.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


}