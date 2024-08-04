package com.example.criminalmove

import java.util.Date

data class Crime(
    var id: String = "",
    var title: String = "",
    var isSolved: Boolean = false,
    var date: Date = Date()
)
