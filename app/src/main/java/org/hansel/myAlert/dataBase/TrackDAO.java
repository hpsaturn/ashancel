package org.hansel.myAlert.dataBase;
/*This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
Created by Javier Mejia @zenyagami
zenyagami@gmail.com
	*/
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.hansel.myAlert.Log.Log;

public class TrackDAO extends SQLiteHelper{
	
	/*public static final String DATABASE_NAME ="t_Track";
	public static final String KEY_ID = "_id";
	public static final String FECHA = "fecha";
	public static final String ANDROID_ID = "androidId";*/
	
	
	public TrackDAO(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	} 
	
	public int getList(String mUsr,String mPass){
		int id = 0;
		Cursor c = super.mDb.rawQuery("select top 1 max(_id) from " + 
				DBConstants.TABLE_TRACK, null );
		
		if(c.moveToFirst()){
			id = c.getInt(0);
		}
		return id;
	}
	
	private int getLastAndroidId(){
		int id = 0;
		Cursor c = super.mDb.rawQuery("select max("+  DBConstants.ANDROID_ID + 
				") from " +	DBConstants.TABLE_TRACK + " LIMIT 1", null );
		
		if(c.moveToFirst()){
			id = c.getInt(0);
		}
		return id;
	}
	
	public long Insertar(String mUsr) {
		ContentValues newValues = new ContentValues();		
		int idAnd = (getLastAndroidId() +1 );
		newValues.put(DBConstants.ANDROID_ID,(idAnd));
		newValues.put(DBConstants.FECHA, mUsr);
		super.mDb.insert(DBConstants.TABLE_TRACK, null, newValues);
		return idAnd;
	}
	
	public void borraTabla(){
		mDb.delete(DBConstants.TABLE_TRACK, null, null);
	}
	
	public long InsertaNewId(String fecha,long id) {
		borraTabla();
		ContentValues newValues = new ContentValues();
        Log.v("=== Guardando TrackID en la tabla " + id);
		newValues.put(DBConstants.KEY_ID, id);
        newValues.put(DBConstants.ANDROID_ID,id);
		newValues.put(DBConstants.FECHA, fecha);
		return super.mDb.insert(DBConstants.TABLE_TRACK, null, newValues);
	}

	public long getTrackId(){
		long id = 0;
		Cursor c = super.mDb.rawQuery("select * from " + DBConstants.TABLE_TRACK, null );

		if(c.moveToFirst()){
			id = c.getLong(0);
			Log.v("=== Obteniendo TrackID de la tabla " + id);
		}
		return id;
	}
}