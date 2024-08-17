package com.wganizo.weather.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationUtils(
    private val context: Context,
    private val locationManager: LocationManager,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    private var onLocationChangedCallback: ((Location) -> Unit)? = null

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(activity: AppCompatActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0f, this
                    )
                }
            } else {
                Toast.makeText(context, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            requestPermissions(context as AppCompatActivity)
        }
    }

    override fun onLocationChanged(location: Location) {
        onLocationChangedCallback?.invoke(location)
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("LocationUtils", "Provider $provider disabled")
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("LocationUtils", "Provider $provider enabled")
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.d("LocationUtils", "Provider $provider status changed to $status")
    }

    fun getCityName(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context)
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.locality
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
