package com.example.criminalmove

import android.content.Context
import java.io.File

object CrimeRepository {
    fun getPhotoFile(context: Context, crime: Crime): File {
        val filesDir = context.filesDir
        return File(filesDir, crime.photoFileName)
    }
}