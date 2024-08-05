package com.example.criminalmove

import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

data class Crime(
    var id: String = "",
    var title: String = "",
    var isSolved: Boolean = false,
    var date: Date = Date()
)

fun addCrimeToFirestore(crime: Crime) {
    val db = FirebaseFirestore.getInstance()

    // Генерируем уникальный идентификатор для каждого title
    val uniqueTitle = "${crime.title}_${System.currentTimeMillis()}"

    // Создаем карту данных для хранения в Firestore
    val crimeData = hashMapOf(
        "id" to crime.id,
        "title" to uniqueTitle,
        "isSolved" to crime.isSolved,
        "date" to crime.date
    )

    // Добавляем документ в коллекцию "Crimes"
    db.collection("Crimes")
        .add(crimeData)
        .addOnSuccessListener { documentReference ->
            println("DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Error adding document: $e")
        }
}
