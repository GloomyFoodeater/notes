package com.example.notes.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.NotesStorage
import com.example.notes.model.StorageType
import com.example.notes.storageio.FileSystemIO
import com.example.notes.storageio.SqliteIO
import com.example.notes.services.NotesSqliteOpenHelper
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        var storage: NotesStorage? = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("DEPRECATION")
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val notesFsIO = FileSystemIO("$filesDir/notes")
        val notesSqliteIO = SqliteIO(NotesSqliteOpenHelper(this))

        if (storage == null)
            storage = NotesStorage(notesFsIO, linkedSetOf(notesFsIO, notesSqliteIO))

        val storage = storage!!

        val filterContainer = findViewById<View>(R.id.filterContainer)
        val settingsContainer = findViewById<View>(R.id.settingsContainer)
        val notesListContainer = findViewById<RecyclerView>(R.id.notesListContainer)
        val searchQueryEdit = findViewById<EditText>(R.id.searchQueryEdit)
        val fsCheckbox = findViewById<CheckBox>(R.id.fsCheckbox)
        val sqliteCheckbox = findViewById<CheckBox>(R.id.sqliteCheckbox)
        val fsRadioButton = findViewById<RadioButton>(R.id.fsRadioButton)
        val sqliteRadioButton = findViewById<RadioButton>(R.id.sqliteRadioButton)
        val storageTypeRadioGroup = findViewById<RadioGroup>(R.id.storageTypeRadioGroup)
        val filterButton = findViewById<Button>(R.id.filterButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val addButton = findViewById<Button>(R.id.addButton)

        filterButton.setOnClickListener {
            settingsContainer.isVisible = false
            filterContainer.isVisible = !filterContainer.isVisible
        }
        settingsButton.setOnClickListener {
            filterContainer.isVisible = false
            settingsContainer.isVisible = !settingsContainer.isVisible
        }
        addButton.setOnClickListener {
            val storageType = when (storageTypeRadioGroup.checkedRadioButtonId) {
                R.id.fsRadioButton -> StorageType.FileSystem
                R.id.sqliteRadioButton -> StorageType.SQLite
                else -> throw Exception("Unknown storage type")
            }

            storage.addNote(storageType)
            notesListContainer.adapter!!.notifyDataSetChanged()
        }
        searchQueryEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val prevCount = storage.notes.size
                storage.titleQuery = searchQueryEdit.text.toString()
                if (prevCount != storage.notes.size)
                    notesListContainer.adapter!!.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        fsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) storage.addReader(notesFsIO)
            else storage.removeReader(notesFsIO)
            notesListContainer.adapter!!.notifyDataSetChanged()
        }
        sqliteCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) storage.addReader(notesSqliteIO)
            else storage.removeReader(notesSqliteIO)
            notesListContainer.adapter!!.notifyDataSetChanged()
        }
        fsRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) storage.writer = notesFsIO
        }
        sqliteRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) storage.writer = notesSqliteIO
        }

        notesListContainer.layoutManager = LinearLayoutManager(this)
        notesListContainer.adapter = NotesAdapter.instance
    }
}