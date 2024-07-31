package com.example.criminalmove.database

import androidx.room.TypeConverters
import java.util.Date
import java.util.UUID

class CrimeTypeConverters {

    @TypeConverters
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
    @TypeConverters
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverters
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
    @TypeConverters
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

}