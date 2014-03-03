package jp.zyyx.dynamicapp.plugins;

import jp.zyyx.dynamicapp.core.DynamicAppPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

public class Database extends DynamicAppPlugin {
	private final static int SQLITE_ERROR = 1;
	
	private SQLiteDatabase dbHandle;
	private SQLiteHelper sqliteHelper;
	private static Database instance = null;
	
	private Database() {}
	
	public static synchronized Database getInstance() {
        if (instance == null) {
	            instance = new Database();
	    }
	    return instance;
	}
	
	@Override
	public void execute() {
		if (methodName.equalsIgnoreCase("init")) {
			this.open();
		} else if (methodName.equalsIgnoreCase("executeSQL")) {
			this.executeSQL();
		} else if (methodName.equalsIgnoreCase("close")) {
			this.close();
		} else {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
		}
	}
	
	private void open() {
		String dbName = param.get("dbName", "");
		/*File dbPath = new File(DynamicAppUtils.makePath("db"));
		
		if(!dbPath.exists()) {
			dbPath.mkdirs();
		}*/
		
		if(dbHandle != null && dbHandle.isOpen()) {
			dbHandle.close();
			sqliteHelper = null;
		}
		
		sqliteHelper = new SQLiteHelper(dynamicApp.getApplicationContext(), dbName, null, 1);
		dbHandle = sqliteHelper.getWritableDatabase();

		dynamicApp.callJsEvent(PROCESSING_FALSE);
		if(dbHandle.isOpen()) {
			onSuccess();
		} else {
			Database.onError(String.valueOf(Database.SQLITE_ERROR), callbackId);
		}
	}
	
	private void close() {
		if(dbHandle != null && dbHandle.isOpen()) {
			dbHandle.close();
			sqliteHelper = null;
		}
		
		dynamicApp.callJsEvent(PROCESSING_FALSE);
		if(!dbHandle.isOpen()) {
			onSuccess();
		} else {
			Database.onError(String.valueOf(Database.SQLITE_ERROR), callbackId);
		}
	}
	
	private void executeSQL() {
		String sqlQuery = param.get("sql", "");
		boolean isSelectQuery = Boolean.valueOf(param.get("isSelectQuery","false"));
		JSONArray resultSet = new JSONArray();
		
		if(dbHandle != null && dbHandle.isOpen()) {
			if(isSelectQuery) {
				try {
					Cursor cursor = dbHandle.rawQuery(sqlQuery, null);
					if(cursor != null) {
						cursor.moveToFirst();
						
						int numResultColumns = cursor.getColumnCount();
						String[] columnNames = cursor.getColumnNames();
						
						Cursor typesCursor = null;
						boolean typesFlag = false;

						if(Build.VERSION.SDK_INT < 11) {
							String typesQuery = "SELECT ";
							for (int i = 0; i < numResultColumns; i++) {
								typesQuery += "typeof("+columnNames[i]+")"
										+ ((i==numResultColumns-1)?" ":", ");
							}
							typesQuery += "FROM (" + sqlQuery + ")";

							typesCursor = dbHandle.rawQuery(typesQuery, null);
							if(typesCursor != null) {
								typesFlag = true;
								typesCursor.moveToFirst();
							}
						}
						
						while (!cursor.isAfterLast()) {
							JSONObject row = new JSONObject();
							for (int i = 0; i < numResultColumns; i++) {
								int type;
								if(typesFlag) {
									String cType = typesCursor.getString(i);
									if(cType.equalsIgnoreCase("INTEGER")) {
										type = Cursor.FIELD_TYPE_INTEGER;
									} else if(cType.equalsIgnoreCase("REAL")) {
										type = Cursor.FIELD_TYPE_FLOAT;
									} else if(cType.equalsIgnoreCase("TEXT")) {
										type = Cursor.FIELD_TYPE_STRING;
									} else if(cType.equalsIgnoreCase("BLOB")) {
										type = Cursor.FIELD_TYPE_BLOB;
									} else {
										type = Cursor.FIELD_TYPE_NULL;
									}
								} else {
									type = cursor.getType(i);
								}

								try {
									switch (type) {
									case Cursor.FIELD_TYPE_INTEGER:
										row.put(columnNames[i], cursor.getInt(i));
										break;
									case Cursor.FIELD_TYPE_FLOAT:
										row.put(columnNames[i], cursor.getFloat(i));
										break;
									case Cursor.FIELD_TYPE_STRING:
										row.put(columnNames[i], cursor.getString(i));
										break;
									case Cursor.FIELD_TYPE_BLOB:
										row.put(columnNames[i], cursor.getBlob(i));
										break;
									default: //Cursor.FIELD_TYPE_NULL
										row.put(columnNames[i], "null");
										break;
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							resultSet.put(row);
							cursor.moveToNext();
							if(typesCursor != null) {
								typesCursor.moveToNext();
							}
						}
						cursor.close();
						if(typesCursor != null) {
							typesCursor.close();
						}
					}
				} catch (Exception e) {
					resultSet = null;
					e.printStackTrace();
				}
			} else {
				try {
					dbHandle.execSQL(sqlQuery);
				} catch (Exception e) {
					resultSet = null;
					e.printStackTrace();
				}
			}
		}

		dynamicApp.callJsEvent(PROCESSING_FALSE);
		if(resultSet != null) {
			Database.onSuccess(resultSet, callbackId, false);
		} else {
			Database.onError(String.valueOf(Database.SQLITE_ERROR), callbackId);
		}
	}
}

class SQLiteHelper extends SQLiteOpenHelper {
	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
}