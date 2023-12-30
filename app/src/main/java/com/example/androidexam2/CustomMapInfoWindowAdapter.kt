package com.example.androidexam2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.CompletableFuture
import javax.sql.DataSource

import com.bumptech.glide.request.target.Target
import android.graphics.drawable.Drawable

class CustomMapInfoWindowAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View? {
        val meal = marker.tag as? Meal ?: return null

//        Log.d("!!!", "Meal from Tag ${meal}")
//
        val view = LayoutInflater.from(context).inflate(R.layout.map_info_label, null)
//        val titleTextView: TextView = view.findViewById(R.id.mapInfoTitleTextView)
//        val mapInfoImageView: ImageView = view.findViewById(R.id.mapInfoLabelImageView)
//        titleTextView.text = marker.title
//        val storage = FirebaseStorage.getInstance()
//
//        val storageRef = meal.imageURI?.let { storage.getReferenceFromUrl(it) }
//        val imageDownloadFuture = CompletableFuture<Unit>()
//        storageRef?.downloadUrl?.addOnSuccessListener { uri ->
//            Log.d("!!!", "ImageDownloaded")
//            Glide.with(context)
//                .load(uri)
//                .centerCrop()
//                .listener(object : RequestListener<Drawable> {
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable,
//                        model: Any,
//                        target: com.bumptech.glide.request.target.Target<Drawable>?,
//                        dataSource: com.bumptech.glide.load.DataSource,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        marker.showInfoWindow()
//                        return false
//                    }
//                })
//                .into(mapInfoImageView)
//            imageDownloadFuture.complete(null)
//
//        }?.addOnFailureListener {
//            imageDownloadFuture.completeExceptionally(it)
//        }
        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }



}