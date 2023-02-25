package com.example.notes.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.UUID

class NotesStorage {

    private var storageTypeFilters = mutableSetOf(StorageType.FileSystem, StorageType.SQLite)

    var titleQuery: String = ""
        set(value) {
            field = value
            reFilter()
        }

    private var _filtered: List<Note> = ArrayList()
    private val _notes = ArrayList<Note>()
    val notes: List<Note>
        get() = _filtered

    private fun reFilter() {
        _filtered = _notes.filter {
            storageTypeFilters.contains(it.storageType) &&
                    it.title.startsWith(titleQuery, true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote(storageType: StorageType): Int {
        var note: Note
        var pos = -1
        val name = "Untitled"
        val now = LocalDate.now()
        val body = ""

        // Try to make unique uuid
        val maxAttempts = 50;
        var counter = 0
        do {
            note = Note(name, now, now, storageType, body)
        } while (_notes.find { it.uuid == note.uuid } != null && counter++ < maxAttempts)

        // Add into RAM
        if (counter >= maxAttempts) return pos
        note.title = note.uuid.toString()
        val res = _notes.add(note)
        if (!res) return pos

        // TODO: Add into external memory
        when (storageType) {
            StorageType.FileSystem -> {

            }
            StorageType.SQLite -> {

            }
        }

        // Re filter and find position of inserted
        val isInFiltered =
            storageTypeFilters.contains(note.storageType) && note.title.startsWith(titleQuery, true)
        if (isInFiltered) {
            reFilter()
            pos = _filtered.size - 1
        }

        return pos
    }

    fun addStorageTypeFilter(storageType: StorageType) {
        storageTypeFilters.add(storageType)
        reFilter()
    }

    fun removeStorageTypeFilter(storageType: StorageType) {
        storageTypeFilters.remove(storageType)
        reFilter()
    }

    fun removeNoteBy(uuid: UUID): Int {
        val notesPos = _notes.indexOfFirst { it.uuid.equals(uuid) }
        if(notesPos == -1) return notesPos
        val storageType = _notes[notesPos].storageType
        val filteredPos = _filtered.indexOfFirst { it.uuid.equals(uuid) }

        // Remove from RAM
        if (notesPos != -1) {
            _notes.removeAt(notesPos)
            reFilter()
        }

        // Remove from external memory
        when (storageType) {
            StorageType.FileSystem -> {

            }
            StorageType.SQLite -> {

            }
        }

        return filteredPos
    }
}