package com.interview.notes.kotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID.randomUUID

@Entity(tableName = "notes")
data class Note(
    val title: String,
    val content: String,
    @PrimaryKey val id: String = randomUUID().toString(),
    val timeStamp: String = System.currentTimeMillis().toString()
)
