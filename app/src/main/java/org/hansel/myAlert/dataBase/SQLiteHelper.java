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
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper{
	private Context mCtx = null;
	private DataBaseHelperInternal mDBHelper = null;
	protected SQLiteDatabase mDb = null;
	/** constructor **/
	public SQLiteHelper(Context ctx){
		this.mCtx = ctx;
	}
	public SQLiteHelper open() throws SQLException{
		Log.d(SQLiteHelper.class.getSimpleName(),"== Opening DataBase ");
		mDBHelper = new DataBaseHelperInternal(mCtx);
		mDb = mDBHelper.getWritableDatabase();
		return this;
	}
	public void close(){
		Log.d(SQLiteHelper.class.getSimpleName(),"== Closing Database ");
		mDBHelper.close();
	}
	/** Clase privada para el control del SQLite **/
	private static class DataBaseHelperInternal extends SQLiteOpenHelper{
		public DataBaseHelperInternal(Context context) {
			super(context, DBConstants.DATABASE_NAME, null,
					DBConstants.DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DBConstants.sqlTablaUsuario);
			db.execSQL(DBConstants.sqlTablaContactoMail);
			db.execSQL(DBConstants.sqlTablaContactoNumero);
			db.execSQL(DBConstants.sqlTablaContacto);
			db.execSQL(DBConstants.sqlTablaConfig);
			db.execSQL(DBConstants.sqlTablaAlerta);
			db.execSQL(DBConstants.sqlTablaEvento);
			db.execSQL(DBConstants.sqlTablaTrack);
			db.execSQL(DBConstants.sqlRingsTable);
			db.execSQL(DBConstants.sqlContactsRingsTable);
			db.execSQL(DBConstants.sqlFlipTable);
			insertarDemo(db);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
			/**NOTA: Por simplicidad del ejemplo aqu� utilizamos directamente la opci�n de
eliminar la tabla anterior y crearla de nuevo vac�a con el nuevo formato.
Sin embargo lo normal ser� que haya que migrar datos de la tabla antigua
a la nueva, por lo que este m�todo deber�a ser m�s elaborado.**/
			//Se elimina la versi�n anterior de la tabla
			db.execSQL("DROP TABLE IF EXISTS t_Contacto");
			onCreate(db);
		}
		public void insertarDemo(SQLiteDatabase db){
			System.out.println("Se estan insertando en las tablas------------->>>");
			// db.execSQL("insert into sesion (nombre, descripcion) values ('Seleccione un servicio','uno')");
		}
	}
}

