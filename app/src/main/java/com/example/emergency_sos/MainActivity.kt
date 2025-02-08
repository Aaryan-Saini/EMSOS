package com.example.emergency_sos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.emergency_sos.ui.MainScreen
import com.example.emergency_sos.ui.RescuerDashboard
import com.example.emergency_sos.ui.UserDashboard
import com.example.emergency_sos.ui.theme.EmergencySOSTheme
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember



data class SOSAlert(val latitude: Double, val longitude: Double)

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkGooglePlayServices()) {
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = FirebaseAuth.getInstance()

        setContent {
            EmergencySOSTheme {
                val navController = rememberNavController()
                val activeSOSAlerts = remember { mutableStateOf(emptyList<SOSAlert>()) } // State to hold alerts

                // Fetch alerts and update state
                getActiveSOSAlerts { alerts ->
                    activeSOSAlerts.value = alerts
                }

                NavHost(
                    navController = navController,
                    startDestination = "mainScreen"
                ) {
                    composable("mainScreen") {
                        MainScreen(
                            navController = navController,
                            onSOSClick = { onSendSOSButtonClick() },
                            onFetchAlertsClick = { getActiveSOSAlerts { /* handle alerts */ } },
                            onPhoneAuthClick = { println("Phone Auth Clicked!") }
                        )
                    }
                    composable("userDashboard") {
                        UserDashboard()
                    }
                    composable("rescuerDashboard") {
                        RescuerDashboard(
                            credits = 0,
                            activeSOSAlerts = activeSOSAlerts.value // Pass updated alerts
                        )
                    }
                }
            }
        }
    }



    private fun checkGooglePlayServices(): Boolean {
        val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (result != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Please update Google Play Services.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun onSendSOSButtonClick() {
        if (!isLocationEnabled()) {
            Toast.makeText(this, "Please enable location services.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Anonymous"
                        sendSOSAlert(userId, location.latitude, location.longitude)
                    } else {
                        requestCurrentLocation()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Location", "Error fetching location", e)
                    Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestCurrentLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            if (location != null) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Anonymous"
                sendSOSAlert(userId, location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "Unable to fetch location.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("Location", "Error fetching current location", e)
            Toast.makeText(this, "Error fetching current location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSOSAlert(userId: String, latitude: Double, longitude: Double) {
        val sosData = hashMapOf(
            "userId" to userId,
            "location" to hashMapOf("lat" to latitude, "long" to longitude),
            "timestamp" to System.currentTimeMillis(),
            "status" to "active"
        )

        db.collection("SOS_logs").add(sosData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "SOS Alert Sent! ID: ${documentReference.id}")
                Toast.makeText(this, "SOS Sent Successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error sending SOS", e)
                Toast.makeText(this, "Failed to Send SOS!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getActiveSOSAlerts(onResult: (List<SOSAlert>) -> Unit) {
        db.collection("SOS_logs")
            .whereEqualTo("status", "active")
            .get()
            .addOnSuccessListener { documents ->
                val sosAlerts = documents.mapNotNull { document ->
                    val location = document.get("location") as? Map<*, *>
                    val latitude = location?.get("lat") as? Double
                    val longitude = location?.get("long") as? Double
                    if (latitude != null && longitude != null) {
                        SOSAlert(latitude, longitude)
                    } else {
                        null
                    }
                }
                onResult(sosAlerts)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching SOS alerts", e)
                onResult(emptyList()) // Return empty list in case of error
            }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show()
            onSendSOSButtonClick()
        } else {
            Toast.makeText(this, "Location permission is required for SOS.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
