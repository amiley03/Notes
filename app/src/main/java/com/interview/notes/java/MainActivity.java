package com.interview.notes.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.interview.notes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Main {@link android.app.Activity} which displays a list of existing Notes.
 */
public class MainActivity extends AppCompatActivity {

    private NotesStore notesStore;

    private RecyclerView notesList;
    private Button addNoteButton;
    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notesStore = ((MainApplication) getApplication()).getNotesStore();

        setContentView(R.layout.activity_main);

        notesList = findViewById(R.id.notes_list);
        addNoteButton = findViewById(R.id.btn_add_note);

        notesAdapter = new NotesAdapter();
        notesList.setLayoutManager(new LinearLayoutManager(this));
        notesList.setAdapter(notesAdapter);
        notesAdapter.setNotes(notesStore.getNotes());

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNoteActivityIntent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(addNoteActivityIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh notes in the RecyclerView
        notesAdapter.setNotes(notesStore.getNotes());
    }

    private static class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

        private final List<Note> notes;

        private NotesAdapter() {
            this.notes = new ArrayList<>();
        }

        void setNotes(List<Note> freshNotes){
            this.notes.clear();
            this.notes.addAll(freshNotes);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TextView noteTitleView = holder.itemView.findViewById(R.id.note_title);
            noteTitleView.setText(notes.get(position).title);
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(final View itemView) { super(itemView); }
        }

    }


}
