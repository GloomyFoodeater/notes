package com.example.notes.storageio

import com.example.notes.model.Note
import com.example.notes.model.StorageType

interface NotesStorageIO {
    val storageType: StorageType
    fun readAll(): ArrayList<Note>
    fun write(note: Note)
    fun remove(note: Note)
}