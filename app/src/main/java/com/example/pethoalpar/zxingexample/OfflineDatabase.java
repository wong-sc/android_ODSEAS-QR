package com.example.pethoalpar.zxingexample;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

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
    private static final String STAFF_NAME = "student_id";
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

    public OfflineDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Database
    @Override
    public void onCreate(SQLiteDatabase odseasqr) {
        String CREATE_COURSE_TABLE = "CREATE TABLE" + TABLE_COURSE + "("
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

        String CREATE_COURSE_HANDLER_TABLE = "CREATE TABLE" + TABLE_COURSE_HANDLER + "("
                + COURSE_HANDLER_ID + " INTEGER PRIMARY KEY,"
                + STAFF_ID + " INTEGER,"
                + COURSE_ID + " VARCHAR,"
                + INVIGILATOR_POSITION + " VARCHAR,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_ENROLL_HANDLER_TABLE = "CREATE TABLE" + TABLE_ENROLL_HANDLER + "("
                + ENROLL_HANDLER_ID + " INTEGER PRIMARY KEY,"
                + STUDENT_ID + " INTEGER,"
                + COURSE_ID + " VARCHAR,"
                + ISCHECKED + " BOOLEAN,"
                + CHECKIN_TIME + " DATETIME,"
                + CHECKOUT_TIME + " DATETIME,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_STAFF_TABLE = "CREATE TABLE" + TABLE_STAFF + "("
                + STAFF_ID + " INTEGER PRIMARY KEY,"
                + STAFF_NAME + " VARCHAR,"
                + STAFF_PASSWORD + " VARCHAR,"
                + STAFF_EMAIL + " VARCHAR,"
                + STAFF_PHONENO + " INTEGER,"
                + ROLE + " VARCHAR,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_STUDENT_TABLE = "CREATE TABLE" + TABLE_STUDENT + "("
                + STUDENT_ID + " INTEGER PRIMARY KEY,"
                + STUDENT_NAME + " VARCHAR,"
                + STUDENT_FACULTY + " VARCHAR,"
                + STUDENT_MAJOR + " VARCHAR,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_VENUE_TABLE = "CREATE TABLE" + TABLE_VENUE + "("
                + VENUE_ID + " VARCHAR PRIMARY KEY,"
                + VENUE_NAME + " VARCHAR,"
                + VENUE_CAPACITY + " INTEGER,"
                + CREATED_DATE + " DATETIME,"
                + UPDATED_DATE + " DATETIME" + ")";

        String CREATE_VENUE_HANDLER_TABLE = "CREATE TABLE" + TABLE_VENUE_HANDLER + "("
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

        try {
            JSONObject jsonObject1 = new JSONObject(data);
            JSONArray jsonArray = jsonObject1.getJSONArray("result");

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
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
            }
            } catch (JSONException error){
                Log.d("error", error.toString());
            }
    }

    public void insertCourseHandler(String data){

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            JSONObject jsonObject1 = new JSONObject(data);
            JSONArray jsonArray = jsonObject1.getJSONArray("result");

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(STAFF_ID, result.getString("staff_id"));
                values.put(COURSE_ID, result.getString("course_id"));
                values.put(INVIGILATOR_POSITION, result.getString("invigilator_position"));
                values.put(CREATED_DATE, result.getString("created_date"));
                values.put(UPDATED_DATE, result.getString("updated_date"));
                // insert row
                long tag_id = db.insert(TABLE_COURSE_HANDLER, null, values);
                Log.d("status = ", tag_id + " ");
            }
        } catch (JSONException error){
            Log.d("error", error.toString());
        }
    }

    public void insertEnrollHandler(String data){

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            JSONObject jsonObject1 = new JSONObject(data);
            JSONArray jsonArray = jsonObject1.getJSONArray("result");

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
                Log.d("status = ", tag_id + " ");
            }
        } catch (JSONException error){
            Log.d("error", error.toString());
        }
    }

    public void insertStaff(String data){

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            JSONObject jsonObject1 = new JSONObject(data);
            JSONArray jsonArray = jsonObject1.getJSONArray("result");

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
                Log.d("status = ", tag_id + " ");
            }
        } catch (JSONException error){
            Log.d("error", error.toString());
        }
    }

    public void insertStudent(String data){

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            JSONObject jsonObject1 = new JSONObject(data);
            JSONArray jsonArray = jsonObject1.getJSONArray("result");

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
                long tag_id = db.insert(TABLE_ENROLL_HANDLER, null, values);
                Log.d("status = ", tag_id + " ");
            }
        } catch (JSONException error){
            Log.d("error", error.toString());
        }
    }

    public void insertVenue(String data){

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            JSONObject jsonObject1 = new JSONObject(data);
            JSONArray jsonArray = jsonObject1.getJSONArray("result");

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(VENUE_ID, result.getString("venue_id"));
                values.put(VENUE_NAME, result.getString("venue_name"));
                values.put(VENUE_CAPACITY, result.getString("venue_capacity"));
                values.put(CREATED_DATE, result.getString("created_date"));
                values.put(UPDATED_DATE, result.getString("updated_date"));
                // insert row
                long tag_id = db.insert(TABLE_ENROLL_HANDLER, null, values);
                Log.d("status = ", tag_id + " ");
            }
        } catch (JSONException error){
            Log.d("error", error.toString());
        }
    }

    public void insertVenueHandler(String data){

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            JSONObject jsonObject1 = new JSONObject(data);
            JSONArray jsonArray = jsonObject1.getJSONArray("result");

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(VENUE_HANDLER_ID, result.getString("venue_handler_id"));
                values.put(VENUE_ID, result.getString("venue_id"));
                values.put(COURSE_ID,result.getString("course_id"));
                values.put(CREATED_DATE, result.getString("created_date"));
                values.put(UPDATED_DATE, result.getString("updated_date"));
                // insert row
                long tag_id = db.insert(TABLE_ENROLL_HANDLER, null, values);
                Log.d("status = ", tag_id + " ");
            }
        } catch (JSONException error){
            Log.d("error", error.toString());
        }
    }

    public int checkAlreadyScan(int student_id, String course_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ENROLL_HANDLER +
                " WHERE "
                + STUDENT_ID + " = " + student_id
                + " AND "
                + COURSE_ID + " = " + course_id;

        Log.e("Log", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        int isCheck =  cursor.getInt(cursor.getColumnIndex(ISCHECKED));

        return isCheck;
    }
}
