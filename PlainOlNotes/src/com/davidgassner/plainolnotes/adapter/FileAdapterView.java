package com.davidgassner.plainolnotes.adapter;

import com.davidgassner.plainolnotes.R;
import com.davidgassner.plainolnotes.data.FolderItem;
import com.davidgassner.plainolnotes.data.NoteItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;


public class FileAdapterView extends LinearLayout {

	public FileAdapterView(Context context, FolderItem folder) {
		super(context);
				
		 this.setOrientation(HORIZONTAL);          
         LinearLayout.LayoutParams folderLayout =   
             new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);    

         TextView folderText = new TextView( context );  
         folderText.setText(folder.getName());
         Drawable drawable = getResources().getDrawable(R.drawable.ic_action_collections_collection);
         drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
         folderText.setCompoundDrawables(drawable, null, null, null);
         folderText.setTextSize(20);
         folderText.setCompoundDrawablePadding(10);
         folderText.setPadding(5, 10, 5, 10);
         folderText.setEllipsize(TextUtils.TruncateAt.END);
         folderText.setSingleLine(true);
         folderText.getPaint().setFakeBoldText(true);
         addView(folderText, folderLayout);         
	}
	
	public FileAdapterView(Context context, NoteItem note) {
		super(context);
				
		 this.setOrientation(HORIZONTAL);          
         LinearLayout.LayoutParams folderLayout =   
             new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);    
         
         TextView folderText = new TextView( context );
         if (note.getTitle().trim().equals(""))
        	 folderText.setText(note.getText());
         else
        	 folderText.setText(note.getTitle());
         Drawable drawable = getResources().getDrawable(R.drawable.ic_action_content_doc);
         drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
         folderText.setCompoundDrawables(drawable, null, null, null);
         folderText.setTextSize(20);
         folderText.setCompoundDrawablePadding(10);
         folderText.setPadding(5, 10, 5, 10);
         folderText.setEllipsize(TextUtils.TruncateAt.END);
         folderText.setSingleLine(true);
         addView(folderText, folderLayout);
         
	}
	
}
