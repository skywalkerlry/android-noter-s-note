package com.davidgassner.plainolnotes;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;

import android.util.Log;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.davidgassner.plainolnotes.data.NoteItem;
import com.davidgassner.plainolnotes.dateUtils.DateUtils;

public class NoteEditorActivity extends SherlockActivity {

	private NoteItem note;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_editor);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = this.getIntent();
		note = new NoteItem();
		note.setKey(intent.getStringExtra("key"));
		note.setTitle(intent.getStringExtra("title"));
		note.setText(intent.getStringExtra("text"));
		note.setAbsPath(intent.getStringExtra("absPath"));

		EditText et = (EditText) findViewById(R.id.noteTitle);
		et.setText(note.getTitle());
		et.setSelection(note.getTitle().length());

		et = (EditText) findViewById(R.id.noteText);
		et.setText(note.getText());
		et.setSelection(note.getText().length());
	}

	private void saveAndFinish() {
		EditText et = (EditText) findViewById(R.id.noteText);
		String noteText = et.getText().toString().trim();

		et = (EditText) findViewById(R.id.noteTitle);
		String noteTitle = et.getText().toString().trim();

		Intent intent = new Intent();
		intent.putExtra("key", note.getKey());
		intent.putExtra("title", noteTitle);
		intent.putExtra("text", noteText);
		intent.putExtra("absPath", note.getAbsPath());
		
		String editTime = DateUtils.getDate();
		intent.putExtra("lastEdit", editTime);
		
		setResult(RESULT_OK, intent);
		finish();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			saveAndFinish();
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		saveAndFinish();
	}

}
