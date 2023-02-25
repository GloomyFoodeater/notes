package com.example.notes.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class NotesStorage : java.io.Serializable {

    private constructor()

    companion object {
        val instance = NotesStorage()
    }

    private var storageFilters = linkedSetOf(StorageType.FileSystem, StorageType.SQLite)

    var titleQuery: String = ""
        set(value) {
            field = value
            reFilter()
        }

    private var _filtered = ArrayList<Note>()
    private val _notes = ArrayList<Note>()
    val notes: List<Note>
        get() = _filtered

    private fun isInFiltered(note: Note): Boolean {
        return storageFilters.contains(note.storageType) &&
                note.title.startsWith(titleQuery, true)
    }

    private fun reFilter() {
        _filtered = _notes.filter(::isInFiltered) as ArrayList<Note>
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote(storageType: StorageType): Int {
        var note: Note
        var pos = -1
        val name = "Untitled"
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val now = LocalDateTime.now()
        val body = ""

        // Try to make unique uuid
        val maxAttempts = 50
        var counter = 0
        do {
            note = Note(name, now, now, storageType, body)
        } while (_notes.find { it.uuid == note.uuid } != null && counter++ < maxAttempts)

        // Add into RAM
        if (counter >= maxAttempts) return pos
        val res = _notes.add(note)
        if (!res) return pos

        // TODO: Add into external memory
        when (storageType) {
            StorageType.FileSystem -> {

            }
            StorageType.SQLite -> {

            }
        }

        if (isInFiltered(note)) {
            reFilter()
            pos = _filtered.size - 1
        }

        return pos
    }

    fun removeNoteBy(uuid: UUID): Int {
        val notesPos = _notes.indexOfFirst { it.uuid.equals(uuid) }
        if (notesPos == -1) return notesPos
        val storageType = _notes[notesPos].storageType
        val filteredPos = _filtered.indexOfFirst { it: Note -> it.uuid.equals(uuid) }

        // Remove from RAM
        _notes.removeAt(notesPos)
        reFilter()

        // Remove from external memory
        when (storageType) {
            StorageType.FileSystem -> {

            }
            StorageType.SQLite -> {

            }
        }

        return filteredPos
    }

    fun editNote(note: Note) : Int {
        val index = _notes.indexOfFirst { it.uuid == note.uuid }
        with(_notes[index]) {
            title = note.title
            body = note.body
            lastUpdateDate = note.lastUpdateDate
        }

        // TODO: Add into external memory
        when (note.storageType) {
            StorageType.FileSystem -> {

            }
            StorageType.SQLite -> {

            }
        }

        if (isInFiltered(note)) {
            reFilter()
        }

        return index
    }

    fun addStorageTypeFilter(storageType: StorageType) {
        storageFilters.add(storageType)
        reFilter()
    }

    fun removeStorageTypeFilter(storageType: StorageType) {
        storageFilters.remove(storageType)
        reFilter()
    }
}