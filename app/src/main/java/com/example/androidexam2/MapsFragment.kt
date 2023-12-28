package com.example.androidexam2

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class MapsFragment : Fragment(), GoogleMap.OnInfoWindowClickListener {
    private val REQUEST_LOCATION = 2
    private var chosenMeal: Meal? = null
    private val meals = mutableListOf<Meal>()
    private val zoomLevel = 13.0f
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var getLocationAccess: ActivityResultLauncher<String>
    private lateinit var map: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->

        val customMapInfoWindowAdapter = CustomMapInfoWindowAdapter(requireContext())
        //googleMap.setInfoWindowAdapter(customMapInfoWindowAdapter)
        map = googleMap
        checkAndRequestPermission(googleMap)

        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        if (chosenMeal != null) {
            addOneMarker(googleMap)
        } else {

            getListOfMeals()
        }
        googleMap.setOnInfoWindowClickListener(this)
    }

    private fun placeListOfMarkers(googleMap: GoogleMap) {
        for (meal in meals) {
            if (meal.gpsArray?.size != 0) {
                val lat = meal.gpsArray?.get(0)
                val lng = meal.gpsArray?.get(1)
                if (lat != null && lng != null) {
                    val latLng = LatLng(lat, lng)
                    val marker =
                        googleMap.addMarker(MarkerOptions().position(latLng).title(meal.name))
                    marker?.tag = meal
                }
            }
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED
        ) {
            locationProvider.lastLocation.addOnSuccessListener { location ->
                val userLatLng = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, zoomLevel))
            }
        }

    }

    fun addOneMarker(googleMap: GoogleMap) {
        val lat = chosenMeal?.gpsArray?.get(0)
        val long = chosenMeal?.gpsArray?.get(1)

        if (lat != null && long != null) {
            Log.d("!!!", "$lat & $long")
            val mealLatLng = LatLng(lat, long)


            val marker = googleMap.addMarker(MarkerOptions().position(mealLatLng).title(chosenMeal?.name))
            marker?.tag = chosenMeal
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mealLatLng, zoomLevel))
            if (map.isMyLocationEnabled) {
                locationProvider.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {

                        val userLatLng = LatLng(location.latitude, location.longitude)
                        val builder = LatLngBounds.Builder()
                        builder.include(userLatLng)
                        builder.include(mealLatLng)
                        val bounds = builder.build()
                        val padding = 100
                        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                        googleMap.animateCamera(cameraUpdate)

                        val distance = FloatArray(1)
                        Location.distanceBetween(
                            mealLatLng.latitude, mealLatLng.longitude,
                            userLatLng.latitude, userLatLng.longitude, distance
                        )
                        val distanceInKm = distance[0] / 1000
                        Log.d("!!!", distanceInKm.toString())
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationProvider = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocationAccess =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Log.d("!!!", "We have permission")
                    map.isMyLocationEnabled = true
                }
            }

        arguments?.let {
            chosenMeal = it.getSerializable("meal") as Meal?
        }

    }

    private fun checkAndRequestPermission(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
            getLocationAccess.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            googleMap.isMyLocationEnabled = true
        }
    }

    private fun getListOfMeals() {
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
            placeListOfMarkers(map)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        val mapsBackButton =
            view.findViewById<FloatingActionButton>(R.id.mapsBackFloatingActionButton)
        mapsBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        val detailsFragment = DetailsFragment()
        val bundle = Bundle()
        val markerMeal = marker.tag as? Meal
        if (markerMeal != null){
            bundle.putSerializable("meal", markerMeal)
            detailsFragment.arguments = bundle
        }
        Log.d("!!!", "${markerMeal?.name.toString()} Från window clicked")
        val transaction = parentFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.replace(R.id.fragmentContainer, detailsFragment)
        transaction.commit()
    }


}