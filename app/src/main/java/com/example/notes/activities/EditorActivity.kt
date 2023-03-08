package com.example.notes.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.notes.R
import com.example.notes.activities.MainActivity.Companion.storage
import java.time.LocalDateTime
import java.util.*

@Suppress("DEPRECATION")
class EditorActivity : AppCompatActivity() {
    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val uuid = intent.getSerializableExtra("active-note-uuid") as UUID
        val activeNote = storage!!.notes.find { it.uuid == uuid }

        val titleEdit = findViewById<EditText>(R.id.titleEdit)
        val editorField = findViewById<EditText>(R.id.editorField)
        val saveButton = findViewById<Button>(R.id.saveButton)

        titleEdit.setText(activeNote!!.title)
        editorField.setText(activeNote.body)

        titleEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveButton.isVisible = true
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        editorField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveButton.isVisible = true
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        saveButton.setOnClickListener {
            with(activeNote) {
                title = titleEdit.text.toString()
                body = editorField.text.toString()
                lastUpdateDate = LocalDateTime.now()
            }
            storage!!.editNote(activeNote)
            saveButton.isVisible = false
            NotesAdapter.instance.notifyDataSetChanged()
        }

    }
}