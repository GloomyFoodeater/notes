package com.example.notes.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.notes.storageio.NotesStorageIO
import java.time.LocalDateTime
import java.util.*

class NotesStorage(
    var writer: NotesStorageIO,
    private val readers: LinkedHashSet<NotesStorageIO>
) {

    var titleQuery: String = ""
        set(value) {
            field = value
            reFilter()
        }

    private val _allNotes = ArrayList<Note>()
    private var _filteredNotes = _allNotes
    val notes: List<Note>
        get() = _filteredNotes
    private var allowedIos = LinkedHashSet<NotesStorageIO>()

    init {
        allowedIos.addAll(readers)
        readers.forEach {
            _allNotes.addAll(it.readAll())
        }
        reFilter()
    }

    private fun mustBeInFiltered(note: Note): Boolean {
        return readers.any { it.filter == note.storageType } &&
                note.title.startsWith(titleQuery, true)
    }

    private fun reFilter() {
        _filteredNotes = _allNotes.filter(::mustBeInFiltered) as ArrayList<Note>
        _filteredNotes.sortByDescending { it.lastUpdateDate }
    }

    private fun getIoOf(note: Note): NotesStorageIO =
        allowedIos.find { it.filter == note.storageType }!!

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote(storageType: StorageType) {
        var note: Note
        val name = "Untitled"
        val now = LocalDateTime.now()
        val body = ""

        // Try to make unique uuid
        val maxAttempts = 50
        var counter = 0
        do {
            note = Note(name, now, now, storageType, body)
        } while (_allNotes.find { it.uuid == note.uuid } != null && counter++ < maxAttempts)

        // Add into RAM
        if (counter >= maxAttempts) return
        val res = _allNotes.add(note)
        if (!res) return

        // Add into external memory
        writer.write(note)

        if (mustBeInFiltered(note)) reFilter()

    }

    fun removeNoteBy(uuid: UUID) {
        val notesPos = _allNotes.indexOfFirst { it.uuid == uuid }
        if (notesPos == -1) return

        // Remove from RAM
        val note = _allNotes.removeAt(notesPos)
        reFilter()

        // Remove from external memory
        getIoOf(note).remove(note)
    }

    fun editNote(note: Note) {
        val index = _allNotes.indexOfFirst { it.uuid == note.uuid }
        with(_allNotes[index]) {
            title = note.title
            body = note.body
            lastUpdateDate = note.lastUpdateDate
        }

        // Add into external memory
        getIoOf(note).write(note)

        if (mustBeInFiltered(note)) reFilter()
    }

    fun addReader(io: NotesStorageIO) {
        readers.add(io)
        reFilter()
    }

    fun removeReader(io: NotesStorageIO) {
        readers.remove(io)
        reFilter()
    }
}