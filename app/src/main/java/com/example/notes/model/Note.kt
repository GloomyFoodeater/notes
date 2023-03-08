package com.example.notes.model

import java.time.LocalDateTime
import java.util.*

class Note(
    var title: String,
    val creationDate: LocalDateTime,
    var lastUpdateDate: LocalDateTime,
    val storageType: StorageType,
    var body: String,
    uuid: UUID? = null
) {
    var uuid: UUID = when (uuid) {
        null -> UUID.randomUUID()
        else -> uuid
    }
}