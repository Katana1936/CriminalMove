package com.example.criminalmove

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.criminalmove.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Проверяем, загружен ли уже фрагмент
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        // Если нет, загружаем CrimeListFragment
        if (currentFragment == null) {
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    // Метод интерфейса Callbacks, вызываемый при выборе преступления
    override fun onCrimeSelected(crimeId: String) {
        val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
