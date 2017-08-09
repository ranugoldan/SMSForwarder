package id.technobit.smsforwarder.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import id.technobit.smsforwarder.model.Message;
import id.technobit.smsforwarder.model.Whitelist;
import me.everything.providers.android.telephony.Sms;

/**
 * Created by ranug on 26/04/2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "smsforwarder";

    private static final String TABLE_WHITELIST = "WHITELIST";

    private static final String TABLE_SMS = "SMS";

    private final String TAG = "DBHandler";

    private static final String KEY_ID = "id";
    private static final String KEY_WHITELIST_NUM = "whitelist_num";
    private static final String KEY_SMS_ID = "sms_id";
    private static final String KEY_SENDER = "sender";
    private static final String KEY_BODY = "body";
    private static final String KEY_DATE = "date";
    private static final String KEY_IS_FORWARDED = "is_forwarded";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WHITELIST_TABLE = "CREATE TABLE " + TABLE_WHITELIST + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WHITELIST_NUM + " TEXT" + ")";
        db.execSQL(CREATE_WHITELIST_TABLE);
        String CREATE_SMS_TABLE = "CREATE TABLE " + TABLE_SMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SENDER + " TEXT," + KEY_SMS_ID + " TEXT,"
                + KEY_BODY + " TEXT," + KEY_DATE + " TEXT" + ")";
        db.execSQL(CREATE_SMS_TABLE);
        Log.d(TAG, "Db and table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WHITELIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);

        onCreate(db);
    }

    public void addWhitelist(Whitelist whitelist){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WHITELIST_NUM, whitelist.getNumber());

        db.insert(TABLE_WHITELIST, null, values);
        db.close();
    }

    public void addSMS(Message message){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SMS_ID, message.getId());
        values.put(KEY_SENDER, message.getSenderNumber());
        values.put(KEY_BODY, message.getContent());
        values.put(KEY_DATE, message.getDate());

        db.insert(TABLE_SMS, null, values);
        db.close();
        Log.d("addSMS", message.getId()+ " added");
        getAllMessage();
    }

    public List<Message> getAllMessage(){
        List<Message> messageList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SMS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setSenderNumber(cursor.getString(1));
                message.setId(cursor.getString(2));
                message.setContent(cursor.getString(3));
                message.setDate(cursor.getString(4));
                Log.d("messagelist", message.getContent());

                messageList.add(message);
            } while (cursor.moveToNext());
        }

        return messageList;
    }

    public List<Whitelist> getAllWhitelist(){
        List<Whitelist> whitelists = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_WHITELIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Whitelist whitelist = new Whitelist();
                whitelist.setNumber(cursor.getString(1));;

                whitelists.add(whitelist);
            } while (cursor.moveToNext());
        }

        return whitelists;
    }

    public void deleteWhitelist(Whitelist data) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WHITELIST, KEY_WHITELIST_NUM + " = ?",
                new String[] { String.valueOf(data.getNumber()) });
        db.close();
        //Log.d(TAG, "Data with nama="+data.getNama()+" deleted");
    }

    public boolean isSmsSynced(Sms sms){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_SMS + " WHERE " + KEY_SENDER + " = " + String.valueOf(sms.address) + " AND " + KEY_BODY + " = \"" + String.valueOf(sms.body) + "\"", null);
        if (c.moveToFirst()){
            return true;
        } else {
            return false;
        }
    }
//
//    public void addData(Data data) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAMA, data.getNama());
//        values.put(KEY_ALAMAT, data.getAlamat());
//        values.put(KEY_KELAMIN, data.getJenisKelamin());
//        values.put(KEY_TGL_LAHIR, data.getTanggalLahir());
//
//        // Inserting Row
//        db.insert(TABLE_DATA, null, values);
//        db.close(); // Closing database connection
//
//
//        Log.d(TAG, "Data with nama="+data.getNama()+" added");
//    }
//
//    public List<Data> getAllData() {
//        List<Data> dataList = new ArrayList<Data>();
//
//        String selectQuery = "SELECT  * FROM " + TABLE_DATA;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                Data data = new Data();
//                data.setId(Integer.parseInt(cursor.getString(0)));
//                data.setNama(cursor.getString(1));
//                data.setAlamat(cursor.getString(2));
//                data.setJenisKelamin(cursor.getString(3));
//                data.setTanggalLahir(cursor.getString(4));
//
//                dataList.add(data);
//            } while (cursor.moveToNext());
//        }
//        Log.d(TAG, "Select all:");
//        for (Data data: dataList) {
//            Log.d(TAG, "Data with nama="+data.getNama()+", JenisKelamin="+ data.getJenisKelamin() +" selected");
//        }
//
//        return dataList;
//    }
//    public void deleteData(Data data) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_DATA, KEY_NAMA + " = ?",
//                new String[] { String.valueOf(data.getNama()) });
//        db.close();
//        Log.d(TAG, "Data with nama="+data.getNama()+" deleted");
//    }
//
//    public void updateData(Data data){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAMA, data.getNama());
//        values.put(KEY_ALAMAT, data.getAlamat());
//        values.put(KEY_KELAMIN, data.getJenisKelamin());
//        values.put(KEY_TGL_LAHIR, data.getTanggalLahir());
//
//        db.update(TABLE_DATA, values, KEY_NAMA + " = ?",
//                new String[] { String.valueOf(data.getNama()) });
//        Log.d(TAG, "Data with nama="+data.getNama()+" updated");
//    }
//
//    public void deleteAll() {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        db.execSQL("DELETE FROM "+TABLE_DATA);
//        db.close();
//        Log.d(TAG, "All data deleted");
//    }
}