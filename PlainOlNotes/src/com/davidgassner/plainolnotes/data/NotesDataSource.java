package com.davidgassner.plainolnotes.data;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.util.Log;

public class NotesDataSource {

	private static final String PREFNOTE = "notes";
	private static final String PREFFOLDER = "folder";
	private SharedPreferences notePrefs;
	private SharedPreferences folderPrefs;

	public NotesDataSource(Context context) {
		notePrefs = context
				.getSharedPreferences(PREFNOTE, Context.MODE_PRIVATE);
		folderPrefs = context.getSharedPreferences(PREFFOLDER,
				Context.MODE_PRIVATE);
	}

	public List<FolderItem> findAllFolders(String path) {

		Gson gson = new Gson();
		String json = folderPrefs.getString(path, "");
		FolderItem folder = gson.fromJson(json, FolderItem.class);
		List<FolderItem> folderList = new ArrayList<FolderItem>();

		List<String> folders = folder.getFolderList();
		for (String key : folders) {
			json = folderPrefs.getString(key, "");
			FolderItem folder_temp = gson.fromJson(json, FolderItem.class);
			folderList.add(folder_temp);
		}

		return folderList;

	}

	public List<NoteItem> findAllNotes(String path) {

		Gson gson = new Gson();
		String json = folderPrefs.getString(path, "");
		FolderItem folder = gson.fromJson(json, FolderItem.class);

		List<NoteItem> noteList = new ArrayList<NoteItem>();
		List<String> notes = folder.getNoteList();
		
		for (String key : notes) {
			Gson gson_temp = new Gson();
			String json_temp = notePrefs.getString(key, "");
			NoteItem note_temp = gson_temp.fromJson(json_temp, NoteItem.class);
			noteList.add(note_temp);
		}

		return noteList;

	}

	public boolean containFolder(String key) {
		if (folderPrefs.contains(key))
			return true;
		else
			return false;
	}

	public boolean updateFolder(FolderItem folder, String path) {

		SharedPreferences.Editor editor = folderPrefs.edit();
		String key = path.concat(folder.getKey());
		Gson gson = new Gson();
		String json = gson.toJson(folder);
		editor.putString(key, json);

		if (!folder.getKey().equals("")) {

			json = folderPrefs.getString(path, "");
			FolderItem folderPrev = gson.fromJson(json, FolderItem.class);
			if (!folderPrev.getFolderList().contains(key)) {
				folderPrev.getFolderList().add(key);

				json = gson.toJson(folderPrev);
				editor.putString(path, json);
			}

		}
		editor.commit();
		return true;
	}

	public boolean updateNote(NoteItem note, String path) {
		SharedPreferences.Editor editor = notePrefs.edit();
		String key = path.concat(note.getKey());
		note.setAbsPath(key);
		Gson gson = new Gson();
		String json = gson.toJson(note);
		editor.putString(key, json);
		editor.commit();
		
		editor = folderPrefs.edit();
		json = folderPrefs.getString(path, "");
		FolderItem folderPrev = gson.fromJson(json, FolderItem.class);
		if (!folderPrev.getNoteList().contains(key)){
			folderPrev.getNoteList().add(0, key);
			json = gson.toJson(folderPrev);
			editor.putString(path, json);
		
			editor.commit();
		}
		return true;
	}

	public boolean remove(NoteItem note, String path) {
		String key = path.concat(note.getKey());
		if (notePrefs.contains(key)) {
			SharedPreferences.Editor editor = notePrefs.edit();
			editor.remove(key);
			editor.commit();
			
			editor = folderPrefs.edit();
			Gson gson = new Gson();
			String json = folderPrefs.getString(path, "");
			FolderItem folderPrev = gson.fromJson(json, FolderItem.class);
			folderPrev.getNoteList().remove(key);
			json = gson.toJson(folderPrev);
			editor.putString(path, json);

			editor.commit();

		}

		return true;
	}

	public boolean remove(FolderItem folder, String path) {
		
		SharedPreferences.Editor folderEditor = folderPrefs.edit();
		SharedPreferences.Editor noteEditor = notePrefs.edit();
		
		Gson gson = new Gson();
		String json;
		
		String key = path.concat(folder.getKey());

		if (folderPrefs.contains(key)) {
			for (String note : folder.getNoteList()) {
				noteEditor.remove(note);
			}
			
			noteEditor.commit();
			
			for (String item : folder.getFolderList()) {
				json = folderPrefs.getString(item, "");
				FolderItem subFolder = gson.fromJson(json, FolderItem.class);
				remove(subFolder, key);
			}
			
			folderEditor.remove(key);
			
			json = folderPrefs.getString(path, "");
			FolderItem folderPrev = gson.fromJson(json, FolderItem.class);
			folderPrev.getFolderList().remove(key);
			json = gson.toJson(folderPrev);
			folderEditor.putString(path, json);

			folderEditor.commit();

		}

		return true;
	}
	
	public String getActionBarTitle(String path) {
		Gson gson = new Gson();
		String json = folderPrefs.getString(path, "");
		FolderItem folderPrev = gson.fromJson(json, FolderItem.class);
		return folderPrev.getName();
	}
	
	public List<NoteItem> searchNote(String query) {
		List<NoteItem> resultList = new ArrayList<NoteItem>();
		Map<String, ?> notesMap = notePrefs.getAll();
		SortedSet<String> keys = new TreeSet<String>(notesMap.keySet());
		Gson gson = new Gson();
		String json;
		for (String key : keys) {
			json = notePrefs.getString(key, "");
			NoteItem note = gson.fromJson(json, NoteItem.class);
			if (note.getTitle().contains(query) || note.getText().contains(query)){
				resultList.add(note);
			}
		}
		return resultList;
	}
	
	public List<NoteItem> getAllNotes() {
		List<NoteItem> allNotes = new ArrayList<NoteItem>();
		Map<String, ?> notesMap = notePrefs.getAll();
		SortedSet<String> keys = new TreeSet<String>(notesMap.keySet());
		Gson gson = new Gson();
		String json;
		for (String key : keys) {
			json = notePrefs.getString(key, "");
			NoteItem note = gson.fromJson(json, NoteItem.class);
			allNotes.add(note);
		}
		return allNotes;
	}
	

	public boolean clearAll() {
		SharedPreferences.Editor editor1 = folderPrefs.edit();
		editor1.clear();
		editor1.commit();
		SharedPreferences.Editor editor2 = notePrefs.edit();
		editor2.clear();
		editor2.commit();
		return true;
	}

}
