package com.example.notes.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.activities.MainActivity.Companion.storage
import java.time.format.DateTimeFormatter
import java.util.*


class NotesAdapter private constructor() : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    companion object {
        val instance = NotesAdapter()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val creationDate: TextView = itemView.findViewById(R.id.creationDate)
        val lastUpdateDate: TextView = itemView.findViewById(R.id.lastUpdateDate)
        val storageType: TextView = itemView.findViewById(R.id.storageType)
        var uuid: UUID? = null
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
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
                        storage!!.removeNoteBy(holder.uuid!!)
                        notifyDataSetChanged()
                    }
                }
                .setNegativeButton("No") { _, _ -> }
            val alert = builder.create()
            alert.show()
            true
        }

        return holder
    }

    override fun getItemCount(): Int = storage!!.notes.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = storage!!.notes[position]
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