package com.davidgassner.plainolnotes.adapter;

import java.util.List;

import com.davidgassner.plainolnotes.data.FolderItem;
import com.davidgassner.plainolnotes.data.NoteItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FileAdapter <Object> extends BaseAdapter {
	
	private Context context;
	private List<Object> fileList;
	private int folderNum;

	public FileAdapter(Context context, List<Object> fileList, int folderNum) {
		this.context = context;
		this.fileList = fileList;
		this.folderNum = folderNum;
	}

	@Override
	public int getCount() {
		
		return fileList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return fileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position<folderNum){
			FolderItem folder = (FolderItem)fileList.get(position);
			return new FileAdapterView(this.context, folder);
		}
		else {
			NoteItem note = (NoteItem)fileList.get(position);
			return new FileAdapterView(this.context, note);
		}
	}

}
