package org.hansel.myAlert.dataBase;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class RingDAO extends SQLiteHelper {
	public RingDAO(Context ctx) {
		super(ctx);
	}

	public Cursor getRingsByContactCursor(String idContact) {
		String query = "select * from " + DBConstants.TABLE_RINGS
				+" where " + DBConstants.TABLE_CONTACS_RINGS + "._ID_CONTACT = '"
				+ idContact + "' AND " + DBConstants.TABLE_CONTACS_RINGS +
				"._ID_RING = " + DBConstants.TABLE_RINGS + "._ID";
		Cursor c = super.mDb.rawQuery(query, null);
		return c;
	}

	public Cursor getAllContactsRing(String idRing){
		String query = "select _id_contact from " + DBConstants.TABLE_CONTACS_RINGS
				+" where _ID_RING = " + idRing;
		Cursor c = super.mDb.rawQuery(query, null);
		return c;

	}

	public Cursor getRingsByNameCursor(String name) {
		String query = "select * from " + DBConstants.TABLE_RINGS
				+" where NAME like '%" + name +"%'";
		Cursor c = super.mDb.rawQuery(query, null);
		return c;
	}

	public Cursor getRigsCursor(){
		Cursor c = super.mDb.rawQuery("select * from " + DBConstants.TABLE_RINGS,
				null);
		return c;
	}

	public Cursor getRing(String id){
		Cursor c = super.mDb.rawQuery("select * from " + DBConstants.TABLE_RINGS
				+ " where _id = ?1", new String[]{id});
		return c;
	}

	public long addContactToRing(String idContact, String idRing) {
		ContentValues newValues = new ContentValues();
		newValues.put("_ID_RING", idRing);
		newValues.put("_ID_CONTACT", idContact);
		return super.mDb.insert(DBConstants.TABLE_CONTACS_RINGS, null,
				newValues);
	}

	public boolean deleteContactFromRing(String idContact, String idRing){
		String whereClause = "_ID_RING = ?1 AND _ID_CONTACT = ?2" ;
		String[] whereArgs = new String[2];
		whereArgs[0] = idRing;
		whereArgs[1] = idContact;
		return super.mDb.delete(DBConstants.TABLE_CONTACS_RINGS, whereClause,
				whereArgs) > 0;
	}

	public long addRing(String name, boolean notify){
		int ringNotify = notify==true?1:0;
		ContentValues newValues = new ContentValues();
		newValues.put("name", name);
		newValues.put("notify", String.valueOf(ringNotify));
		return super.mDb.insert(DBConstants.TABLE_RINGS, null, newValues);
	}

	public long updateRing(String id, String name, boolean notify, List<String> contacts){
		int result = -1;
		ContentValues values = new ContentValues();
		values.put("NAME", name);
		values.put("NOTIFY",notify==true?1:0);

		String whereClause = "_ID = " + id;
		super.mDb.beginTransaction();
		try{
			result = super.mDb.update(DBConstants.TABLE_RINGS, values, whereClause,
					null);

			whereClause = "_ID_RING = " + id;
			super.mDb.delete(DBConstants.TABLE_CONTACS_RINGS, whereClause, null);

			ContentValues insertValues = new ContentValues();
			insertValues.put("_ID_RING", id);
			Iterator <String> it = contacts.iterator();

			while(it.hasNext()){
				insertValues.put("_ID_CONTACT", it.next());
				super.mDb.insert(DBConstants.TABLE_CONTACS_RINGS, null, insertValues);
			}

			mDb.setTransactionSuccessful();
		}
		finally {
			mDb.endTransaction();
		}
		return result;
	}

	public long updateNotification(String id, boolean notify){
		int result = -1;
		ContentValues values = new ContentValues();
		values.put("NOTIFY",notify==true?1:0);

		String whereClause = "_ID = " + id;
		super.mDb.beginTransaction();
		try{
			result = super.mDb.update(DBConstants.TABLE_RINGS, values, whereClause,
					null);

			mDb.setTransactionSuccessful();
		}
		finally {
			mDb.endTransaction();
		}
		return result;
	}

	public long deleteRing(String idRing){
		String whereClause1 = "_ID_RING = " + idRing;
		String whereClause2 = "_id = " + idRing;
		long result = 0;

		super.mDb.beginTransaction();
		try{
			result = super.mDb.delete(DBConstants.TABLE_CONTACS_RINGS,
					whereClause1, null);
			result += super.mDb.delete(DBConstants.TABLE_RINGS,
					whereClause2, null);
			super.mDb.setTransactionSuccessful();
		}
		finally {
			super.mDb.endTransaction();
		}
		return result;
	}

	public Cursor getNotificationContactsId(){
		Cursor c = super.mDb.rawQuery("select _ID_CONTACT from " +
				DBConstants.TABLE_RINGS + "," + DBConstants.TABLE_CONTACS_RINGS
				+ " where notify = 1 and _ID_RING = _ID", null);
		return c;
	}
}