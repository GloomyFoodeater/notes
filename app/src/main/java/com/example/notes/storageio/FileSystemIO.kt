package com.example.notes.storageio

import android.os.Build
import android.util.JsonReader
import android.util.JsonWriter
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.notes.model.Note
import com.example.notes.model.StorageType
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class FileSystemIO(private val path: String) : NotesStorageIO {
    override val storageType = StorageType.FileSystem

    private fun readNote(reader: JsonReader, fileName: String): Note? {
        var note: Note? = null
        reader.use {
            reader.beginObject()
            var title: String? = null
            var creationDate: LocalDateTime? = null
            var lastUpdateDate: LocalDateTime? = null
            var body: String? = null
            while (reader.hasNext()) {
                val name = reader.nextName()
                val value = reader.nextString()
                when (name) {
                    "title" -> title = value
                    "creationDate" -> creationDate = LocalDateTime.parse(value)
                    "lastUpdateDate" -> lastUpdateDate = LocalDateTime.parse(value)
                    "body" -> body = value
                    else -> reader.skipValue()
                }
            }
            if (title != null && creationDate != null && lastUpdateDate != null && body != null) {
                note = Note(
                    title,
                    creationDate,
                    lastUpdateDate,
                    StorageType.FileSystem,
                    body,
                    UUID.fromString(fileName.dropLast(5))
                )
            }
            reader.endObject()
        }
        return note
    }

    override fun readAll(): ArrayList<Note> {
        val notes = ArrayList<Note>()
        File(path).list()?.forEach {
            try {
                val reader = JsonReader(FileReader("${path}/${it}"))
                val note = readNote(reader, it)
                if (note != null) notes.add(note)
            } catch (e: Exception) {
                Log.e("Reading error: ", e.toString())
            }
        }
        return notes
    }

    private fun ensurePathExistence() {
        val dir = File(path)
        if (!dir.exists()) dir.mkdirs()
        else if (!dir.isDirectory || !dir.canWrite()) {
            dir.delete()
            dir.mkdirs()
        }
    }

    override fun write(note: Note) {
        ensurePathExistence()
        val writer = JsonWriter(FileWriter("${path}/${note.uuid}.json"))
        writer.use {
            with(writer) {
                beginObject()
                name("title").value(note.title)
                name("creationDate").value(note.creationDate.toString())
                name("lastUpdateDate").value(note.lastUpdateDate.toString())
                name("body").value(note.body)
                endObject()
            }
        }
    }

    override fun remove(note: Note) = Files.delete(Paths.get("${path}/${note.uuid}.json"))
}