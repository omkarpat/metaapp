package com.pict.metaappui.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pict.metaappui.modal.FileItem;
import com.pict.metaappui.modal.LogItem;
import com.pict.metaappui.modal.SellerRank;
import com.pict.metaappui.modal.UserRequest;
import com.pict.metaappui.modal.UserResponses;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar on 23/12/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MetaAppDb";

    // Table Names
    private static final String TABLE_USERREQUEST = "UserRequest";
    private static final String TABLE_USERRESPONSES = "UserResponses";
    private static final String TABLE_USERFILES = "UserFiles";
    private static final String TABLE_USERCONTACTS = "UserContacts";
    private static final String TABLE_SELLERRANK = "SellerRank";
    private static final String TABLE_LOGS = "Logs";


    //Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_REQUESTID = "RequestId";
    private static final String KEY_TOPIC = "Topic";

    //UserRequest column names
    private static final String KEY_INTENTDESC = "Intent_Desc";
    private static final String KEY_DEADLINE = "Deadline";
    private static final String KEY_PENDING = "Pending";

    //UserResponses column names
    private static final String KEY_SERVICEDESC = "Service_Desc";
    private static final String KEY_COST = "Cost";
    private static final String KEY_TIMETOCOMPLETE = "TTC";
    private static final String KEY_SELLER = "seller";

    //UserFiles column names
    private static final String KEY_NAME = "Name";
    private static final String KEY_LOCATION = "Location";
    private static final String KEY_FILETYPE = "FileType";
    // Photos = 1
    // Videos = 2
    // Text = 3

    //UserContacts column names
    private static final String KEY_CNAME = "CName";
    private static final String KEY_CNUMBER = "CNumber";

    //SellerRank column names
    private static final String KEY_RANK = "Rank";
    private static final String KEY_RATING = "Rating";

    //Logs Column names
    private static final String KEY_LOG = "Log";
    private static final String KEY_TIMESTAMP = "Timestamp";

    // Table Create Statements
    // No boolean datatype in sqlite so use integer with 0 as false and 1 as true
    private static final String CREATE_TABLE_USERREQUEST = "CREATE TABLE "
            + TABLE_USERREQUEST + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_REQUESTID
            + " INTEGER UNIQUE," + KEY_TOPIC + " TEXT," + KEY_INTENTDESC + " TEXT,"
            + KEY_DEADLINE + " DATETIME," + KEY_PENDING + " INTEGER" + ")";

    private static final String CREATE_TABLE_USERRESPONSES = "CREATE TABLE "
            + TABLE_USERRESPONSES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_REQUESTID
            + " INTEGER ," + KEY_TOPIC + " TEXT," + KEY_SERVICEDESC + " TEXT,"
            + KEY_TIMETOCOMPLETE + " DATETIME," + KEY_COST + " REAL," + KEY_SELLER + " INTEGER" + ")";

    private static final String CREATE_TABLE_USERFILES = "CREATE TABLE "
            + TABLE_USERFILES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT ," + KEY_LOCATION + " TEXT UNIQUE," +KEY_FILETYPE + " INTEGER" + ")";

    private static final String CREATE_TABLE_USERCONTACTS = "CREATE TABLE "
            + TABLE_USERCONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CNAME
            + " TEXT UNIQUE," + KEY_CNUMBER + " TEXT" + ")";

    private static final String CREATE_TABLE_SELLERRANK = "CREATE TABLE "
            + TABLE_SELLERRANK + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT UNIQUE," + KEY_RANK + " REAL," + KEY_RATING + " INTEGER"+ ")";

    private static final String CREATE_TABLE_LOGS = "CREATE TABLE "
            + TABLE_LOGS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT ,"+ KEY_LOG+ " TEXT ," + KEY_TIMESTAMP + " DATETIME" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERREQUEST);
        db.execSQL(CREATE_TABLE_USERRESPONSES);
        db.execSQL(CREATE_TABLE_USERFILES);
        db.execSQL(CREATE_TABLE_USERCONTACTS);
        db.execSQL(CREATE_TABLE_SELLERRANK);
        db.execSQL(CREATE_TABLE_LOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERREQUEST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERREQUEST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERCONTACTS);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SELLERRANK);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LOGS);
        onCreate(db);

    }

    public long createUserRequest(UserRequest obj){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_REQUESTID, obj.getRequestId());
        values.put(KEY_TOPIC, obj.getTopic());
        values.put(KEY_INTENTDESC, obj.getIntent_desc());
        values.put(KEY_DEADLINE, obj.getDeadline_date() + " " + obj.getDeadline_time());
        if(obj.isPending())
            values.put(KEY_PENDING, 1);
        else
            values.put(KEY_PENDING, 0);
        long id=db.insert(TABLE_USERREQUEST,null,values);
        return id;
    }

    public UserRequest getUserRequest(int requestId){
        SQLiteDatabase db=this.getReadableDatabase();

        String select_query="SELECT * FROM "+TABLE_USERREQUEST+" WHERE "+KEY_REQUESTID+" = "+requestId;
        Log.i(LOG, select_query);

        Cursor c=db.rawQuery(select_query, null);
        if(c!=null)
            c.moveToFirst();

        UserRequest obj=new UserRequest();
        obj.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        obj.setRequestId(c.getInt(c.getColumnIndex(KEY_REQUESTID)));
        obj.setTopic(c.getString(c.getColumnIndex(KEY_TOPIC)));
        obj.setIntent_desc(c.getString(c.getColumnIndex(KEY_INTENTDESC)));
        obj.setDeadline_date(c.getString(c.getColumnIndex(KEY_DEADLINE)).split(" ")[0]);
        obj.setDeadline_time(c.getString(c.getColumnIndex(KEY_DEADLINE)).split(" ")[1]);
        if(c.getInt(c.getColumnIndex(KEY_PENDING))==1)
            obj.setPending(true);
        else
            obj.setPending(false);

        return obj;

    }

    public List<UserRequest> getAllUserRequests(){
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_USERREQUEST;
        Log.i(LOG, select_query);

        List<UserRequest> list=new ArrayList<UserRequest>();
        Cursor c=db.rawQuery(select_query, null);

        if(c.moveToFirst()) {
            do {
                UserRequest obj = new UserRequest();
                obj.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                obj.setRequestId(c.getInt(c.getColumnIndex(KEY_REQUESTID)));
                obj.setTopic(c.getString(c.getColumnIndex(KEY_TOPIC)));
                obj.setIntent_desc(c.getString(c.getColumnIndex(KEY_INTENTDESC)));
                obj.setDeadline_date(c.getString(c.getColumnIndex(KEY_DEADLINE)).split(" ")[0]);
                obj.setDeadline_time(c.getString(c.getColumnIndex(KEY_DEADLINE)).split(" ")[1]);
                if (c.getInt(c.getColumnIndex(KEY_PENDING)) == 1)
                    obj.setPending(true);
                else
                    obj.setPending(false);
                list.add(obj);
            }while (c.moveToNext());
        }
        return list;
    }

    public long createUserResponses(UserResponses obj){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_REQUESTID, obj.getRequestId());
        values.put(KEY_TOPIC,obj.getTopic());
        values.put(KEY_SERVICEDESC,obj.getService_desc());
        values.put(KEY_COST, obj.getCost());
        values.put(KEY_TIMETOCOMPLETE, obj.getTime_to_complete());
        values.put(KEY_SELLER, obj.getSeller());
        long id=db.insert(TABLE_USERRESPONSES, null, values);
        return id;
    }

    public List<UserResponses> getUserResponses(int requestId){
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_USERRESPONSES+" WHERE "+KEY_REQUESTID+" = "+requestId;
        Log.i(LOG, select_query);

        List<UserResponses> list=new ArrayList<UserResponses>();
        Cursor c=db.rawQuery(select_query, null);

        if(c.moveToFirst()) {
            do {
                UserResponses obj = new UserResponses();
                obj.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                obj.setRequestId(c.getInt(c.getColumnIndex(KEY_REQUESTID)));
                obj.setTopic(c.getString(c.getColumnIndex(KEY_TOPIC)));
                obj.setService_desc(c.getString(c.getColumnIndex(KEY_SERVICEDESC)));
                obj.setTime_to_complete(c.getString(c.getColumnIndex(KEY_TIMETOCOMPLETE)));
                obj.setCost(c.getFloat(c.getColumnIndex(KEY_COST)));
                obj.setSeller(c.getString(c.getColumnIndex(KEY_SELLER)));
                list.add(obj);
            }while (c.moveToNext());
        }
        return list;
    }

    public int updatePendingRow(int requestId){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_PENDING, 0);
        int noofrowsaffected=db.update(TABLE_USERREQUEST, values, KEY_REQUESTID + "=" + requestId, null);
        return noofrowsaffected;
    }

    public long createUserFiles(FileItem obj,String type){
        int fileType = getFileType(type);
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_NAME,obj.getName());
        values.put(KEY_LOCATION, obj.getLocation());
        values.put(KEY_FILETYPE, fileType);
        long id=db.insert(TABLE_USERFILES, null, values);
        return id;
    }

    public int getFileType(String type){
        if(type.equals("Photo"))
            return 1;
        else if(type.equals("Video"))
            return 2;
        else if(type.equals("Text Files"))
            return 3;
        else
            return -1;
    }

    public List<FileItem> getAllUserFiles(String type){
        int fileType = getFileType(type);
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_USERFILES+" WHERE "+KEY_FILETYPE+" = "+fileType;
        Log.i(LOG, select_query);

        List<FileItem> list=new ArrayList<FileItem>();
        Cursor c=db.rawQuery(select_query, null);

        if(c.moveToFirst()) {
            do {
                FileItem obj = new FileItem();
                obj.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                obj.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
                obj.setIsChecked(false);
                list.add(obj);
            }while (c.moveToNext());
        }
        return list;
    }

    public boolean deleteFile(String location){
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_USERFILES, KEY_LOCATION + " = ?", new String[]{location});
        if(count==1)
            return true;
        return false;
    }

    public long createUserContact(FileItem obj){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_CNAME,obj.getName());
        values.put(KEY_CNUMBER, obj.getLocation());
        long id=db.insert(TABLE_USERCONTACTS, null, values);
        return id;
    }

    public List<FileItem> getAllUserContacts(){
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_USERCONTACTS;
        Log.i(LOG, select_query);

        List<FileItem> list=new ArrayList<FileItem>();
        Cursor c=db.rawQuery(select_query, null);

        if(c.moveToFirst()) {
            do {
                FileItem obj = new FileItem();
                obj.setName(c.getString(c.getColumnIndex(KEY_CNAME)));
                obj.setLocation(c.getString(c.getColumnIndex(KEY_CNUMBER)));
                obj.setIsChecked(false);
                list.add(obj);
            }while (c.moveToNext());
        }
        return list;
    }

    public boolean deleteContact(String cName){
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_USERCONTACTS, KEY_CNAME + " = ?", new String[]{cName});
        if(count==1)
            return true;
        return false;
    }

    public long createSeller(SellerRank obj){
        SQLiteDatabase db=this.getWritableDatabase();
        String select_query="SELECT * FROM "+TABLE_SELLERRANK;
        Log.e(LOG,select_query);
        Cursor c=db.rawQuery(select_query, null);
        boolean exist = false;
        if(c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                if(name.equals(obj.getName())){
                    exist = true;
                    break;
                }
            }while (c.moveToNext());
        }
        if(exist){
            ContentValues values = new ContentValues();
            values.put(KEY_RANK, obj.getRank());
            values.put(KEY_RATING, obj.getRating());
            int id = db.update(TABLE_SELLERRANK,values,KEY_NAME+ "=?",new String[]{obj.getName()} );
            return id;
        }
        else{
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, obj.getName());
            values.put(KEY_RANK, obj.getRank());
            values.put(KEY_RATING, obj.getRating());
            long id = db.insert(TABLE_SELLERRANK,null,values);
            return id;
        }
    }



    public double getSellerRank(String name){
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_SELLERRANK;
        Log.e(LOG,select_query);
        Cursor c=db.rawQuery(select_query, null);
        if(c.moveToFirst()) {
            do {
                String nname = c.getString(c.getColumnIndex(KEY_NAME));
                if(nname.equals(name)){
                    double rank = c.getDouble(c.getColumnIndex(KEY_RANK));
                    return rank;
                }
            }while (c.moveToNext());
        }
        return -1;
    }

    public int getSellerRating(String name){
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_SELLERRANK;
        Log.e(LOG,select_query);
        Cursor c=db.rawQuery(select_query, null);
        if(c.moveToFirst()) {
            do {
                String nname = c.getString(c.getColumnIndex(KEY_NAME));
                if(nname.equals(name)){
                    int rating = c.getInt(c.getColumnIndex(KEY_RATING));
                    return rating;
                }
            }while (c.moveToNext());
        }
        return -1;
    }

    public String[] getSellers(){
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_SELLERRANK;
        Log.e(LOG,select_query);
        List<String> sellers=new ArrayList<>();
        Cursor c=db.rawQuery(select_query, null);
        if(c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                sellers.add(name);
            }while (c.moveToNext());
        }
        String[] s=new String[sellers.size()];
        return sellers.toArray(s);
    }

    public long createLog(LogItem obj){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_NAME, obj.getName());
        values.put(KEY_LOG, obj.getLog());
        values.put(KEY_TIMESTAMP, obj.getTimestamp());
        long id=db.insert(TABLE_LOGS, null, values);
        return id;
    }

    public List<LogItem> getLogs(){
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_LOGS;
        Log.i(LOG, select_query);

        List<LogItem> list=new ArrayList<LogItem>();
        Cursor c=db.rawQuery(select_query, null);

        if(c.moveToFirst()) {
            do {
                LogItem obj = new LogItem();
                obj.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                obj.setLog(c.getString(c.getColumnIndex(KEY_LOG)));
                obj.setTimestamp(c.getString(c.getColumnIndex(KEY_TIMESTAMP)));
                list.add(obj);
            }while (c.moveToNext());
        }
        return list;
    }


    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}

