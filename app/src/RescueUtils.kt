import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

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
