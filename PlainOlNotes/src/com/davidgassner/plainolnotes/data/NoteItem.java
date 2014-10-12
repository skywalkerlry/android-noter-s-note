package com.davidgassner.plainolnotes.data;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.davidgassner.plainolnotes.dateUtils.DateUtils;

public class NoteItem implements Comparable<NoteItem>{

	private String key;
	private String title;
	private String text;
	private String absPath;
	private String lastEditTime;
	
	public static final String TITLE = "title";
	public static final String TEXT = "text";
	public static final String KEY = "key";
	public static final String ABSPATH = "absPath";
	public static final String LASTEDIT = "lastEdit";
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAbsPath() {
		return absPath;
	}
	public void setAbsPath(String absPath) {
		this.absPath = absPath;
	}
	public String getLastEditTime() {
		return lastEditTime;
	}
	public void setLastEditTime(String lastEditTime) {
		this.lastEditTime = lastEditTime;
	}
	
	public static NoteItem getNew() {
		
		String key = DateUtils.getDate();
		NoteItem note = new NoteItem();
		note.setKey(key);
		note.setTitle("");
		note.setText("");
		note.setAbsPath("");
		note.setLastEditTime(key);
		return note;
		
	}
	@Override
	public int compareTo(NoteItem another) {
		
		return this.getLastEditTime().compareTo(another.getLastEditTime());
	}
	
}
