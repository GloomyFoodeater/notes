package com.example.notes.services

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesSqliteOpenHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Notes.db"
    }

    private val _createEntries =
        "CREATE TABLE notes (" +
                "id INTEGER PRIMARY KEY," +
                "uuid TEXT NOT NULL UNIQUE," +
                "title TEXT NOT NULL," +
                "creationDate TEXT NOT NULL," +
                "lastUpdateDate TEXT NOT NULL," +
                "body TEXT NOT NULL)"

    private val _deleteEntries = "DROP TABLE IF EXISTS notes"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(_createEntries)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(_deleteEntries)
        onCreate(db)
    }
}