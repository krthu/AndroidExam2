package com.example.androidexam2

import android.Manifest
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.memoryLruGcSettings

class MapsFragment : Fragment() {
    private val REQUEST_LOCATION = 2
    private var meal: Meal? = null
    private val zoomLevel = 10.0f
    private lateinit var loactionProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var getLocationAccess: ActivityResultLauncher<String>
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
          
        } else  {
            googleMap.isMyLocationEnabled = true
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        Log.d("!!!", "${meal}")
        val lat = meal?.gpsArray?.get(0)
        val long = meal?.gpsArray?.get(1)

        if (lat != null && long != null){
            Log.d("!!!", "$lat & $long")
            val mealLatLng = LatLng(lat, long)

            googleMap.addMarker(MarkerOptions().position(mealLatLng).title(meal?.name))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mealLatLng, zoomLevel))


        }

//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loactionProvider = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.Builder(2000).build()


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations){
                    Log.d("!!!", "Lat: ${location.latitude}, lng: ${location.longitude}")
                }
            }
        }
        getLocationAccess = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted){
                startLocationUpdate()
            }
        }
        checkAndRequestPermission()
        arguments?.let {
            meal = it.getSerializable("meal") as Meal?
            Log.d("!!!", meal.toString())
        }
    }

    private fun checkAndRequestPermission(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PERMISSION_GRANTED) {
            getLocationAccess.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            Log.d("!!!", "After Location launch")
        } else {
            startLocationUpdate()
        }
    }
    private fun startLocationUpdate() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
            loactionProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun stopLocationUpdates(){
        loactionProvider.removeLocationUpdates(locationCallback)
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
        val mapsBackButton = view.findViewById<FloatingActionButton>(R.id.mapsBackFloatingActionButton)
        mapsBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

}