package com.interview.notes.kotlin

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.interview.notes.R

/**
 * {@link android.app.Activity} to create a new Note.
 * The layout has two EditTexts, one for title and another for content
 */
class AddNoteActivity : AppCompatActivity() {

    /*
    *************************************************************************************************************************
        NOTE: BEFORE MAKING CHANGES HERE, MAKE SURE THAT YOU CHANGE THE PACKAGE OF AddNoteActivity IN AndroidManifest.xml
    *************************************************************************************************************************
    */

    private var notesStore: NotesStore? = null

    private lateinit var titleEdit: EditText
    private lateinit var contentEdit: EditText
    private lateinit var saveNoteButton: Button

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notesStore = (application as MainApplication).getNotesStore()

        setContentView(R.layout.activity_add_note)

        titleEdit = findViewById(R.id.title_edit)
        contentEdit = findViewById(R.id.content_edit)
        saveNoteButton = findViewById(R.id.btn_save_note)

        saveNoteButton.setOnClickListener(View.OnClickListener {
            val title = titleEdit.getText().toString()
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this@AddNoteActivity, "Please enter title", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val content = contentEdit.getText().toString()
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this@AddNoteActivity, "Please enter content", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            notesStore?.saveNote(Note(title, content))
            finish()
        })
    }



}
