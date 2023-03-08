package com.example.notes.storageio

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.notes.model.Note
import com.example.notes.model.StorageType
import com.example.notes.services.NotesSqliteOpenHelper
import java.time.LocalDateTime
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class SqliteIO(private val dbHelper: NotesSqliteOpenHelper) : NotesStorageIO {

    override val filter = StorageType.SQLite

    private fun readNote(cursor: Cursor): Note {
        with(cursor) {
            val uuid = getString(getColumnIndexOrThrow("uuid"))
            val title = getString(getColumnIndexOrThrow("title"))
            val creationDate = getString(getColumnIndexOrThrow("creationDate"))
            val lastUpdateDate = getString(getColumnIndexOrThrow("lastUpdateDate"))
            val body = getString(getColumnIndexOrThrow("body"))
            return Note(
                title,
                LocalDateTime.parse(creationDate),
                LocalDateTime.parse(lastUpdateDate),
                StorageType.SQLite,
                body,
                UUID.fromString(uuid)
            )
        }

    }

    override fun readAll(): ArrayList<Note> {
        val notes = ArrayList<Note>()
        val cursor = dbHelper.readableDatabase.query(
            "notes", null, null, null, null, null, null
        )

        cursor.use { while (cursor.moveToNext()) notes.add(readNote(cursor)) }
        return notes
    }

    override fun write(note: Note) {
        val values = ContentValues().apply {
            with(note) {
                put("uuid", uuid.toString())
                put("title", title)
                put("creationDate", creationDate.toString())
                put("lastUpdateDate", lastUpdateDate.toString())
                put("body", body)
            }
        }
        val db = dbHelper.writableDatabase
        db.insertWithOnConflict("notes", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    override fun remove(note: Note) {
        dbHelper.writableDatabase.delete("notes", "uuid = \"${note.uuid}\"", null)
    }
}