package quique.proyecto.excercise;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class DatabaseHandler2 extends SQLiteOpenHelper {

	//Database Version
	private static final int DATABASE_VERSION = 1;
	
	//Database Name
	private static final String DATABASE_NAME = "MapPoints1";
	
	//Table name
	private static final String TABLE_MAP_POINTS = "Points_Table_2";
	
	//Table Column names
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	
	SQLiteDatabase db;
	
	private Context contextG;
	
	public DatabaseHandler2(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		db = getWritableDatabase();
		db.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE "+TABLE_MAP_POINTS+" ("
				+ KEY_LATITUDE + " REAL," + KEY_LONGITUDE + " REAL)";
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	       // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAP_POINTS);
 
        // Create tables again
        onCreate(db);		
	}

	public void addPoint(LatLng location) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		values.put(KEY_LATITUDE, location.latitude);
		values.put(KEY_LONGITUDE, location.longitude);
		
		db.insert(TABLE_MAP_POINTS, null, values);
		db.close();
	}
	
	public List<LatLng> getAllPoints(){
		List<LatLng> PointsList = new ArrayList<LatLng>();
		
		String selectQuery = "SELECT * FROM  " + TABLE_MAP_POINTS;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Toast.makeText(contextG, "Getting all Points Count:"+Integer.toString(cursor.getCount()),
                Toast.LENGTH_SHORT).show();
		if(cursor.moveToFirst()){
			do{
				LatLng point = new LatLng(cursor.getDouble(0),cursor.getDouble(1));
				PointsList.add(point);
			} while(cursor.moveToNext());
		}
		
		return PointsList;
	}
}
