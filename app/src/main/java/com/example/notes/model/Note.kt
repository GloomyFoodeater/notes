package com.example.notes.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class Note(
    var title: String,
    val creationDate: LocalDateTime,
    var lastUpdateDate: LocalDateTime,
    val storageType: StorageType,
    var body: String
) : java.io.Serializable {
    val uuid = UUID.randomUUID()
}