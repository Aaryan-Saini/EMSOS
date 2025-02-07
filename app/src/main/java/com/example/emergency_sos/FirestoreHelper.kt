package com.example.emergency_sos.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

object FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()

    fun logSOSAlert(userId: String, location: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val sosLog = hashMapOf(
            "userId" to userId,
            "location" to location,
            "timestamp" to System.currentTimeMillis(),
            "status" to "active"
        )

        db.collection("SOS_logs")
            .add(sosLog)
            .addOnSuccessListener {
                Log.d("SOS", "SOS logged successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("SOS", "Error logging SOS", e)
                onFailure(e)
            }
    }

    fun updateRescuerCredits(rescuerId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userRef = db.collection("credits").document(rescuerId)
        userRef.update("totalCredits", FieldValue.increment(10))
            .addOnSuccessListener {
                Log.d("Credits", "Credits updated successfully for rescuer: $rescuerId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Credits", "Error updating credits", e)
                onFailure(e)
            }
    }
}
