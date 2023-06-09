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
import com.example.notes.services.NotesSqliteOpenHelper
import com.example.notes.storageio.FileSystemIO
import com.example.notes.storageio.SqliteIO
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        var storage: NotesStorage? = null
        var fileSystemIO: FileSystemIO? = null
        var sqliteIO: SqliteIO? = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("DEPRECATION")
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (fileSystemIO == null)
            fileSystemIO = FileSystemIO("$filesDir/notes")
        if (sqliteIO == null)
            sqliteIO = SqliteIO(NotesSqliteOpenHelper(this))
        if (storage == null)
            storage = NotesStorage(fileSystemIO!!, linkedSetOf(fileSystemIO!!, sqliteIO!!))

        val storage = storage!!

        val settingsContainer = findViewById<View>(R.id.settingsContainer)
        val notesListContainer = findViewById<RecyclerView>(R.id.notesListContainer)
        val searchQueryEdit = findViewById<EditText>(R.id.searchQueryEdit)
        val fsCheckbox = findViewById<CheckBox>(R.id.fsCheckbox)
        val sqliteCheckbox = findViewById<CheckBox>(R.id.sqliteCheckbox)
        val fsRadioButton = findViewById<RadioButton>(R.id.fsRadioButton)
        val sqliteRadioButton = findViewById<RadioButton>(R.id.sqliteRadioButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val addButton = findViewById<Button>(R.id.addButton)

        settingsButton.setOnClickListener {
            settingsContainer.isVisible = !settingsContainer.isVisible
        }
        addButton.setOnClickListener {
            storage.addNote()
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
            if (isChecked) storage.addReader(fileSystemIO!!)
            else storage.removeReader(fileSystemIO!!)
            notesListContainer.adapter!!.notifyDataSetChanged()
        }
        sqliteCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) storage.addReader(sqliteIO!!)
            else storage.removeReader(sqliteIO!!)
            notesListContainer.adapter!!.notifyDataSetChanged()
        }
        fsRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) storage.writer = fileSystemIO!!
        }
        sqliteRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) storage.writer = sqliteIO!!
        }

        notesListContainer.layoutManager = LinearLayoutManager(this)
        notesListContainer.adapter = NotesAdapter.instance
    }
}