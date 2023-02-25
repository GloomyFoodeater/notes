package com.example.notes

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.model.NotesStorage
import java.util.UUID

class NoteAdapter(private val storage: NotesStorage) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
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

        view.setOnClickListener{
            val intent = Intent(parent.context, EditorActivity::class.java)
            (parent.context as MainActivity).startActivity(intent)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = storage.notes[position]
        with(holder) {
            title.text = note.title
            creationDate.text = note.creationDate.toString()
            lastUpdateDate.text = note.lastUpdateDate.toString()
            storageType.text = note.storageType.name
            uuid = note.uuid
        }
    }
}