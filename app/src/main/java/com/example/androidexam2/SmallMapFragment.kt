package com.example.androidexam2


import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import android.Manifest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class SmallMapFragment : Fragment(), GoogleMap.OnMapClickListener {


    private val zoom = 15.0f

    private var gpsPoint = mutableListOf<Double>()
    private var marker: Marker? = null
    private lateinit var googleMap: GoogleMap
    private lateinit var getLocationAccess: ActivityResultLauncher<String>
    private val callback = OnMapReadyCallback { map ->
        googleMap = map

        if (gpsPoint != null && gpsPoint.isNotEmpty()) {
            val latLng = LatLng(gpsPoint[0], gpsPoint[1])
            if (latLng != null) {
                if (marker == null) {
                    marker = googleMap.addMarker(MarkerOptions().position(latLng).title("Marker"))
                } else {
                    marker?.position = latLng
                }


                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
                googleMap.animateCamera(cameraUpdate)
            }
        } else {
            checkAndRequestPermission()
        }

        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.uiSettings.isTiltGesturesEnabled = false
        googleMap.setOnMapClickListener(this)
    }

    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        ) {
            enableMyLocation()
        } else {
            getLocationAccess.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, zoom)
                    marker =
                        googleMap.addMarker(MarkerOptions().position(userLatLng).title("Marker"))!!
                    onMapClick(userLatLng)
                    googleMap.animateCamera(cameraUpdate)
                }
            }

        } else {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLocationAccess =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {

                    enableMyLocation()
                } else {

                }
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onMapClick(latLng: LatLng) {
        if (marker == null) {
            marker = googleMap.addMarker(MarkerOptions().position(latLng).title("Marker"))
        } else {
            marker?.position = latLng
        }
        marker?.position = latLng
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

        val parentFragment = parentFragmentManager.findFragmentByTag("createMealFragment")

        if (parentFragment is CreateMealFragment) {
            parentFragment.onMapClick(latLng.latitude, latLng.longitude)
        }
    }
}