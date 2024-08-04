package com.example.criminalmove

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CrimeListViewModel : ViewModel() {
    private val _crimesLiveData = MutableLiveData<List<Crime>>()
    val crimesLiveData: LiveData<List<Crime>> get() = _crimesLiveData

    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    init {
        fetchCrimesFromFirestore()
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    private fun fetchCrimesFromFirestore() {
        listenerRegistration = db.collection("Crimes")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("CrimeListViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val crimes = mutableListOf<Crime>()
                    for (document in snapshot.documents) {
                        val crime = document.toObject(Crime::class.java)
                        crime?.id = document.id
                        if (crime != null) {
                            crimes.add(crime)
                        }
                    }
                    _crimesLiveData.value = crimes
                }
            }
    }
}
