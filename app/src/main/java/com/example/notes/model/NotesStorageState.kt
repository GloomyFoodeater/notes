package com.example.notes.model

class NotesStorageState: java.io.Serializable {
    var storageFilters: LinkedHashSet<StorageType>? = null
    var titleQuery: String? = null
    var filtered: ArrayList<Note>? = null
    var notes: ArrayList<Note>? = null
}