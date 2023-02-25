package com.example.notes

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
import com.example.notes.model.NotesStorage
import com.example.notes.model.StorageType
import java.util.*


class MainActivity : AppCompatActivity() {

    var storage = NotesStorage()

    @Suppress("DEPRECATION")
    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null)
            storage = savedInstanceState.getSerializable("storage") as NotesStorage

        val filterContainer = findViewById<View>(R.id.filterContainer)
        val settingsContainer = findViewById<View>(R.id.settingsContainer)
        val notesListContainer = findViewById<RecyclerView>(R.id.notesListContainer)
        val searchQueryEdit = findViewById<EditText>(R.id.searchQueryEdit)
        val fsCheckbox = findViewById<CheckBox>(R.id.fsCheckbox)
        val sqliteCheckBox = findViewById<CheckBox>(R.id.sqliteCheckbox)
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

            val pos = storage.addNote(storageType)
            if (pos != -1)
                notesListContainer.adapter!!.notifyItemInserted(pos)
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
            if (isChecked) storage.addStorageTypeFilter(StorageType.FileSystem)
            else storage.removeStorageTypeFilter(StorageType.FileSystem)
            notesListContainer.adapter!!.notifyDataSetChanged()
        }
        sqliteCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) storage.addStorageTypeFilter(StorageType.SQLite)
            else storage.removeStorageTypeFilter(StorageType.SQLite)
            notesListContainer.adapter!!.notifyDataSetChanged()
        }
        fsRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) fsCheckbox.isChecked = true
        }
        sqliteRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sqliteCheckBox.isChecked = true
        }

        notesListContainer.layoutManager = LinearLayoutManager(this)
        notesListContainer.adapter = NoteAdapter(storage)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putSerializable("storage", storage)
    }
}