package com.example.notes

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.model.NotesStorage
import java.time.format.DateTimeFormatter
import java.util.UUID

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private constructor() : super()

    companion object {
        val instance = NoteAdapter()
    }

    private val storage = NotesStorage.instance

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val creationDate: TextView = itemView.findViewById(R.id.creationDate)
        val lastUpdateDate: TextView = itemView.findViewById(R.id.lastUpdateDate)
        val storageType: TextView = itemView.findViewById(R.id.storageType)
        var uuid: UUID? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.notes_list_item, parent, false)
        val holder = ViewHolder(view)

        view.setOnClickListener {
            val intent = Intent(parent.context, EditorActivity::class.java)
            val mainActivity = parent.context as MainActivity
            intent.putExtra("active-note-uuid", holder.uuid)
            mainActivity.startActivity(intent)
        }

        view.setOnLongClickListener {
            val builder = AlertDialog.Builder(parent.context)
            builder.setMessage("Remove note?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    if (holder.uuid != null) {
                        val pos = storage.removeNoteBy(holder.uuid!!)
                        if (pos != -1) this.notifyItemRemoved(pos)
                    }
                }
                .setNegativeButton("No") { _, _ -> }
            val alert = builder.create()
            alert.show()
            true
        }

        return holder
    }

    override fun getItemCount(): Int = storage.notes.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = storage.notes[position]
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        with(holder) {
            title.text = note.title
            creationDate.text = note.creationDate.format(formatter)
            lastUpdateDate.text = note.lastUpdateDate.format(formatter)
            storageType.text = note.storageType.name
            uuid = note.uuid
        }
    }
}