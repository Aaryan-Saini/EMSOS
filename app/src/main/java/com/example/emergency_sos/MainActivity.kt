package com.example.emergency_sos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.emergency_sos.ui.MainScreen
import com.example.emergency_sos.ui.RescuerDashboard
import com.example.emergency_sos.ui.UserDashboard
import com.example.emergency_sos.ui.theme.EmergencySOSTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            EmergencySOSTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "mainScreen"
                ) {
                    composable("mainScreen") {
                        MainScreen(
                            navController = navController,
                            onSOSClick = { sendSOSAlert() }
                        )
                    }
                    composable("userDashboard") {
                        UserDashboard()
                    }
                    composable("rescuerDashboard") {
                        RescuerDashboard(credits = 0)
                    }
                }
            }
        }
    }

    private fun sendSOSAlert() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val locationData = hashMapOf(
                            "lat" to location.latitude,
                            "long" to location.longitude
                        )

                        val sosData = hashMapOf(
                            "userId" to (FirebaseAuth.getInstance().currentUser?.uid ?: "Anonymous"),
                            "location" to locationData,
                            "timestamp" to System.currentTimeMillis(),
                            "status" to "active"
                        )

                        db.collection("SOS_logs")
                            .add(sosData)
                            .addOnSuccessListener {
                                Log.d("SOS", "SOS alert sent successfully.")
                                Toast.makeText(this, "SOS Alert Sent!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e("SOS", "Error sending SOS", e)
                                Toast.makeText(this, "Failed to send SOS alert", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Unable to fetch location.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Location", "Failed to get location", e)
                    Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
                }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }
}
