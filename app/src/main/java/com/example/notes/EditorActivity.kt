package com.example.notes

import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.notes.model.Note
import com.example.notes.model.NotesStorage
import java.time.LocalDate

@Suppress("DEPRECATION")
class EditorActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        val activeNote = intent.getSerializableExtra("active-note") as Note
        // TODO: Pass references between activities
        val storage = intent.getSerializableExtra("storage") as NotesStorage

        val titleEdit = findViewById<EditText>(R.id.titleEdit)
        val editorField = findViewById<EditText>(R.id.editorField)
        val saveButton = findViewById<Button>(R.id.saveButton)

        titleEdit.setText(activeNote.title)
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
                lastUpdateDate = LocalDate.now()
            }
            storage.editNote(activeNote)
            saveButton.isVisible = false
        }

    }
}