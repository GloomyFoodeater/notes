package com.example.notes.model

import org.junit.Test
import java.time.LocalDateTime

internal class NotesStorageTest {
    private val io = UnknownStorageIO()
    private val storage = NotesStorage(io, linkedSetOf(io))

    @Test
    fun setTitleQuery() {
        // Arrange
        for (i in 1..3) storage.addNote()
        storage.notes[0].title = "Titled"
        val allNotes = storage.notes

        // Act
        storage.titleQuery = "Titled"
        val titledSubset = storage.notes
        storage.titleQuery = "Untitled"
        val untitledSubset = storage.notes

        // Assert
        assert(titledSubset.size == 1 && titledSubset[0] == allNotes[0])
        assert(
            untitledSubset.size == 2 &&
                    untitledSubset[0] == allNotes[1] &&
                    untitledSubset[1] == allNotes[2]
        )
    }

    @Test
    fun addNote() {
        // Arrange

        // Act
        for (i in 1..3) storage.addNote()

        // Assert
        assert(storage.notes.size == 3 && storage.notes.all {
            it.storageType == StorageType.Unknown && it.title == "Untitled" && it.body == ""
        })
    }

    @Test
    fun removeNoteBy() {
        // Arrange
        for (i in 1..4) storage.addNote()
        val uuids = storage.notes.map { it.uuid }

        // Act
        storage.removeNoteBy(uuids[2]) // Middle of the list
        storage.removeNoteBy(uuids[3]) // End of the list
        storage.removeNoteBy(uuids[0]) // Start of the list
        storage.removeNoteBy(uuids[1]) // Start and last

        // Assert
        assert(storage.notes.isEmpty())
    }

    @Test
    fun editNote() {
        // Arrange
        storage.addNote()
        val uuid = storage.notes[0].uuid
        val title = "New title"
        val newTime = LocalDateTime.now().plusMinutes(2)
        val storageType = StorageType.SQLite
        val body = "Hello world!"
        val info = Note(title, newTime, newTime, storageType, body, uuid)

        // Act
        storage.editNote(info)

        // Assert
        val updatedNode = storage.notes[0]
        assert(
            updatedNode.uuid == uuid &&
                    updatedNode.title == title &&
                    updatedNode.creationDate != newTime &&
                    updatedNode.lastUpdateDate == newTime &&
                    updatedNode.storageType != storageType &&
                    updatedNode.body == body
        )
    }

    @Test
    fun changeReaders() {
        // Arrange
        storage.addNote()
        val note = storage.notes[0]

        // Act
        storage.removeReader(io)
        val changedSubset = storage.notes
        storage.addReader(io)
        val restoredSubset = storage.notes

        // Asset
        assert(changedSubset.isEmpty() && restoredSubset.size == 1 && restoredSubset[0] == note)
    }
}