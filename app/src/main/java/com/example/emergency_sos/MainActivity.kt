package com.example.emergency_sos
import com.google.firebase.firestore.FieldValue
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.emergency_sos.ui.theme.EmergencySOSTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            EmergencySOSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        onSOSClick = { sendSOSAlert() }
                    )
                }
            }
        }
    } //check

        private fun sendSOSAlert() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Fetch the current location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val locationString = "${location.latitude}, ${location.longitude}"
                        // Log the SOS alert with location
                        logSOSAlert(locationString)
                    } else {
                        Toast.makeText(this, "Unable to fetch location!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Location", "Failed to get location", e)
                    Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun logSOSAlert(location: String) {
        val db = FirebaseFirestore.getInstance()

        val sosLog = hashMapOf(
            "userId" to (FirebaseAuth.getInstance().currentUser?.uid ?: "Anonymous"),
            "location" to location,
            "timestamp" to System.currentTimeMillis(),
            "status" to "active"
        )

        db.collection("SOS_logs")
            .add(sosLog)
            .addOnSuccessListener {
                Log.d("SOS", "SOS logged successfully")
                Toast.makeText(this, "SOS Alert Sent Successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("SOS", "Error logging SOS", e)
                Toast.makeText(this, "Failed to send SOS alert", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onRescueComplete(rescuerId: String) {
        // Update the rescuer's credits after completing the rescue
        RescueUtils.updateCredits(rescuerId)
    }
}

object RescueUtils {

    // Update credits for the rescuer
    fun updateCredits(rescuerId: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(rescuerId)

        userRef.update("credits", FieldValue.increment(10)) // Add 10 credits
            .addOnSuccessListener {
                Log.d("Credits", "Credits updated successfully for rescuer: $rescuerId")
            }
            .addOnFailureListener { e ->
                Log.e("Credits", "Error updating credits", e)
            }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, onSOSClick: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hello $name!",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = onSOSClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send SOS")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EmergencySOSTheme {
        Greeting(name = "Android", onSOSClick = {})
    }
}