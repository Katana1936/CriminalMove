package com.example.criminalmove

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.criminalmove.database.Crime
import com.example.criminalmove.database.CrimeDataSource

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeDataSource.get()
    val crimeListLiveData: LiveData<List<Crime>> = crimeRepository.getCrimes()
}
