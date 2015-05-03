package in.swifiic.app.photoapp.andi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PhotoDB extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "photoapp.db";
	private static final String TABLE_PHOTOAPP = "photoapp";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SERVER_ID = "server_id";
	public static final String COLUMN_IMG_NAME = "img_name";
	public static final String COLUMN_UPVOTE = "upvotes";
	private static final String PHOTO_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_PHOTOAPP
			+ " ("
			+ COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_SERVER_ID
			+ " INT NOT NULL UNIQUE, "
			+ COLUMN_IMG_NAME
			+ " STRING NOT NULL, "
			+ COLUMN_UPVOTE + " INT DEFAULT 0);";

	public PhotoDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public PhotoDB(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(PHOTO_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PhotoDB.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOAPP);
		onCreate(db);
	}

	public void insertRow(Context context, int serverId, String imgName,
			int upvotes) throws SQLiteException {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues vals = new ContentValues();
		vals.put(COLUMN_SERVER_ID, serverId);
		vals.put(COLUMN_IMG_NAME, imgName);
		vals.put(COLUMN_UPVOTE, upvotes);
		db.insertOrThrow(TABLE_PHOTOAPP, null, vals);
		Log.d("DB_INFO", "New row added");
		db.close();
	}
	
	public boolean getId(int imgId) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PHOTOAPP + " WHERE " + COLUMN_SERVER_ID + " = " + imgId, null);
		boolean hasObject = false;
	    if(cursor.moveToFirst()){
	    	//record exists; user already has the same image
	        hasObject = true;
	    }
		cursor.close();
		db.close();
		return hasObject;
	}

}
