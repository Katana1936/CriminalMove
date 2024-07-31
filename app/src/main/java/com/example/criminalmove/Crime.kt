package com.example.criminalmove.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class Crime(@PrimaryKey
                 val id: UUID = UUID.randomUUID(),
                 var title: String = "",
                 var isSolved: Boolean = false,
                 var date: Date = Date()
)