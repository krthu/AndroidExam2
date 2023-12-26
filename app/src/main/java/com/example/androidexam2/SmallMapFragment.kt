package com.example.androidexam2

import android.content.Context
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class SmallMapFragment : Fragment(), GoogleMap.OnMapClickListener {

    interface MapClickListener {
        fun onMapClick(latitude: Double, longitude: Double)
    }
    private var mapClickListener: MapClickListener? = null
    private var gpsPoint = mutableListOf<Double>()
    private lateinit var marker: Marker
    private lateinit var googleMap: GoogleMap
    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        Log.d("!!!", "From Smallmapfragment ${gpsPoint?.get(0)}, ${gpsPoint?.get(1)}")
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        val latLng = LatLng(gpsPoint[0], gpsPoint[1])
        if (latLng != null){
            marker = googleMap.addMarker(MarkerOptions().position(latLng).title("Marker"))!!
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom( latLng, 15.0f)
            googleMap.animateCamera(cameraUpdate)


        }
       // googleMap.uiSettings.isScrollGesturesEnabled = false
       // googleMap.uiSettings.isZoomGesturesEnabled = false
        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.uiSettings.isTiltGesturesEnabled = false
        googleMap.setOnMapClickListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MapClickListener){
            mapClickListener = context
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_small_map, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            gpsPoint.add(it.getDouble("lat"))
            gpsPoint.add(it.getDouble("lng"))
        }
        //mapClickListener = parentFragment as? MapClickListener
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onMapClick(latLng: LatLng) {
        marker.position = latLng
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

        val parentFragment = parentFragmentManager.findFragmentByTag("createMealFragment")

        if (parentFragment is CreateMealFragment){
            parentFragment.onMapClick(latLng.latitude, latLng.longitude)
        }
    }
}