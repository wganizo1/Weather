package com.wganizo.weather.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.wganizo.weather.R
import com.wganizo.weather.databinding.FragmentHomeBinding
import com.wganizo.weather.utils.LocationUtils

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationUtils: LocationUtils
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private lateinit var homeViewModel: HomeViewModel
    private var lat: Double? = null
    private var lon: Double? = null
    private var cityName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Using the factory to instantiate HomeViewModel
        val factory = HomeViewModelFactory(requireContext())
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val locationManager = requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        locationUtils = LocationUtils(requireContext(), locationManager, LocationServices.getFusedLocationProviderClient(requireContext()))

        // Initialize map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // Observe the weather data from ViewModel
        homeViewModel.weatherText.observe(viewLifecycleOwner) { weatherInfo ->
           // binding.textHome.text = weatherInfo
        }

        return root
    }

    private fun getCurrentLocation(onLocationRetrieved: (LatLng) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle the case where permissions are not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                lat = location.latitude
                lon = location.longitude
                val currentLocation = LatLng(lat!!, lon!!)
                onLocationRetrieved(currentLocation)
            }
        }.addOnFailureListener {
            // Handle the case where location is not retrieved
            println("Failed to retrieve location: $it")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Custom InfoWindowAdapter to display the weather details
        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            private val infoWindow: View = layoutInflater.inflate(R.layout.custom_info_window, null)

            override fun getInfoWindow(marker: Marker): View? {
                return null // Return null to use default info window frame
            }

            override fun getInfoContents(marker: Marker): View {
                val locationTextView = infoWindow.findViewById<TextView>(R.id.locationTextView)
                val tempTextView = infoWindow.findViewById<TextView>(R.id.tempTextView)
                val humidityTextView = infoWindow.findViewById<TextView>(R.id.humidityTextView)

                locationTextView.text = locationUtils.getCityName(marker.position.latitude, marker.position.longitude)
                tempTextView.text = marker.snippet?.split("\n")?.get(0)
                humidityTextView.text = marker.snippet?.split("\n")?.get(1)

                return infoWindow
            }
        })

        getCurrentLocation { currentLocation ->
            // Fetch weather data and update the map with markers
            homeViewModel.fetchWeatherData(currentLocation.latitude, currentLocation.longitude) { weather ->
                val city = locationUtils.getCityName(currentLocation.latitude, currentLocation.longitude)
                cityName = city
                val marker = mMap.addMarker(
                    MarkerOptions().position(currentLocation)
                        .title("Weather in $cityName")
                        .snippet("Temperature: ${weather.temp}°\nHumidity: ${weather.humidity}%")
                )
                marker?.showInfoWindow() // Show the info window immediately
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10f))
            }
        }

        mMap.setOnMapClickListener { latLng ->
            // Add a marker at the clicked location
            val markerOptions = MarkerOptions().position(latLng)
            val marker = mMap.addMarker(markerOptions)

            // Fetch weather data for the clicked location
            homeViewModel.fetchWeatherData(latLng.latitude, latLng.longitude) { weather ->
                val city = locationUtils.getCityName(latLng.latitude, latLng.longitude)
                cityName = city
                marker?.title = "Weather in $city"
                marker?.snippet = "Temperature: ${weather.temp}°\nHumidity: ${weather.humidity}%"
                marker?.showInfoWindow()
            }

            // Move the camera to the clicked location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
        }

        mMap.setOnInfoWindowClickListener { marker ->
            val bundle = Bundle().apply {
                putFloat("latitude", marker.position.latitude.toFloat())
                putFloat("longitude", marker.position.longitude.toFloat())
                putString("place", cityName.toString())
            }
            findNavController().navigate(R.id.navigation_weather_forecast, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
