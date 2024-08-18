package com.wganizo.weather

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.wganizo.weather.databinding.ActivityMainBinding
import com.wganizo.weather.sqlite.PreferencesDatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_weather_forecast
            )
        )
        navView.setupWithNavController(navController)

        // Set up item selection listener
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_preferences -> {
                    showUnitSelectionDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun showUnitSelectionDialog() {
        val options = arrayOf("Metric", "Imperial")

        val dbHelper = PreferencesDatabaseHelper(applicationContext)
        AlertDialog.Builder(this)
            .setTitle("Select Unit Option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        Toast.makeText(this, getString(R.string.metric_selected), Toast.LENGTH_SHORT).show()
                        dbHelper.updateUnitPreference("metric")
                    }
                    1 -> {
                        Toast.makeText(this,
                            getString(R.string.imperial_selected), Toast.LENGTH_SHORT).show()
                        dbHelper.updateUnitPreference("imperial")
                    }
                }
                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
            }
            .show()
    }
}
