package com.example.criminalmove

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Используем кнопку date_button
        val dateButton: Button = findViewById(R.id.date_button)
        val currentDate = getCurrentFormattedDate()
        dateButton.text = currentDate

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    private fun getCurrentFormattedDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}
