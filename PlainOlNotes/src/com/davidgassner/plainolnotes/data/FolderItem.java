package com.davidgassner.plainolnotes.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.davidgassner.plainolnotes.dateUtils.DateUtils;

public class FolderItem implements Comparable<FolderItem>{
	
	private String name;
	private String key;
	private List<String> folderList;
	private List<String> noteList;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<String> getFolderList() {
		return folderList;
	}
	public void setFolderList(List<String> folderList) {
		this.folderList = folderList;
	}
	public List<String> getNoteList() {
		return noteList;
	}
	public void setNoteList(List<String> noteList) {
		this.noteList = noteList;
	}
	
	public static FolderItem getNew(String name1) {
		
		String key = DateUtils.getDate();
		FolderItem folder = new FolderItem();
		folder.setKey(key);
		folder.setName(name1);
		folder.setFolderList(new ArrayList<String>());
		folder.setNoteList(new ArrayList<String>());
		return folder;
	}
	@Override
	public int compareTo(FolderItem another) {
		
		return this.getName().compareTo(another.getName());
	}

}
