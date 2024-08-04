package com.example.criminalmove.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.random.Random

class CrimeDataSource private constructor(context: Context) {

    private val configuration = RealmConfiguration.Builder(schema = setOf(Crime::class))
        .name("crimes.realm")
        .build()

    private val realm = Realm.open(configuration)

    fun getCrimes(): LiveData<List<Crime>> {
        val liveData = MutableLiveData<List<Crime>>()
        runBlocking {
            val crimes = withContext(Dispatchers.IO) {
                realm.query<Crime>().find()
            }
            Log.i(TAG, "Loaded crimes: ${crimes.size}")
            liveData.postValue(crimes)
        }
        return liveData
    }

    fun getCrime(id: String): LiveData<Crime?> {
        val liveData = MutableLiveData<Crime?>()
        runBlocking {
            val crime = withContext(Dispatchers.IO) {
                realm.query<Crime>("id == $0", id).find().firstOrNull()
            }
            liveData.postValue(crime)
        }
        return liveData
    }

    fun addCrime(crime: Crime) {
        runBlocking {
            realm.write {
                copyToRealm(crime)
            }
        }
    }

    private fun populateDatabaseWithRandomCrimes() {
        runBlocking {
            realm.write {
                for (i in 0 until 10000) {
                    val crime = Crime(
                        title = "Crime #${i + 1}",
                        date = Date(System.currentTimeMillis() - Random.nextLong(1000000000L)).time,
                        isSolved = Random.nextBoolean()
                    )
                    copyToRealm(crime)
                }
            }
        }
        Log.i(TAG, "Populated database with 10,000 random crimes")
    }

    private fun populateDatabaseIfEmpty() {
        runBlocking {
            val crimeCount = withContext(Dispatchers.IO) {
                realm.query<Crime>().count().find()
            }
            Log.i(TAG, "Crime count in database: $crimeCount")
            if (crimeCount == 0L) {
                Log.i(TAG, "Database is empty, populating with random crimes")
                populateDatabaseWithRandomCrimes()
            }
        }
    }

    companion object {
        private var INSTANCE: CrimeDataSource? = null
        private const val TAG = "CrimeDataSource"

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeDataSource(context)
                INSTANCE?.populateDatabaseIfEmpty()
            }
        }

        fun get(): CrimeDataSource {
            return INSTANCE ?: throw IllegalStateException("CrimeDataSource must be initialized")
        }
    }
}
