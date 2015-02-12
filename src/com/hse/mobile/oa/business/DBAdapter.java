package com.hse.mobile.oa.business;
import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.SQLException;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  
import android.util.Log;  
  
public class DBAdapter {  
  
    static final String KEY_ROWID = "_id";  
    static final String KEY_NAME = "name";  
    static final String KEY_EMAIL = "email";  
    static final String KEY_NEWSTITLE="newstitle";
    static final String KEY_NEWSSUMMARY="newssummary";
    static final String TAG = "DBAdapter";  
      
    static final String DATABASE_NAME = "MyDB";  
    static final String DATABASE_TABLE = "contacts";  
    static final int DATABASE_VERSION = 1;  
      
    static final String DATABASE_CREATE =   
            "create table contacts( _id integer primary key autoincrement, " +   
            "name text not null, email text not null,newstitle text not null, newssummary text not null);";  
    final Context context;  
      
    DatabaseHelper DBHelper;  
    SQLiteDatabase db;  
      
    public DBAdapter(Context cxt)  
    {  
        this.context = cxt;  
        DBHelper = new DatabaseHelper(context);  
    }  
      
    private static class DatabaseHelper extends SQLiteOpenHelper  
    {  
  
        DatabaseHelper(Context context)  
        {  
            super(context, DATABASE_NAME, null, DATABASE_VERSION);  
        }  
        @Override  
        public void onCreate(SQLiteDatabase db) {  
            // TODO Auto-generated method stub  
            try  
            {  
                db.execSQL(DATABASE_CREATE);  
            }  
            catch(SQLException e)  
            {  
                e.printStackTrace();  
            }  
        }  
  
        @Override  
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
            // TODO Auto-generated method stub  
            Log.wtf(TAG, "Upgrading database from version "+ oldVersion + "to "+  
             newVersion + ", which will destroy all old data");  
            db.execSQL("DROP TABLE IF EXISTS contacts");  
            onCreate(db);  
        }  
    }  
    //查询是否有重复
    public Boolean hasthenewsid(String theid){
    	Cursor c= db.query(DATABASE_TABLE, new String[]{ KEY_ROWID,KEY_NAME,KEY_EMAIL,KEY_NEWSTITLE,KEY_NEWSSUMMARY}, "name=?", new String[]{theid}, null, null, null);
    	if(c.moveToFirst()){
    		Log.i("test", ""+c.getString(0)+"  "+c.getString(1)+"   "+c.getString(2)+"  "+c.getString(3)+"  "+c.getString(4) );
    		return true;
    	}
    	else {
    		Log.i("test", "没有这条数据");
    		return false;
    	}
    } 
    //open the database  
    public DBAdapter open() throws SQLException  
    {  
        db = DBHelper.getWritableDatabase();  
        return this;  
    }  
    //close the database  
    public void close()  
    {  
        DBHelper.close();  
    }  
      
    //insert a contact into the database  
    public long insertContact(String name, String email,String newstitle,String newssummary)  
    {  
        ContentValues initialValues = new ContentValues();  
        initialValues.put(KEY_NAME, name);  
        initialValues.put(KEY_EMAIL, email);  
        initialValues.put(KEY_NEWSTITLE, newstitle);
        initialValues.put(KEY_NEWSSUMMARY, newssummary);
        return db.insert(DATABASE_TABLE, null, initialValues);  
    }  
    //delete a particular contact  
    public boolean deleteContact(long rowId)  
    {  
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" +rowId, null) > 0;  
    }  
    //retreves all the contacts  
    public Cursor getAllContacts()  
    {  
        return db.query(DATABASE_TABLE, new String[]{ KEY_ROWID,KEY_NAME,KEY_EMAIL,KEY_NEWSTITLE,KEY_NEWSSUMMARY}, null, null, null, null, null);  
    }  
    //retreves a particular contact  
    public Cursor getContact(long rowId) throws SQLException  
    {  
        Cursor mCursor =   
                db.query(true, DATABASE_TABLE, new String[]{ KEY_ROWID,  
                         KEY_NAME, KEY_EMAIL,KEY_NEWSTITLE,KEY_NEWSSUMMARY}, KEY_ROWID + "=" + rowId, null, null, null, null, null);  
        if (mCursor != null)  
            mCursor.moveToFirst();  
        return mCursor;  
    }  
    //updates a contact  
    public boolean updateContact(long rowId, String name, String email,String newstitle,String newssummary)  
    {  
        ContentValues args = new ContentValues();  
        args.put(KEY_NAME, name);  
        args.put(KEY_EMAIL, email); 
        args.put(KEY_NEWSTITLE, newstitle);
        args.put(KEY_NEWSSUMMARY, newssummary);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" +rowId, null) > 0;  
    }  
}  