package org.hansel.myAlert.Utils;

import java.io.Serializable;

import org.linphone.Contact;

import android.graphics.Bitmap;
import android.net.Uri;

public class ContactRing implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String contactId;
	private String contactName;
	private transient Uri photoUri;
	private transient Bitmap photo;
	private boolean selected;
	
	
	public ContactRing(String contactId, String contactName, 
			Uri photoUri, Bitmap photo, boolean selected){
		this.contactId = contactId;
		this.contactName = contactName;
		this.photoUri = photoUri;
		this.photo = photo;
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getContactName() {
		return contactName;
	}
	
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	
	public Uri getPhotoUri() {
		return photoUri;
	}
	
	public Bitmap getPhoto() {
		return photo;
	}
		
}
