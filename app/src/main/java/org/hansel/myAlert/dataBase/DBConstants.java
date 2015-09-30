package org.hansel.myAlert.dataBase;

public class DBConstants {
	public static final String TABLE_TRACK = "T_TRACK";
	public static final String TABLE_USERS = "T_USUARIO";
	public static final String TABLE_CONTACTS = "T_CONTACTO";
	public static final String TABLE_RINGS = "T_RINGS"; 
	public static final String TABLE_CONTACS_RINGS = "T_CONTACTS_RINGS";
	public static final String TABLE_FLIP_SETTINGS = "T_FLIP_SETTINGS";
	public static final String DATABASE_NAME ="hancel-db";
	public static final String KEY_ID = "_id";
	public static final String FECHA = "fecha";
	public static final String ANDROID_ID = "androidId";
	public static final int DATABASE_VERSION = 3;
	public final static String IdPadre = "id_padre_interno";
	public static final String telefonos= "phoneNumbers";
	public static final String emails= "emails";
	public static final String fkIdUsuario = "fkIdUsuario";
	public final static String usuario = "usuario";
	public static final String password = "password";
	public static final String mail = "mail";
	public static final String sqlTablaUsuario = "CREATE TABLE T_USUARIO (_id INTEGER PRIMARY KEY NOT NULL , usuario TEXT, password TEXT, mail TEXT)";
	public static final String sqlTablaContactoMail = "CREATE TABLE C_CONTACTOMAIL (_id INTEGER PRIMARY KEY NOT NULL , mail TEXT,fkIdContacto INTEGER)";
	public static final String sqlTablaContactoNumero = "CREATE TABLE C_CONTACTONUMERO (_id INTEGER PRIMARY KEY NOT NULL , numero INTEGER,fkIdContacto INTEGER)";
	public static final String sqlTablaContacto = "CREATE TABLE T_CONTACTO (_id INTEGER PRIMARY KEY NOT NULL , id_padre_interno INTEGER, phoneNumbers TEXT, emails TEXT)";
	public static final String sqlTablaConfig = "CREATE TABLE T_CONFIG (_id INTEGER PRIMARY KEY NOT NULL , intevalo TEXT, esperaAlerta TEXT, fkIdSesion INTEGER, mensaje TEXT)";
	public static final String sqlTablaAlerta = "CREATE TABLE T_ALERTA (_id INTEGER PRIMARY KEY NOT NULL , esperaAlerta TEXT, fkIdSesion INTEGER)";
	public static final String sqlTablaEvento = "CREATE TABLE T_EVENTO (_id INTEGER PRIMARY KEY NOT NULL , estatus INTEGER, fkIdSesion INTEGER)";
	public static final String sqlTablaTrack = "CREATE TABLE T_TRACK (_id INTEGER PRIMARY KEY NOT NULL ,androidId INTEGER NOT NULL, fecha TEXT)";
	public static final String sqlRingsTable = "CREATE TABLE T_RINGS (_id INTEGER PRIMARY KEY NOT NULL, name TEXT, notify INTEGER)";
	public static final String sqlContactsRingsTable = "CREATE TABLE T_CONTACTS_RINGS (_ID_RING INTEGER, _ID_CONTACT INTEGER, PRIMARY KEY (_ID_RING, _ID_CONTACT), FOREIGN KEY(_ID_RING) REFERENCES T_RINGS(_ID))";
	public static final String sqlFlipTable = "CREATE TABLE " + TABLE_FLIP_SETTINGS + "(_KEY TEXT, VALUE TEXT, PRIMARY KEY (_KEY))";

}