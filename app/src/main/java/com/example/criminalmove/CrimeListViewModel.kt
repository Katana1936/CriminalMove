package com.example.criminalmove

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class CrimeListViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val crimesLiveData = MutableLiveData<List<Crime>>()
    private lateinit var filesDir: File

    // Инициализация директории для хранения файлов
    fun initialize(context: Context) {
        filesDir = context.applicationContext.filesDir
    }

    // Метод для получения файла фотографии по имени
    fun getPhotoFile(crime: Crime): File {
        return File(filesDir, crime.photoFileName)
    }

    fun getCrimesLiveData(): LiveData<List<Crime>> {
        return crimesLiveData
    }

    private fun fetchCrimes() {
        firestore.collection("Crimes")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Обработка ошибок
                    return@addSnapshotListener
                }

                val crimeList = snapshot?.documents?.map { document ->
                    document.toObject(Crime::class.java)?.apply { id = document.id }
                }?.filterNotNull() ?: emptyList()

                crimesLiveData.value = crimeList
            }
    }
}
