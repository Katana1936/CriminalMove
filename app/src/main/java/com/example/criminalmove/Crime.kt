package com.example.criminalmove

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

data class Crime(
    @DocumentId var id: String = "",
    var title: String = "",
    var isSolved: Boolean = false,
    var date: Date = Date(),
    var suspect: String? = null,
    var contactId: String = ""
) {
    val photoFileName: String
        get() = "IMG_$id.jpg"
}

fun addCrimeToFirestore(crime: Crime) {
    val db = FirebaseFirestore.getInstance()

    val uniqueTitle = "${crime.title}_${System.currentTimeMillis()}"

    val crimeData = hashMapOf(
        "id" to crime.id,
        "title" to uniqueTitle,
        "isSolved" to crime.isSolved,
        "date" to crime.date
    )

    db.collection("Crimes")
        .add(crimeData)
        .addOnSuccessListener { documentReference ->
            println("DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Error adding document: $e")
        }
}
