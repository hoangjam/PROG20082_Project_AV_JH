package com.example.prog20082_project_av_jh.views

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.prog20082_project_av_jh.R
import com.example.prog20082_project_av_jh.locationservices.LocationManager
import com.example.prog20082_project_av_jh.ui.MatchedProfileFragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = this@DisplayMapActivity.toString()

    private lateinit var locationManager: LocationManager
    private lateinit var location: Location
    private lateinit var currentLocation: LatLng
    private var map: GoogleMap? = null
    private val DEFAULT_ZOOM: Float = 20F
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_map)

        this.locationManager = LocationManager(this@DisplayMapActivity)
        this.currentLocation = LatLng(0.0, 0.0)

        if (LocationManager.locationPermissionsGranted) {
            this.getLastLocation()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                for(location in locationResult.locations){
                    currentLocation = LatLng(location.latitude, location.longitude)
                    addMarkerOnMap(currentLocation)
                }
            }
        }
    }

    override fun onResume(){
        super.onResume()
        locationManager.requestLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        locationManager.fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }



    private fun getLastLocation() {
        this.locationManager.getLastLocation()?.observe(this, { loc: Location? ->
            if (loc != null) {
                this.location = loc
                this.currentLocation = LatLng(location.latitude, location.longitude)
                println(this.currentLocation.toString())
                this.addMarkerOnMap(this.currentLocation)
            }
        })
    }

    private fun addMarkerOnMap(location: LatLng) {
        if (this.map != null) {
            this.map!!.addMarker(
                MarkerOptions().position(location).title("DOG HERE")
            )
            this.map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM))
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        this.getLastLocation()

        if(googleMap != null){
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isScrollGesturesEnabled = true

            googleMap.addMarker(
                MarkerOptions().position(this.currentLocation).title("Dog is here")
            )
        }

        // arbitrarily move the camera to a matched dog position?
        
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}