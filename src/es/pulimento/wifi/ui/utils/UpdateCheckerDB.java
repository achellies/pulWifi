package es.pulimento.wifi.ui.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UpdateCheckerDB extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "pulwifi.db";
	private static final int DATABASE_VERSION = 1;
	public static final String UPDATER_TABLE_NAME = "updatechecker";
	public static final String ID_FIELD = "updaterID";
	public static final String TIMESTAMP_FIELD = "tstamp";
	private static final String UPDATER_TABLE_CREATE = "CREATE TABLE " + UPDATER_TABLE_NAME + " ("
			+ ID_FIELD + " INTEGER PRIMARY KEY, " + TIMESTAMP_FIELD + " NUMERIC);";

	UpdateCheckerDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(UPDATER_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Constants.TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + UPDATER_TABLE_NAME);
		onCreate(db);

	}
}