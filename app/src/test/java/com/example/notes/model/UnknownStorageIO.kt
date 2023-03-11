package com.example.notes.model

import com.example.notes.storageio.NotesStorageIO

class UnknownStorageIO : NotesStorageIO {
    override val storageType = StorageType.Unknown

    override fun readAll(): ArrayList<Note> {
        return ArrayList()
    }

    override fun write(note: Note) {
        // Ignore...
    }

    override fun remove(note: Note) {
        // Ignore
    }
}