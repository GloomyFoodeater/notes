package com.example.notes.model

import java.time.LocalDate
import java.util.UUID

class Note(
    var title: String,
    val creationDate: LocalDate,
    var lastUpdateDate: LocalDate,
    val storageType: StorageType,
    var body: String
) : java.io.Serializable {
    val uuid = UUID.randomUUID()
}