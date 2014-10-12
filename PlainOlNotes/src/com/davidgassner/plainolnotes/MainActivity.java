package com.davidgassner.plainolnotes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.ViewConfiguration;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.davidgassner.plainolnotes.adapter.FileAdapter;
import com.davidgassner.plainolnotes.data.FolderItem;
import com.davidgassner.plainolnotes.data.NoteItem;
import com.davidgassner.plainolnotes.data.NotesDataSource;

public class MainActivity extends SherlockListActivity implements
		SearchView.OnQueryTextListener {

	private static final int EDITOR_ACTIVITY_REQUEST = 1001;
	private static final int MENU_DELETE_ID = 1002;
	private static final int MENU_RENAME_ID = 1003;
	private int currentFileId;
	private NotesDataSource datasource;
	private List<FolderItem> folderList;
	private List<NoteItem> notesList;
	private List<NoteItem> searchResultList;
	private List<NoteItem> recentResultList;
	private String currentPath;
	private ActionBar actionBar;
	private String actionBarTitle;
	private SearchView searchView;
	private MenuItem searchItem;
	private boolean searchMode;
	private boolean recentMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		registerForContextMenu(getListView());
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}

		currentPath = "root";
		searchMode = false;
		datasource = new NotesDataSource(this);

		if (!datasource.containFolder(currentPath)) {
			FolderItem folder = FolderItem.getNew("root");
			folder.setKey("");
			datasource.updateFolder(folder, currentPath);
		}

		folderList = datasource.findAllFolders(currentPath);

		refreshDisplay();

	}

	private void refreshDisplay() {
		if (searchItem != null) {
			if (searchItem.expandActionView()) {
				searchItem.collapseActionView();
			}
		}
		actionBarTitle = datasource.getActionBarTitle(currentPath);
		if (!actionBarTitle.equals("root") && !currentPath.equals("root")) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(actionBarTitle);
		} else {
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setHomeButtonEnabled(false);
			actionBar.setTitle(R.string.app_name);
		}

		folderList = datasource.findAllFolders(currentPath);
		notesList = datasource.findAllNotes(currentPath);
		Collections.sort(folderList);

		List<Object> fileList = new ArrayList<Object>();
		for (FolderItem folder : folderList) {
			fileList.add(folder);
		}
		for (NoteItem note : notesList) {
			fileList.add(note);
		}
		FileAdapter<Object> adapter = new FileAdapter<Object>(this, fileList,
				folderList.size());

		setListAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSherlock().getMenuInflater().inflate(R.menu.main, menu);
		searchItem = menu.findItem(R.id.itemSearch);
		searchView = (SearchView) menu.findItem(R.id.itemSearch)
				.getActionView();
		searchView.setQueryHint("Search for notes");
		searchView.setOnQueryTextListener(this);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_create_file) {
			createNote();
		}

		else if (item.getItemId() == R.id.action_create_folder) {
			createFolder(null);
		}

		else if (item.getItemId() == R.id.action_recent_edit) {
			recentMode = true;
			searchMode = false;
			recentResultList = datasource.getAllNotes();
			Collections.sort(recentResultList, Collections.reverseOrder());
			showRecentResult();
		}

		else if (item.getItemId() == android.R.id.home) {
			if (!currentPath.equals("root") && searchMode == false
					&& recentMode == false) {
				currentPath = currentPath.substring(0,
						currentPath.length() - 25);
				refreshDisplay();
			} else if (searchMode == true) {
				searchMode = false;
				refreshDisplay();
			} else if (recentMode == true) {
				recentMode = false;
				refreshDisplay();
			} 

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (!currentPath.equals("root") && searchMode == false) {
			currentPath = currentPath.substring(0, currentPath.length() - 25);
			refreshDisplay();
		} else if (searchMode == true) {
			searchMode = false;
			refreshDisplay();
		} else if (recentMode == true) {
			recentMode = false;
			refreshDisplay();
		} else if (currentPath.equals("root")) {
			System.exit(0);
		}
	}

	private void createFolder(final FolderItem folder) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_new_folder, null);
		builder.setView(view)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText e1 = (EditText) view
								.findViewById(R.id.folder_name);
						String folderName = e1.getText().toString().trim();
						if (!folderName.trim().equals("")) {
							for (FolderItem folder : folderList) {
								if (folder.getName().equals(folderName)) {
									return;
								}
							}
							if (folder == null) {
								FolderItem new_folder = FolderItem
										.getNew(folderName);
								datasource
										.updateFolder(new_folder, currentPath);
							} else {
								folder.setName(folderName);
								datasource.updateFolder(folder, currentPath);
							}
							refreshDisplay();
						}
					}
				}).setNegativeButton("Cancel", null).show();

	}

	private void createNote() {
		NoteItem note = NoteItem.getNew();
		Intent intent = new Intent(this, NoteEditorActivity.class);
		intent.putExtra(NoteItem.KEY, note.getKey());
		intent.putExtra(NoteItem.TITLE, note.getTitle());
		intent.putExtra(NoteItem.TEXT, note.getText());
		intent.putExtra(NoteItem.ABSPATH, note.getAbsPath());
		startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		NoteItem note;
		if (searchMode == true || recentMode == true) {
			if (searchMode == true) {
				note = searchResultList.get(position);
			} else {
				note = recentResultList.get(position);
			}
			Intent intent = new Intent(this, NoteEditorActivity.class);
			intent.putExtra(NoteItem.KEY, note.getKey());
			intent.putExtra(NoteItem.TITLE, note.getTitle());
			intent.putExtra(NoteItem.TEXT, note.getText());
			intent.putExtra(NoteItem.ABSPATH, note.getAbsPath());
			startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
		}

		else {
			if (position < folderList.size()) {
				FolderItem folder = folderList.get(position);
				currentPath = currentPath.concat(folder.getKey());
				refreshDisplay();
			} else {
				note = notesList.get(position - folderList.size());
				Intent intent = new Intent(this, NoteEditorActivity.class);
				intent.putExtra(NoteItem.KEY, note.getKey());
				intent.putExtra(NoteItem.TITLE, note.getTitle());
				intent.putExtra(NoteItem.TEXT, note.getText());
				intent.putExtra(NoteItem.ABSPATH, note.getAbsPath());
				startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDITOR_ACTIVITY_REQUEST && resultCode == RESULT_OK) {

			NoteItem note = new NoteItem();
			if (!(data.getStringExtra(NoteItem.TITLE).trim().equals("") && data
					.getStringExtra(NoteItem.TEXT).trim().equals(""))) {
				note.setKey(data.getStringExtra(NoteItem.KEY));
				note.setTitle(data.getStringExtra(NoteItem.TITLE));
				note.setText(data.getStringExtra(NoteItem.TEXT));
				note.setAbsPath(data.getStringExtra(NoteItem.ABSPATH));
				note.setLastEditTime(data.getStringExtra(NoteItem.LASTEDIT));
				Log.i("abspath", note.getAbsPath());
				if (searchMode == true) {
					searchMode = false;
					currentPath = note.getAbsPath().substring(0,
							note.getAbsPath().length() - 25);
				}
				if (recentMode == true) {
					recentMode = false;
					currentPath = note.getAbsPath().substring(0,
							note.getAbsPath().length() - 25);
				}

				datasource.updateNote(note, currentPath);
				refreshDisplay();
			}

		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		currentFileId = (int) info.id;
		menu.add(0, MENU_DELETE_ID, 0, R.string.delete);
		if (currentFileId < folderList.size() && searchMode == false
				&& recentMode == false) {
			menu.add(0, MENU_RENAME_ID, 1, R.string.rename);
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		if (item.getItemId() == MENU_DELETE_ID) {
			if (currentFileId < folderList.size() && searchMode == false
					&& recentMode == false) {
				FolderItem folder = folderList.get(currentFileId);
				datasource.remove(folder, currentPath);
				refreshDisplay();
			}

			else {
				if (searchMode == true) {
					NoteItem note = searchResultList.get(currentFileId);
					searchResultList.remove(currentFileId);
					datasource.remove(
							note,
							note.getAbsPath().substring(0,
									note.getAbsPath().length() - 25));
					showSearchResult();
				} else if (recentMode == true) {
					NoteItem note = recentResultList.get(currentFileId);
					recentResultList.remove(currentFileId);
					datasource.remove(
							note,
							note.getAbsPath().substring(0,
									note.getAbsPath().length() - 25));
					showRecentResult();
				} else {
					NoteItem note = notesList.get(currentFileId
							- folderList.size());
					datasource.remove(note, currentPath);
					refreshDisplay();
				}
			}

		}

		else if (item.getItemId() == MENU_RENAME_ID) {
			FolderItem folder = folderList.get(currentFileId);
			createFolder(folder);
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {

		searchMode = true;
		recentMode = false;
		searchResultList = datasource.searchNote(query);
		showSearchResult();
		return true;
	}

	private void showSearchResult() {

		if (!searchResultList.isEmpty()) {
			actionBar.setTitle("Search Result");
			FileAdapter<NoteItem> adapter = new FileAdapter<NoteItem>(this,
					searchResultList, 0);

			setListAdapter(adapter);
		} else {
			searchMode = false;
			refreshDisplay();
		}
	}

	private void showRecentResult() {

		if (!recentResultList.isEmpty()) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle("Recent Edit");
			FileAdapter<NoteItem> adapter = new FileAdapter<NoteItem>(this,
					recentResultList, 0);

			setListAdapter(adapter);
		} else {
			recentMode = false;
			refreshDisplay();
		}
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return true;
	}

}
