package com.example.pethoalpar.zxingexample;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CalendarContract;
import android.util.Log;
import android.util.SparseArray;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OfflineDatabase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME ="odseas-qr";

    // Course Table & Columns Names
    private static final String TABLE_COURSE ="course";
    private static final String COURSE_ID = "course_id";
    private static final String COURSE_NAME = "course_name";
    private static final String COURSE_CREDIT_HOUR = "course_credit_hour";
    private static final String EXAM_DATE = "exam_date";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String STATUS = "status";
    private static final String STUDENT_NUMBER = "student_number";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    // Course_handler Table & Columns Names
    private static final String TABLE_COURSE_HANDLER ="course_handler";
    private static final String COURSE_HANDLER_ID = "course_handler_id";
    private static final String STAFF_ID = "staff_id";
    private static final String INVIGILATOR_POSITION = "invigilator_position";

    // Enroll_handler Table & Columns Names
    private static final String TABLE_ENROLL_HANDLER ="enroll_handler";
    private static final String ENROLL_HANDLER_ID = "enroll_handler_id";
    private static final String STUDENT_ID = "student_id";
    private static final String ISCHECKED = "ischecked";
    private static final String CHECKIN_TIME = "checkin_time";
    private static final String CHECKOUT_TIME = "checkout_time";

    // Staff Table & Columns Names
    private static final String TABLE_STAFF ="staff";
    private static final String STAFF_NAME = "staff_name";
    private static final String STAFF_PASSWORD = "staff_password";
    private static final String STAFF_EMAIL = "staff_email";
    private static final String STAFF_PHONENO= "staff_phoneno";
    private static final String ROLE = "role";

    // Student Table & Columns Names
    private static final String TABLE_STUDENT ="student";
    private static final String STUDENT_NAME = "student_name";
    private static final String STUDENT_FACULTY = "student_faculty";
    private static final String STUDENT_MAJOR= "student_major";

    // Venue Table & Columns Names
    private static final String TABLE_VENUE ="venue";
    private static final String VENUE_ID = "venue_id";
    private static final String VENUE_NAME = "venue_name";
    private static final String VENUE_CAPACITY = "venue_capacity";

    // Venue_handler Table & Columns Names
    private static final String TABLE_VENUE_HANDLER ="venue_handler";
    private static final String VENUE_HANDLER_ID = "venue_handler_id";

    Context context;

    public OfflineDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Database
    @Override
    public void onCreate(SQLiteDatabase odseasqr) {
        String CREATE_COURSE_TABLE = "CREATE TABLE " + TABLE_COURSE + "("
                + COURSE_ID + " VARCHAR PRIMARY KEY,"
                + COURSE_NAME + " VARCHAR,"
                + COURSE_CREDIT_HOUR + " INTEGER,"
                + EXAM_DATE + " DATE,"
                + START_TIME + " VARCHAR,"
                + END_TIME + " VARCHAR,"
                + STATUS + " BOOLEAN,"
                + STUDENT_NUMBER + " INTEGER,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_COURSE_HANDLER_TABLE = "CREATE TABLE " + TABLE_COURSE_HANDLER + "("
                + COURSE_HANDLER_ID + " INTEGER PRIMARY KEY,"
                + STAFF_ID + " INTEGER,"
                + COURSE_ID + " VARCHAR,"
                + INVIGILATOR_POSITION + " VARCHAR,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_ENROLL_HANDLER_TABLE = "CREATE TABLE " + TABLE_ENROLL_HANDLER + "("
                + ENROLL_HANDLER_ID + " INTEGER PRIMARY KEY,"
                + STUDENT_ID + " INTEGER,"
                + COURSE_ID + " VARCHAR,"
                + ISCHECKED + " BOOLEAN,"
                + CHECKIN_TIME + " DATETIME,"
                + CHECKOUT_TIME + " DATETIME,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_STAFF_TABLE = "CREATE TABLE " + TABLE_STAFF + "("
                + STAFF_ID + " INTEGER PRIMARY KEY,"
                + STAFF_NAME + " VARCHAR,"
                + STAFF_PASSWORD + " VARCHAR,"
                + STAFF_EMAIL + " VARCHAR,"
                + STAFF_PHONENO + " INTEGER,"
                + ROLE + " VARCHAR,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENT + "("
                + STUDENT_ID + " INTEGER PRIMARY KEY,"
                + STUDENT_NAME + " VARCHAR,"
                + STUDENT_FACULTY + " VARCHAR,"
                + STUDENT_MAJOR + " VARCHAR,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_VENUE_TABLE = "CREATE TABLE " + TABLE_VENUE + "("
                + VENUE_ID + " VARCHAR PRIMARY KEY,"
                + VENUE_NAME + " VARCHAR,"
                + VENUE_CAPACITY + " INTEGER,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_VENUE_HANDLER_TABLE = "CREATE TABLE " + TABLE_VENUE_HANDLER + "("
                + VENUE_HANDLER_ID + " INTEGER PRIMARY KEY,"
                + VENUE_ID + " VARCHAR,"
                + COURSE_ID + " VARCHAR,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        // Creating requires tables
        odseasqr.execSQL(CREATE_COURSE_TABLE);
        odseasqr.execSQL(CREATE_COURSE_HANDLER_TABLE);
        odseasqr.execSQL(CREATE_ENROLL_HANDLER_TABLE);
        odseasqr.execSQL(CREATE_STAFF_TABLE);
        odseasqr.execSQL(CREATE_STUDENT_TABLE);
        odseasqr.execSQL(CREATE_VENUE_TABLE);
        odseasqr.execSQL(CREATE_VENUE_HANDLER_TABLE);
    }

    // Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase odseasqr, int oldVersion, int newVersion) {
        odseasqr.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        odseasqr.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_HANDLER);
        odseasqr.execSQL("DROP TABLE IF EXISTS " + TABLE_ENROLL_HANDLER);
        odseasqr.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
        odseasqr.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        odseasqr.execSQL("DROP TABLE IF EXISTS " + TABLE_VENUE);
        odseasqr.execSQL("DROP TABLE IF EXISTS " + TABLE_VENUE_HANDLER);
        onCreate(odseasqr);
    }

    public void insertCourseData(String data){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSE, null,null);
        db.beginTransaction();

        try {
            JSONArray jsonArray = new JSONArray(data);

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(COURSE_ID, result.getString("course_id"));
                values.put(COURSE_NAME, result.getString("course_name"));
                values.put(COURSE_CREDIT_HOUR, result.getString("course_credit_hour"));
                values.put(EXAM_DATE, result.getString("exam_date"));
                values.put(START_TIME, result.getString("start_time"));
                values.put(END_TIME, result.getString("end_time"));
                values.put(STATUS, result.getString("status"));
                values.put(STUDENT_NUMBER, result.getString("student_number"));
                values.put(CREATED_DATE, result.getString("created_date"));
                values.put(UPDATED_DATE, result.getString("updated_date"));

                // insert row
                long tag_id = db.insert(TABLE_COURSE, null, values);
                Log.d("course id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
            Toast.makeText(context, "Successfully added", Toast.LENGTH_SHORT).show();
            } catch (JSONException error){
                Log.d("error", error.toString());
            } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertCourseHandler(String data){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSE_HANDLER, null,null);
        db.beginTransaction();

        try {
            JSONArray jsonArray = new JSONArray(data);

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(STAFF_ID, result.getInt("staff_id"));
                values.put(COURSE_ID, result.getString("course_id"));
                values.put(INVIGILATOR_POSITION, result.getString("invigilator_position"));
                values.put(CREATED_DATE, result.getString("created_date"));
                values.put(UPDATED_DATE, result.getString("updated_date"));
                // insert row
                long tag_id = db.insert(TABLE_COURSE_HANDLER, null, values);
                Log.d("course handler id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException error){
            Log.d("error", error.toString());
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertEnrollHandler(String data){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENROLL_HANDLER, null,null);
        db.beginTransaction();

        try {
            JSONArray jsonArray = new JSONArray(data);

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(ENROLL_HANDLER_ID, result.getString("enroll_handler_id"));
                values.put(STUDENT_ID, result.getString("student_id"));
                values.put(COURSE_ID, result.getString("course_id"));
                values.put(ISCHECKED, result.getString("ischecked"));
                values.put(CHECKIN_TIME, result.getString("checkin_time"));
                values.put(CHECKOUT_TIME, result.getString("checkout_time"));
                values.put(CREATED_DATE, result.getString("created_date"));
                values.put(UPDATED_DATE, result.getString("updated_date"));
                // insert row
                long tag_id = db.insert(TABLE_ENROLL_HANDLER, null, values);
                Log.d("enroll_handler id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException error){
            Log.d("error", error.toString());
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertStaff(String data){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STAFF, null,null);
        db.beginTransaction();

        try {
            JSONArray jsonArray = new JSONArray(data);
            Log.d("data 1", data);

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(STAFF_ID, result.getString("staff_id"));
                values.put(STAFF_NAME, result.getString("staff_name"));
                values.put(STAFF_PASSWORD, result.getString("staff_password"));
                values.put(STAFF_EMAIL, result.getString("staff_email"));
                values.put(STAFF_PHONENO, result.getString("staff_phoneno"));
                values.put(ROLE, result.getString("role"));
                values.put(CREATED_DATE, result.getString("created_date"));
                values.put(UPDATED_DATE, result.getString("updated_date"));
                // insert row
                long tag_id = db.insert(TABLE_STAFF, null, values);
                Log.d("staff id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException error){
            Log.d("error", error.toString());
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertStudent(String data){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT, null,null);
        db.beginTransaction();

        try {
            JSONArray jsonArray = new JSONArray(data);

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(STUDENT_ID, result.getString("student_id"));
                values.put(STUDENT_NAME, result.getString("student_name"));
                values.put(STUDENT_FACULTY, result.getString("student_faculty"));
                values.put(STUDENT_MAJOR, result.getString("student_major"));
                values.put(CREATED_DATE, result.getString("created_date"));
                values.put(UPDATED_DATE, result.getString("updated_date"));
                // insert row
                long tag_id = db.insert(TABLE_STUDENT, null, values);
                Log.d("student id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException error){
            Log.d("error", error.toString());
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertVenue(String data){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VENUE, null,null);
        db.beginTransaction();

        try {
            JSONArray jsonArray = new JSONArray(data);

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(VENUE_ID, result.getString("venue_id"));
                values.put(VENUE_NAME, result.getString("venue_name"));
                values.put(VENUE_CAPACITY, result.getString("venue_capacity"));
                values.put(CREATED_DATE, result.getString("created_date"));
                values.put(UPDATED_DATE, result.getString("updated_date"));
                // insert row
                long tag_id = db.insert(TABLE_VENUE, null, values);
                Log.d("venue id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException error){
            Log.d("error", error.toString());
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertVenueHandler(String data){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VENUE_HANDLER, null,null);
        db.beginTransaction();

        try {
            JSONArray jsonArray = new JSONArray(data);

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(VENUE_HANDLER_ID, result.getString("venue_handler_id"));
                values.put(VENUE_ID, result.getString("venue_id"));
                values.put(COURSE_ID,result.getString("course_id"));
                values.put(CREATED_DATE, result.getString("created_date"));
                values.put(UPDATED_DATE, result.getString("updated_date"));
                // insert row
                long tag_id = db.insert(TABLE_VENUE_HANDLER, null, values);
                Log.d("venue handler id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException error){
            Log.d("error", error.toString());
        } finally {
            db.endTransaction();
        }
        db.close();
    }

//    public String checkAlreadyScan(int student_id, String course_id){
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        ArrayList<JSONObject> courseData = new ArrayList<>();
//
//        String SELECT_COURSE_DETAILS =
//                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = '%s'",
//                        TABLE_ENROLL_HANDLER,
//                        STUDENT_ID, student_id,
//                        COURSE_ID, course_id);
//
//        Log.e("Log", SELECT_COURSE_DETAILS);
//
//        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);
//
//        if (cursor.moveToFirst()) {
//            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));
//
//            do {
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put(STAFF_NAME, cursor.getString(0));
//                    jsonObject.put(INVIGILATOR_POSITION, cursor.getString(1));
//                    courseData.add(cursor.getPosition(),jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Log.d("Result1 == ", cursor.getString(0));
//                Log.d("Result2 == ", cursor.getString(1));
//                try {
//                    Log.d("Result3 == ", courseData.get(0).getString(STAFF_NAME));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } while (cursor.moveToNext());
//        } else Log.d("Result == ", "NO");
//
//        Log.d("Overall result", String.valueOf(courseData));
//
//        cursor.close();
//        db.close();
//
//        return String.valueOf(courseData);
//    }

    public String getSpinnerData(String staff_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();

        String SELECT_COURSE_DETAILS =
                String.format("SELECT * FROM %s INNER JOIN %s ON %s.%s = %s.%s WHERE %s.%s = %s",
                        TABLE_COURSE_HANDLER,
                        TABLE_COURSE,
                        TABLE_COURSE_HANDLER, COURSE_ID,
                        TABLE_COURSE, COURSE_ID,
                        TABLE_COURSE_HANDLER, STAFF_ID,
                        staff_id);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                String SELECT_VENUE = String.format("SELECT venue.venue_name FROM venue_handler JOIN venue ON venue_handler.venue_id = venue.venue_id WHERE venue_handler.course_id = '%s'", cursor.getString(2));
                Cursor cursor2 = db.rawQuery(SELECT_VENUE, null);
                if(cursor2.moveToNext()){
                    Log.d("Result venue", DatabaseUtils.dumpCursorToString(cursor2));
                    do{
                        try {
                            jsonObject.put(VENUE_NAME, cursor2.getString(0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } while(cursor2.moveToNext());
                }
                try {
                    jsonObject.put(COURSE_ID, cursor.getString(6));
                    jsonObject.put(COURSE_NAME, cursor.getString(7));
                    jsonObject.put(EXAM_DATE,cursor.getString(9));
                    jsonObject.put(START_TIME,cursor.getString(10));
                    jsonObject.put(END_TIME,cursor.getString(11));
                    jsonObject.put(STUDENT_NUMBER,cursor.getString(13));
                    courseData.add(cursor.getPosition(),jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Result1 == ", cursor.getString(0));
                Log.d("Result2 == ", cursor.getString(1));
                try {
                    Log.d("Result3 == ", courseData.get(0).getString(COURSE_ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(courseData));
        cursor.close();
        db.close();
        return String.valueOf(courseData);
    }

    public String getSubjectDetails(String course_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();

        String SELECT_COURSE_DETAILS =
                String.format("SELECT %s.%s,%s.%s FROM %s INNER JOIN %s ON %s.%s = %s.%s WHERE %s.%s = '%s' ORDER BY CASE %s WHEN 'CHIEF' THEN 1 WHEN 'INVIGILATOR' THEN 2 END, %s",
                        TABLE_STAFF,STAFF_NAME,
                        TABLE_COURSE_HANDLER, INVIGILATOR_POSITION,
                        TABLE_COURSE_HANDLER,
                        TABLE_STAFF,
                        TABLE_COURSE_HANDLER,STAFF_ID,
                        TABLE_STAFF,STAFF_ID,
                        TABLE_COURSE_HANDLER, COURSE_ID,course_id,
                        INVIGILATOR_POSITION, INVIGILATOR_POSITION);

        Log.d("query", SELECT_COURSE_DETAILS);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(STAFF_NAME, cursor.getString(0));
                    jsonObject.put(INVIGILATOR_POSITION, cursor.getString(1));
                    courseData.add(cursor.getPosition(),jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Result1 == ", cursor.getString(0));
                Log.d("Result2 == ", cursor.getString(1));
                try {
                    Log.d("Result3 == ", courseData.get(0).getString(STAFF_NAME));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(courseData));

        cursor.close();
        db.close();
        return String.valueOf(courseData);
    }

    public String checkAlreadyScan(String course_id,String student_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();

        String SELECT_COURSE_DETAILS =
                String.format("SELECT %s FROM %s WHERE %s = '%s' AND %s = '%s'",
                        ISCHECKED,
                        TABLE_ENROLL_HANDLER,
                        STUDENT_ID, student_id,
                        COURSE_ID,course_id);

        Log.d("query", SELECT_COURSE_DETAILS);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(ISCHECKED, cursor.getString(0));
                    courseData.add(cursor.getPosition(),jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Log.d("Result1 == ", cursor.getString(0));
                // Log.d("Result2 == ", cursor.getString(1));
                // try {
                //     Log.d("Result3 == ", courseData.get(0).getString(STAFF_NAME));
                // } catch (JSONException e) {
                //     e.printStackTrace();
                // }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(courseData));

        cursor.close();
        db.close();
        return String.valueOf(courseData);
    }

    public String getAbsenteesData(String course_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();

        String SELECT_COURSE_DETAILS =
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s IS NULL AND %s IS NULL ORDER BY %s ASC",
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        CHECKIN_TIME,CHECKOUT_TIME,
                        STUDENT_ID);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                String SELECT_STUDENT = String.format("SELECT student_name FROM student WHERE student_id = '%s'", cursor.getString(1));
                Cursor cursor2 = db.rawQuery(SELECT_STUDENT, null);
                if(cursor2.moveToNext()){
                    Log.d("Result student", DatabaseUtils.dumpCursorToString(cursor2));
                    do{
                        try {
                            jsonObject.put(STUDENT_NAME, cursor2.getString(0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } while(cursor2.moveToNext());
                }
                try {
                    jsonObject.put(STUDENT_ID, cursor.getString(1));
                    courseData.add(cursor.getPosition(),jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Log.d("Result1 == ", cursor.getString(0));
                // Log.d("Result2 == ", cursor.getString(1));
                // try {
                //     Log.d("Result3 == ", courseData.get(0).getString(STUDENT_ID));
                // } catch (JSONException e) {
                //     e.printStackTrace();
                // }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(courseData));

        cursor.close();
        db.close();
        return String.valueOf(courseData);
    }

    public String getAllData(String course_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();

        String SELECT_COURSE_DETAILS =
                String.format("SELECT * FROM %s WHERE %s = '%s' ORDER BY %s ASC",
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        STUDENT_ID);
        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);
        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                String SELECT_STUDENT = String.format("SELECT student_name FROM student WHERE student_id = '%s'", cursor.getString(1));
                Cursor cursor2 = db.rawQuery(SELECT_STUDENT, null);
                Log.d("Result cursor2--", DatabaseUtils.dumpCursorToString(cursor2));
                if(cursor2.moveToNext()){
                    Log.d("Result student", DatabaseUtils.dumpCursorToString(cursor2));
                    do{
                        try {
                            jsonObject.put(STUDENT_NAME, cursor2.getString(0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } while(cursor2.moveToNext());
                }
                try {
                    jsonObject.put(STUDENT_ID, cursor.getString(1));
                    courseData.add(cursor.getPosition(),jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");
        Log.d("Overall result", String.valueOf(courseData));
        cursor.close();
        db.close();
        return String.valueOf(courseData);
    }

    public String getAnswerBooklet(String course_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();
        int count = 0;

        String SELECT_COURSE_DETAILS =
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s IS NOT NULL",
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        CHECKOUT_TIME);

        Log.d("query", SELECT_COURSE_DETAILS);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));
            count = cursor.getCount();

//            do {
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("booklet", cursor.getString(0));
//                    courseData.add(cursor.getPosition(),jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                // Log.d("Result1 == ", cursor.getString(0));
//                // Log.d("Result2 == ", cursor.getString(1));
//                // try {
//                //     Log.d("Result3 == ", courseData.get(0).getString(STUDENT_ID));
//                // } catch (JSONException e) {
//                //     e.printStackTrace();
//                // }
//            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(count));

        cursor.close();
        db.close();
        return String.valueOf(count);
    }

    public String getAttendedData(String course_id){

        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;

        String SELECT_COURSE_DETAILS =
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s IS NOT NULL",
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        CHECKIN_TIME);

        Log.d("query", SELECT_COURSE_DETAILS);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));
            count = cursor.getCount();
        } else Log.d("Result == ", "NO");

        cursor.close();
        db.close();
        return String.valueOf(count);
    }

    public String getAttendeesData(String course_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();

        String SELECT_COURSE_DETAILS =
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s IS NOT NULL ORDER BY %s ASC",
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        CHECKIN_TIME,
                        STUDENT_ID);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                String SELECT_STUDENT = String.format("SELECT student_name FROM student WHERE student_id = '%s'", cursor.getString(1));
                Cursor cursor2 = db.rawQuery(SELECT_STUDENT, null);
                if(cursor2.moveToNext()){
                    Log.d("Result student", DatabaseUtils.dumpCursorToString(cursor2));
                    do{
                        try {
                            jsonObject.put(STUDENT_NAME, cursor2.getString(0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } while(cursor2.moveToNext());
                }
                try {
                    jsonObject.put(STUDENT_ID, cursor.getString(1));
                    courseData.add(cursor.getPosition(),jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(courseData));

        cursor.close();
        db.close();
        return String.valueOf(courseData);
    }

    public String getInExaminationData(String course_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();

        String SELECT_COURSE_DETAILS =
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s IS NOT NULL AND %s IS NULL ORDER BY %s ASC",
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        CHECKIN_TIME, CHECKOUT_TIME,
                        STUDENT_ID);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                String SELECT_STUDENT = String.format("SELECT student_name FROM student WHERE student_id = '%s'", cursor.getString(1));
                Cursor cursor2 = db.rawQuery(SELECT_STUDENT, null);
                if(cursor2.moveToNext()){
                    Log.d("Result student", DatabaseUtils.dumpCursorToString(cursor2));
                    do{
                        try {
                            jsonObject.put(STUDENT_NAME, cursor2.getString(0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } while(cursor2.moveToNext());
                }
                try {
                    jsonObject.put(STUDENT_ID, cursor.getString(1));
                    courseData.add(cursor.getPosition(),jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Log.d("Result1 == ", cursor.getString(0));
                // Log.d("Result2 == ", cursor.getString(1));
                // try {
                //     Log.d("Result3 == ", courseData.get(0).getString(STUDENT_ID));
                // } catch (JSONException e) {
                //     e.printStackTrace();
                // }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(courseData));

        cursor.close();
        db.close();
        return String.valueOf(courseData);
    }

    public String getStudentData(String student_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();

        String SELECT_STUDENT_DETAILS =
                String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        STUDENT_NAME,
                        TABLE_STUDENT,
                        STUDENT_ID, student_id);

        Log.d("query", SELECT_STUDENT_DETAILS);

        Cursor cursor = db.rawQuery(SELECT_STUDENT_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(STUDENT_NAME, cursor.getString(0));
                    courseData.add(cursor.getPosition(),jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Log.d("Result1 == ", cursor.getString(0));
                // Log.d("Result2 == ", cursor.getString(1));
                // try {
                //     Log.d("Result3 == ", courseData.get(0).getString(STAFF_NAME));
                // } catch (JSONException e) {
                //     e.printStackTrace();
                // }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(courseData));

        cursor.close();
        db.close();
        return String.valueOf(courseData);
    }

    public String getStudentSubject(String student_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();

        String SELECT_COURSE_DETAILS =
                String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        COURSE_ID,
                        TABLE_ENROLL_HANDLER,
                        STUDENT_ID, student_id);

        Log.d("query", SELECT_COURSE_DETAILS);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(COURSE_ID, cursor.getString(0));
                    courseData.add(cursor.getPosition(),jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Log.d("Result1 == ", cursor.getString(0));
                // Log.d("Result2 == ", cursor.getString(1));
                // try {
                //     Log.d("Result3 == ", courseData.get(0).getString(STAFF_NAME));
                // } catch (JSONException e) {
                //     e.printStackTrace();
                // }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(courseData));

        cursor.close();
        db.close();
        return String.valueOf(courseData);
    }

    public String getSubmittedData (String course_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> courseData = new ArrayList<>();

        String SELECT_COURSE_DETAILS =
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s IS NOT NULL ORDER BY %s ASC",
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        CHECKOUT_TIME,
                        STUDENT_ID);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));
            do {
                JSONObject jsonObject = new JSONObject();
                String SELECT_STUDENT = String.format("SELECT student_name FROM student WHERE student_id = '%s'", cursor.getString(1));
                Cursor cursor2 = db.rawQuery(SELECT_STUDENT, null);
                if(cursor2.moveToNext()){
                    Log.d("Result student", DatabaseUtils.dumpCursorToString(cursor2));
                    do{
                        try {
                            jsonObject.put(STUDENT_NAME, cursor2.getString(0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } while(cursor2.moveToNext());
                }
                try {
                    jsonObject.put(STUDENT_ID, cursor.getString(1));
                    courseData.add(cursor.getPosition(),jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(courseData));

        cursor.close();
        db.close();
        return String.valueOf(courseData);
    }

    public String updateAttendanceRecord(String student_id, String course_id){

        /*this method is  used to update the attendance in the enroll_handler table*/

        SQLiteDatabase db = this.getReadableDatabase();
        String isChecked = "";

        /*query to select the checkin time of specific student*/
        String CHECK_STUDENT =
                String.format("SELECT %s FROM %s WHERE %s = '%s' AND %s = '%s'",
                        CHECKIN_TIME,
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        STUDENT_ID, student_id);

        /*cursor to search in the database*/
        Cursor cursor = db.rawQuery(CHECK_STUDENT, null);

        if (cursor.moveToFirst()) {
            /*only will execute this "IF loop" if there is a matched result*/
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));

            do {
                /* since only one item will be selected therefore first index will be 0*/
                isChecked = cursor.getString(0);
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        if(isChecked.equals("null")){
            /* content values will store the data that want to update in its */
            ContentValues values = new ContentValues();
            values.put(CHECKIN_TIME, " time('now') ");
            /*db.update will update the value in the content values to the database*/
            db.update(TABLE_ENROLL_HANDLER,values, STUDENT_ID + "= ? AND "+ COURSE_ID + " = ?", new String[]{student_id, course_id});
            /*return success checkin if the checkin time is null*/
            return "success checkin";

        } else {
            ContentValues values_checkin = new ContentValues();
            values_checkin.put(CHECKOUT_TIME, " time('now') ");
            db.update(TABLE_ENROLL_HANDLER,values_checkin, STUDENT_ID + "= ? AND "+ COURSE_ID + " = ?", new String[]{student_id, course_id});

            ContentValues values_checkout = new ContentValues();
            values_checkout.put(ISCHECKED, 1);
            db.update(TABLE_ENROLL_HANDLER,values_checkout, STUDENT_ID + "= ? AND "+ COURSE_ID + " = ?", new String[]{student_id, course_id});
            /*return success checkout if the checkin time is not null this mean that the student already checkin before*/
            return "success checkout";
        }
    }
}
