package com.servir.frostmapper2.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler sInstance;

    public static synchronized DatabaseHandler getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHandler(Constantori.DATABASE_Context.getApplicationContext());
        }
        return sInstance;
    }


    private DatabaseHandler(Context context) {
        super(context, Constantori.DATABASE_NAME, null, Constantori.DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_DAT_TABLE = "CREATE TABLE " + Constantori.TABLE_DAT + "("
                + Constantori.KEY_DATRID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constantori.KEY_DATNO + " VARCHAR,"
                + Constantori.KEY_DATFP + " VARCHAR,"
                + Constantori.KEY_DATEAA + " VARCHAR,"
                + Constantori.KEY_DATSEV + " VARCHAR,"
                + Constantori.KEY_DATCW + " VARCHAR,"
                + Constantori.KEY_DATCC + " VARCHAR,"
                + Constantori.KEY_DATWS + " VARCHAR,"
                + Constantori.KEY_LOCNO + " VARCHAR,"
                + Constantori.KEY_DATLRD + " VARCHAR,"
                + Constantori.KEY_DATFCR + " VARCHAR,"
                + Constantori.KEY_DATSTATUS + " VARCHAR,"
                + Constantori.KEY_DATPICNM + " VARCHAR,"
                + Constantori.KEY_DATCOM + " VARCHAR,"
                + Constantori.KEY_DATINDEX + " VARCHAR"
                + ")";
        db.execSQL(CREATE_DAT_TABLE);

        String CREATE_PIC_TABLE = "CREATE TABLE " + Constantori.TABLE_PIC + "("
                + Constantori.KEY_USERPIC + " TEXT PRIMARY KEY,"
                + Constantori.KEY_USERPICPATH + " VARCHAR,"
                + Constantori.KEY_SENDSTAT + " VARCHAR"
                + ")";
        db.execSQL(CREATE_PIC_TABLE);


        String CREATE_LOC_TABLE = "CREATE TABLE " + Constantori.TABLE_LOC + "("
                + Constantori.KEY_LOCRID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constantori.KEY_LOCNO + " VARCHAR,"
                + Constantori.KEY_LOCLOC + " VARCHAR,"
                + Constantori.KEY_LOCAAF + " VARCHAR,"
                + Constantori.KEY_LOCCPA + " VARCHAR,"
                + Constantori.KEY_LOCAO + " VARCHAR,"
                + Constantori.KEY_LOCATP + " VARCHAR,"
                + Constantori.KEY_LOCF + " VARCHAR,"
                + Constantori.KEY_LOCW + " VARCHAR,"
                + Constantori.KEY_LOCMTM + " VARCHAR,"
                + Constantori.KEY_LOCFFS + " VARCHAR,"
                + Constantori.KEY_LOCFFA + " VARCHAR,"
                + Constantori.KEY_LOCLAT + " VARCHAR,"
                + Constantori.KEY_LOCLON + " VARCHAR,"
                + Constantori.KEY_LOCINDEX + " VARCHAR,"
                + Constantori.KEY_LOCSTATUS + " VARCHAR"
                + ")";
        db.execSQL(CREATE_LOC_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Constantori.TABLE_DAT);
        db.execSQL("DROP TABLE IF EXISTS " + Constantori.TABLE_LOC);
        db.execSQL("DROP TABLE IF EXISTS " + Constantori.TABLE_PIC);

        // Create tables again
        onCreate(db);
    }


    public boolean CheckIsDataAlreadyInDBorNot(String TableName, String dbfield, String fieldValue) {
        SQLiteDatabase db = this.getInstance(ApplicationContextor.getAppContext()).getWritableDatabase();
        String sql =  "SELECT * FROM " + TableName + " WHERE " + dbfield + "=?";
        Cursor query = db.rawQuery(
                sql,
                new String[] {fieldValue}
        );

        if(query.getCount() <= 0){
            query.close();
            db.close();
            return false;
        }
        query.close();
        db.close();
        return true;
    }


    //UPDATES//////////////////////////////////////////////////////////////////////////////////////////////////////////
    //this UPDATESs for the optional fields of any collector table, later after the dorminant/manadatory data are INSERTED ONLY
    public void updateDataToTable(String TableName, String primaryKeyField, String primaryKeyValue, JSONArray storesArray) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransactionNonExclusive();

        try {


            for (int i = 0; i < storesArray.length(); i++) {

                JSONObject store = storesArray.getJSONObject(i);

                ContentValues values = new ContentValues();

                //we loop through each key and add its value - contrary to what the other functions do as they
                //repeat the updates with implicit variable/field name declaration
                //doing it implicitly

                Iterator<String> iter = store.keys();
                while (iter.hasNext()) {

                    String key = iter.next();
                    values.put(key, store.getString(key));

                }


                db.update(TableName, values, primaryKeyField + "=?", new String[]{primaryKeyValue});

            }

            db.setTransactionSuccessful();

        } catch(Exception xx){

            Log.e(Constantori.APP_ERROR_PREFIX + "_KolUpSQLGen",xx.getMessage(), xx);

        }finally{

            db.endTransaction();

        }
        //adb.close();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //INSERT


    //this INSERTs for the dorminant/manadatory fields of any collector table, later on the optional data is UPDATED ONLY

    public void insertDataToTable(String TableName, JSONArray storesArray) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransactionNonExclusive();

        try {


            for (int i = 0; i < storesArray.length(); i++) {

                JSONObject store = storesArray.getJSONObject(i);

                ContentValues values = new ContentValues();

                //we loop through each key and add its value - contrary to what the other functions do as they
                //repeat the inserts with implicit variable/field name declaration
                //doing it implicitly

                Iterator<String> iter = store.keys();
                while (iter.hasNext()) {

                    String key = iter.next();
                    values.put(key, store.getString(key));

                }


                db.insert(TableName, null, values);

            }

            db.setTransactionSuccessful();

        } catch(Exception xx){

            Log.e(Constantori.APP_ERROR_PREFIX + "_KolInSQLGen",xx.getMessage(), xx);

        }finally{

            db.endTransaction();

        }
        //adb.close();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public JSONArray PostDataArray_Alldata(String TableName, String FieldName, String Value)
    {

        SQLiteDatabase db = getWritableDatabase();

        JSONArray resultSet = new JSONArray();

        db.beginTransaction();

        try {


            String searchQuery = "";
            Cursor cursor = null;


            if (FieldName.equals("") && Value.equals("")) {
                searchQuery = "SELECT  * FROM " + TableName;
                cursor = db.rawQuery(searchQuery, null );

            }else{
                searchQuery = "SELECT  * FROM " + TableName + " WHERE " + FieldName + " = ? ";;
                cursor = db.rawQuery(
                        searchQuery,
                        new String[] {Value}
                );
            }


            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for( int i=0 ;  i< totalColumn ; i++ )
                {
                    if( cursor.getColumnName(i) != null )
                    {
                        try
                        {
                            if( cursor.getString(i) != null )
                            {
                                Log.e(Constantori.APP_ERROR_PREFIX+"_Post_i", cursor.getString(i) );
                                rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                            }
                            else
                            {
                                rowObject.put( cursor.getColumnName(i) ,  "" );
                            }
                        }
                        catch( Exception e )
                        {
                            Log.d(Constantori.APP_ERROR_PREFIX+"_Post_Error", "Post_"+TableName);
                        }
                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();
            }

            cursor.close();

            db.setTransactionSuccessful();

        } catch(Exception xx){

            Log.e(Constantori.APP_ERROR_PREFIX+"_PostR_ERROR", xx.getMessage());
            xx.printStackTrace();

        }finally{

            db.endTransaction();
            Log.e(Constantori.APP_ERROR_PREFIX+"_PostR_DONE", TableName + " : " +resultSet.toString() );
            return resultSet;

        }


    }


    public void changePostStatus(JSONArray storesArray, String TableName, String primaryKeyField, String statusField, String statusValue) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            for (int i = 0; i < storesArray.length(); i++) {

                JSONObject store = storesArray.getJSONObject(i);

                ContentValues values = new ContentValues();

                values.put(statusField, statusValue);

                String kref = store.getString(primaryKeyField);
                db.update(TableName, values, primaryKeyField + "=?", new String[]{kref});

            }

            db.setTransactionSuccessful();


        } catch(Exception xx){

            Log.e(Constantori.APP_ERROR_PREFIX+"_ChangePost_ERROR", xx.getMessage());
            xx.printStackTrace();

        }finally{
            Log.e(Constantori.APP_ERROR_PREFIX+"_ChangePost_DONE", TableName + " : " + storesArray.toString());
            db.endTransaction();

        }

        //adb.close();
    }


    //get all data values from any table
    public List<HashMap<String, String>> GetAllData(String TableName, String FieldName, String Value){

        SQLiteDatabase db = getWritableDatabase();
        List<HashMap<String, String>>  All_data = new ArrayList<HashMap<String, String>>();

        db.beginTransaction();
        try {

            String searchQuery = "";
            Cursor cursor = null;


            if (FieldName.equals("") && Value.equals("")) {
                searchQuery = "SELECT  * FROM " + TableName;
                cursor = db.rawQuery(searchQuery, null );

            }else{
                searchQuery = "SELECT  * FROM " + TableName + " WHERE " + FieldName + " = ? ";;
                cursor = db.rawQuery(
                        searchQuery,
                        new String[] {Value}
                );
            }



            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                int totalColumn = cursor.getColumnCount();
                HashMap<String, String> tempHash = new HashMap<String, String>();

                for( int i=0 ;  i< totalColumn ; i++ )
                {
                    if( cursor.getColumnName(i) != null )
                    {
                        try
                        {
                            if( cursor.getString(i) != null )
                            {
                                //Log.e(Constantori.APP_ERROR_PREFIX + "_alldataSQL", cursor.getString(i) );
                                tempHash.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                            }
                            else
                            {
                                tempHash.put(cursor.getColumnName(i) ,  "" );
                            }
                        }
                        catch( Exception e )
                        {
                            Log.d(Constantori.APP_ERROR_PREFIX + "_alldataerror", "Retrieve_"+TableName);
                        }
                    }
                }
                All_data.add(tempHash);
                cursor.moveToNext();
            }
            cursor.close();
            db.setTransactionSuccessful();
        } catch(Exception xx){
            Log.e(Constantori.APP_ERROR_PREFIX + "_AllData_ERROR", xx.getMessage());
            xx.printStackTrace();
        }finally{
            db.endTransaction();
            //Log.e(Constantori.APP_ERROR_PREFIX+"_AllData_DONE", TableName + " : " + All_data.toString());
            return All_data;
        }
    }


    public void resetTable(String TableName, String FieldName, String Value) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            if (FieldName.equals("") && Value.equals("")) {
                db.delete(TableName, null, null);
            }else{
                db.delete(TableName, FieldName + "=?", new String[]{Value});
            }

            db.setTransactionSuccessful();
        } catch(Exception xx){
            Log.e(Constantori.APP_ERROR_PREFIX + "_DelTBL_ERROR", xx.getMessage());
            xx.printStackTrace();
        }finally{
            db.endTransaction();
            Log.e(Constantori.APP_ERROR_PREFIX + "_DelTBL_DONE", TableName);
        }
    }


    public int getRowCount(String TableName, String FieldName, String Value) {

        SQLiteDatabase db = getWritableDatabase();


        String searchQuery = "";
        Cursor cursor = null;


        if (FieldName.equals("") && Value.equals("")) {
            searchQuery = "SELECT  * FROM " + TableName;
            cursor = db.rawQuery(searchQuery, null );

        }else{
            searchQuery = "SELECT  * FROM " + TableName + " WHERE " + FieldName + " = ? ";;
            cursor = db.rawQuery(
                    searchQuery,
                    new String[] {Value}
            );
        }



        int rowCount = 0;

        db.beginTransaction();

        try {

            rowCount = cursor.getCount();
            cursor.close();
            db.setTransactionSuccessful();
        } catch(Exception xx){
            Log.e(Constantori.APP_ERROR_PREFIX+"_ROWcnt_ERROR", xx.getMessage());
            xx.printStackTrace();
        }finally{
            db.endTransaction();
            Log.e(Constantori.APP_ERROR_PREFIX+"_ROWcnt_DONE", TableName + " : " + String.valueOf(rowCount));
            return rowCount;
        }

    }

}