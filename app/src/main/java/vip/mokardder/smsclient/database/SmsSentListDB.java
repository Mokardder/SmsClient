package vip.mokardder.smsclient.database;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import vip.mokardder.smsclient.models.sms_list_payload;


public class SmsSentListDB extends SQLiteOpenHelper {
    Context context;
    private static final String TAG = "Mokardder===>";
    private static final String DB_NAME = "sms_sent_list";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "sms_sent_table";
    private static final String KEY_ID = "id";

    private static final String KEY_SENT_TO = "sent_to";
    private static final String KEY_SMS_CONTENT = "sms_content";
    private static final String KEY_TIME = "time";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            KEY_SENT_TO + " TEXT," +
            KEY_SMS_CONTENT + " TEXT," +
            KEY_TIME + " TEXT" +
            ")";
    private static final String DROP_DB = "DROP TABLE IF EXISTS " + TABLE;

    public SmsSentListDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_DB);
        onCreate(db);
    }



    public boolean addSms(String sent_to,
                                      String sms_content,
                                      String date
                          ) {
        if (!isTableExists()) {
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL(CREATE_TABLE);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_SENT_TO, sent_to);
        cv.put(KEY_SMS_CONTENT, sms_content);
        cv.put(KEY_TIME, date);

        long result = db.insert(TABLE, null, cv);
        db.close();

        return result != -1;

    }

//    public ArrayList<AssignedTask> getTasks() {
//        ArrayList<AssignedTask> arr = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//        String currentDate = dateFormat.format(new Date());
//        String query  = "SELECT * FROM " + TABLE_DAC + " WHERE " + KEY_ORDER_DATE + " < ?";
//        Cursor cursor = db.rawQuery(query, new String[]{currentDate});
//
//        while (cursor.moveToNext()) {
//            AssignedTask model = new AssignedTask(
//                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_ORDER_DATE)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_CASHMEMO)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONS_NAME)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOKING_REF)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONS_NUMBER)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONS_NAME)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOBILE_NO)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_ORDER_BY))
//            );
//            arr.add(model);
//        }
//        cursor.close();
//        db.close();
//        return arr;
//    }

    public void truncate_table() {
        SQLiteDatabase db = this.getWritableDatabase();

        if (isTableExists()) {
            db.execSQL(DROP_DB);
            db.execSQL(CREATE_TABLE);
        } else {
            db.execSQL(CREATE_TABLE);
        }

    }

    public boolean isTableExists() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE + "'", null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    public boolean isTableEmpty() {

        SQLiteDatabase db = this.getReadableDatabase();
        if (!isTableExists()) {
            return true;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count == 0;
    }


    public List<sms_list_payload> getAllSmsLists() {
        List<sms_list_payload> unsyncedConsumers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { KEY_SENT_TO, KEY_SMS_CONTENT, KEY_TIME};

        Cursor cursor = null;

        try {

            cursor = db.query(
                    TABLE,      // The table to query
                    columns,    // The columns to return
                    null,  // The columns for the WHERE clause
                    null,  // The values for the WHERE clause
                    null,       // Don't group the rows
                    null,       // Don't filter by row groups
                    null        // The sort order
            );

            while (cursor != null && cursor.moveToNext()) {

                String sent_to = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SENT_TO));
                String sms_content = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SMS_CONTENT));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME));


                sms_list_payload consumer = new sms_list_payload(
                        sent_to,
                        sms_content,
                        time
                );

                unsyncedConsumers.add(consumer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return unsyncedConsumers;
    }

//    public void deleteSingleRow(String _id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        int result = db.delete(TABLE,  KEY + "=?", new String[]{_id});
//        if (result > 0) {
//            Log.d("SQLite", "Row deleted successfully.");
//        } else {
//            Log.d("SQLite", "Row not found or couldn't be deleted.");
//        }
//        db.close();
//    }

}
