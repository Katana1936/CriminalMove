package com.example.criminalmove

import androidx.lifecycle.ViewModel
import com.example.criminalmove.database.Crime
import com.example.criminalmove.database.CrimeRepository

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    val crimes = crimeRepository.getCrimes()
}