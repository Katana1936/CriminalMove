package com.example.criminalmove

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CrimeListViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val crimesLiveData = MutableLiveData<List<Crime>>()

    init {
        fetchCrimes()
    }

    fun getCrimesLiveData(): LiveData<List<Crime>> {
        return crimesLiveData
    }

    private fun fetchCrimes() {
        firestore.collection("Crimes")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val crimeList = snapshot?.documents?.map { document ->
                    document.toObject(Crime::class.java)?.apply { id = document.id }
                }?.filterNotNull() ?: emptyList()

                crimesLiveData.value = crimeList
            }
    }
}
