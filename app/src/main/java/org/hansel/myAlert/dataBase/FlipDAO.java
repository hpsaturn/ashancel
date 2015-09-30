package org.hansel.myAlert.dataBase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class FlipDAO extends SQLiteHelper {
	public FlipDAO(Context ctx) {
		super(ctx);
	}
	
	public Cursor getSettingsValueByKey(String key) {
		String query = "select * from " + DBConstants.TABLE_FLIP_SETTINGS 
				+ " where " + "_KEY = '" + key + "'";
		Cursor c = super.mDb.rawQuery(query, null);
		return c;
	}
	
	public Cursor getAllSettingsCursor(){
		String query = "select * from " + DBConstants.TABLE_FLIP_SETTINGS;				
		Cursor c = super.mDb.rawQuery(query, null);
		return c;
		
	}
			
	public long addkey(String key, String value) {
		ContentValues newValues = new ContentValues();
		newValues.put("_KEY", key);
		newValues.put("VALUE", value);
		return super.mDb.insert(DBConstants.TABLE_FLIP_SETTINGS, null,
				newValues);
	}
	
	public long createOrUpdateKey(String key, String value){
		super.mDb.beginTransaction();
		long result = -1;
		try{
			result = updateKeyValue(key, value);
			if(result == 0)
				result = addkey(key, value);
			super.mDb.setTransactionSuccessful();
		}
		finally {
			super.mDb.endTransaction();				
		}
		return result;
	}
	
	public boolean deleteKey(String key){
		String whereClause = "_KEY = ?1" ;
		String[] whereArgs = new String[1];
		whereArgs[0] = key;
		return super.mDb.delete(DBConstants.TABLE_FLIP_SETTINGS, whereClause,
				whereArgs) > 0;
	}
		
	public long updateKeyValue(String key, String value){
		int result = -1;
		ContentValues values = new ContentValues();
		values.put("VALUE", value);
		
		String whereClause = "_KEY = '" + key + "'";
		result = super.mDb.update(DBConstants.TABLE_FLIP_SETTINGS, values, whereClause, 
				null);
		return result;
	}
	
}
