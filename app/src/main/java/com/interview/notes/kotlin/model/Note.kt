package com.interview.notes.kotlin.model

import java.util.UUID.randomUUID

data class Note(
    val title: String,
    val content: String,
    val id: String = randomUUID().toString(),
    val timeStamp: String = System.currentTimeMillis().toString()
)
