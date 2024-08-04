package com.example.criminalmove

import java.util.*

data class Crime(
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var isSolved: Boolean = false,
    var date: Date = Date()
)
