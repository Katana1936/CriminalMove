package com.example.criminalmove.database

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class Crime : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var title: String = ""
    var date: Long = 0L
    var isSolved: Boolean = false

    constructor() // Пустой конструктор для Realm

    constructor(title: String, date: Long, isSolved: Boolean) {
        this.title = title
        this.date = date
        this.isSolved = isSolved
    }
}
