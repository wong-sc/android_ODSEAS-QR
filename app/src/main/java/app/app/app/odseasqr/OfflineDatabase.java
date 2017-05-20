package app.app.app.odseasqr;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class OfflineDatabase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME ="odseas-qr";

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
    private static final String CHECKOUT_STAFFID = "checkout_staffID";
    private static final String CHECKIN_STAFFID = "checkin_staffID";
    private static final String CHECKIN_STYLE_ID = "checkin_style_id";
    private static final String CHECKOUT_STYLE_ID = "checkout_style_id";

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

    private static final String TABLE_TRACK_CHANGES = "track_changes";
    private static final String TRACK_ID = "track_id";
    private static final String CHANGES_STUDENT_ID = "changes_student_id";

    private static final String TABLE_ATTENDANCE_STYLE = "take_attendance_style";
    private static final String STYLE_ID = "style_id";
    private static final String STYLE_NAME = "style_name";

    Context context;
    SharedPreferences sharedPreferences;

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
                + STATUS + " INTEGER,"
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
                + CHECKIN_STAFFID + " INTEGER,"
                + CHECKOUT_STAFFID + " INTEGER,"
                + CHECKIN_STYLE_ID + " INTEGER,"
                + CHECKOUT_STYLE_ID + " INTEGER,"
                + STATUS + " INTEGER DEFAULT 0,"
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

        String CREATE_ATTENDANCE_STYLE = "CREATE TABLE " + TABLE_ATTENDANCE_STYLE + "("
                + STYLE_ID + " INTEGER PRIMARY KEY,"
                + STYLE_NAME + " VARCHAR" + ")";

        // Creating requires tables
        odseasqr.execSQL(CREATE_COURSE_TABLE);
        odseasqr.execSQL(CREATE_COURSE_HANDLER_TABLE);
        odseasqr.execSQL(CREATE_ENROLL_HANDLER_TABLE);
        odseasqr.execSQL(CREATE_STAFF_TABLE);
        odseasqr.execSQL(CREATE_STUDENT_TABLE);
        odseasqr.execSQL(CREATE_VENUE_TABLE);
        odseasqr.execSQL(CREATE_VENUE_HANDLER_TABLE);
        odseasqr.execSQL(CREATE_ATTENDANCE_STYLE);
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
        odseasqr.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE_STYLE);
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

    public void insertAttendanceStyle(String data){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ATTENDANCE_STYLE, null,null);
        db.beginTransaction();

        try {
            JSONArray jsonArray = new JSONArray(data);

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(STYLE_ID, result.getInt("style_id"));
                // insert row
                long tag_id = db.insert(TABLE_ATTENDANCE_STYLE, null, values);
                Log.d("attendance style = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
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
        Log.d("enroll", data);

        try {
            JSONArray jsonArray = new JSONArray(data);

            for(int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(ENROLL_HANDLER_ID, result.getString("enroll_handler_id"));
                values.put(STUDENT_ID, result.getString("student_id"));
                values.put(COURSE_ID, result.getString("course_id"));
                values.put(ISCHECKED, result.getString("ischecked"));

                if(result.getString("ischecked").equals("1"))
                    values.put(STATUS, 2);
                else if(!result.getString("checkin_time").equals("null") && result.getString("checkout_time").equals("null"))
                    values.put(STATUS, 1);

                values.put(CHECKIN_TIME, result.getString("checkin_time"));
                values.put(CHECKOUT_TIME, result.getString("checkout_time"));
                values.put(CHECKIN_STYLE_ID, result.getString("checkin_style_id"));
                values.put(CHECKOUT_STYLE_ID, result.getString("checkout_style_id"));
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

    public String SyncEnrollHandler(String data){

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
                values.put(STATUS, result.getString("status"));
                values.put(CHECKIN_TIME, result.getString("checkin_time"));
                values.put(CHECKOUT_TIME, result.getString("checkout_time"));
                values.put(CHECKIN_STYLE_ID, result.getString("checkin_style_id"));
                values.put(CHECKOUT_STYLE_ID, result.getString("checkout_style_id"));
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
        return "success";
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

    /*[{
    "created_date":"2017-03-31 13:37:48",
    "checkout_time":"2017-04-12 00:45:43",
    "checkin_time":"2017-03-31 13:42:11",
    "updated_date":"2017-03-31 13:37:48",
    "checkout_staffID":"1",
    "status":"0",
    "checkout_style_id":"2",
    "enroll_handler_id":"1",
    "student_id":"44648",
    "checkin_style_id":"1",
    "ischecked":"1",
    "course_id":"TMN2053"
    }]*/

    public String insertDataFrom_ (String result){
        SQLiteDatabase db = this.getReadableDatabase();
        sharedPreferences = context.getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
        db.beginTransaction();
        try {
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0 ; i < jsonArray.length() ; i++){
                Log.d(SyncActivity.TAG, jsonArray.length() + "");
                JSONObject jsonObject = jsonArray.getJSONObject(i);

//                if(!jsonObject.getString(COURSE_ID).equals(sharedPreferences.getString(Config.COURSE_ID, "null"))){
//                    return "fail";
//                }

                ContentValues values = new ContentValues();
                values.put(CHECKIN_TIME, jsonObject.getString(CHECKIN_TIME));
                values.put(CHECKOUT_TIME, jsonObject.getString(CHECKOUT_TIME));
                values.put(CHECKIN_STAFFID, jsonObject.getInt(CHECKIN_STAFFID));
                values.put(CHECKOUT_STAFFID, jsonObject.getInt(CHECKOUT_STAFFID));
                values.put(STATUS, jsonObject.getInt(STATUS));
                values.put(CHECKIN_STYLE_ID, jsonObject.getInt(CHECKIN_STYLE_ID));
                values.put(CHECKOUT_STYLE_ID, jsonObject.getInt(CHECKOUT_STAFFID));
                values.put(ISCHECKED, jsonObject.getString(ISCHECKED));

                int status = db.update(
                        TABLE_ENROLL_HANDLER, values,
                        STUDENT_ID + "= ? AND "+ COURSE_ID + " = ? AND (" + STATUS + " != 2 AND " + STATUS + " != 4)" ,
                        new String[]{jsonObject.getString("student_id"), jsonObject.getString("course_id")});
                Log.d("status", status+"");
            }
            db.setTransactionSuccessful();
        } catch (JSONException error){
            Log.d("error", error.toString());
        } finally {
            db.endTransaction();

        }
        db.close();
        return "success update";
    }

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
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = 'null' AND %s = 'null' ORDER BY %s ASC",
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
                    jsonObject.put(STATUS, cursor.getString(10));
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
                    jsonObject.put(STATUS, cursor.getString(10));
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
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s != 'null'",
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        CHECKOUT_TIME);

        Log.d("query", SELECT_COURSE_DETAILS);

        Cursor cursor = db.rawQuery(SELECT_COURSE_DETAILS, null);

        if (cursor.moveToFirst()) {
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));
            count = cursor.getCount();
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
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s != 'null'",
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
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s != 'null' ORDER BY %s ASC",
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
                    jsonObject.put(STATUS, cursor.getString(10));
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
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s != 'null' AND %s = 'null' ORDER BY %s ASC",
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
                    jsonObject.put(STATUS, cursor.getString(10));
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
                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = 1 ORDER BY %s ASC",
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        ISCHECKED,
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
                    jsonObject.put(STATUS, cursor.getString(10));
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

    public String updateAttendanceRecord(String student_id, String course_id, String staffID, String style_id, int mode){

        /*this method is  used to update the attendance in the enroll_handler table*/

        SQLiteDatabase db = this.getReadableDatabase();
        String isChecked = "";
        Log.d("staffID: ", staffID);

        /*query to select the checkin time of specific student*/
        String CHECK_STUDENT =
                String.format("SELECT %s FROM %s WHERE %s = '%s' AND %s = '%s'",
                        CHECKIN_TIME,
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        STUDENT_ID, student_id);

        /*cursor to search in the database*/
        Cursor cursor = db.rawQuery(CHECK_STUDENT, null);

        if (cursor != null) {
            cursor.moveToFirst();
            /*only will execute this "IF loop" if there is a matched result*/
            Log.d("Result cursor2--", DatabaseUtils.dumpCursorToString(cursor));
                /* since only one item will be selected therefore first index will be 0*/
            isChecked = cursor.getString(0);
        } else Log.d("Result == ", "NO");

        if(isChecked.equals("null")){
            /* content values will store the data that want to update in its */
            ContentValues values = new ContentValues();
            values.put(CHECKIN_TIME, getDateTime());
            values.put(CHECKIN_STAFFID, staffID);
            values.put(CHECKIN_STYLE_ID, style_id);

            if(mode == 1)
                values.put(STATUS, 1);
            else if(mode == 0)
                values.put(STATUS, 3);

            /*db.update will update the value in the content values to the database*/
            db.update(TABLE_ENROLL_HANDLER,values, STUDENT_ID + "= ? AND "+ COURSE_ID + " = ?", new String[]{student_id, course_id});
            /*return success checkin if the checkin time is null*/
            return "success checkin";

        } else {
            ContentValues values_checkout = new ContentValues();
            values_checkout.put(CHECKOUT_TIME, getDateTime());
            values_checkout.put(CHECKOUT_STAFFID, staffID);
            values_checkout.put(CHECKOUT_STYLE_ID, style_id);

            if(mode == 1)
                values_checkout.put(STATUS, 2);
            else if (mode == 0)
                values_checkout.put(STATUS, 4);

            db.update(TABLE_ENROLL_HANDLER,values_checkout, STUDENT_ID + "= ? AND "+ COURSE_ID + " = ?", new String[]{student_id, course_id});

            ContentValues values_isChecked = new ContentValues();
            values_isChecked.put(ISCHECKED, 1);
            db.update(TABLE_ENROLL_HANDLER,values_isChecked, STUDENT_ID + "= ? AND "+ COURSE_ID + " = ?", new String[]{student_id, course_id});
            /*return success checkout if the checkin time is not null this mean that the student already checkin before*/
            return "success checkout";
        }
    }

    public String StopCourse(String course_id){

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATUS, 1);
        db.update(TABLE_COURSE, values, COURSE_ID + "= ?", new String[]{course_id});
        return "success";
    }

    public Boolean check_course_status(String course_id){
        boolean status = false;

        SQLiteDatabase db = this.getReadableDatabase();

        /*query to select the checkin time of specific student*/
        String CHECK_STATUS =
                String.format("SELECT %s FROM %s WHERE %s = '%s'",
                        STATUS,TABLE_COURSE, COURSE_ID, course_id);

        Cursor cursor = db.rawQuery(CHECK_STATUS, null);

        if((cursor != null) && (cursor.getCount() > 0)){
            cursor.moveToNext();
            if(cursor.getString(0).equals("0"))
                status = true;
            else status = false;
        }
        assert cursor != null;
        cursor.close();
        db.close();
        return status;
    }

    public String getCourseStatus(String course_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String status;

        String CHECK_STATUS =
                String.format("SELECT %s FROM %s WHERE %s = %s", STATUS, TABLE_COURSE, COURSE_ID, course_id);

        Cursor cursor = db.rawQuery(CHECK_STATUS, null);

        if(cursor.moveToFirst()){
            status = cursor.getString(0);
        } else status = "fail";

        cursor.close();
        db.close();
        return status;
    }

    public Cursor getUnsyscData(String course_id){
        SQLiteDatabase db = this.getReadableDatabase();

        /*query to select the checkin time of specific student*/
        String CHECK_UNSYNC =
                String.format("SELECT * FROM %s WHERE (%s = %s OR %s = %s) AND %s = '%s'",
                        TABLE_ENROLL_HANDLER,
                        STATUS, 3, STATUS, 4, COURSE_ID, course_id);

        /*cursor to search in the database*/
        return db.rawQuery(CHECK_UNSYNC, null);
    }

    public Cursor getAttendance(String course_id){

        SQLiteDatabase db = this.getReadableDatabase();

        /*query to select the checkin time of specific student*/
        String GET_ATTENDANCE =
                String.format("SELECT * FROM %s",
                        TABLE_ENROLL_HANDLER);

        /*cursor to search in the database*/
        return db.rawQuery(GET_ATTENDANCE, null);

    }

    public ArrayList<String> check_attendance(SQLiteDatabase db, String student_id, String course_id){
//        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> status = new ArrayList<String>();

         /*query to select the checkin time of specific student*/
        String STUDENT_CHECK =
                String.format("SELECT %s, %s FROM %s WHERE %s = '%s' AND %s = '%s'",
                        CHECKIN_TIME, CHECKOUT_TIME,
                        TABLE_ENROLL_HANDLER,
                        COURSE_ID, course_id,
                        STUDENT_ID, student_id);

        Cursor cursor = db.rawQuery(STUDENT_CHECK, null);

        if (cursor.moveToFirst()) {
            /*only will execute this "IF loop" if there is a matched result*/
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));
            status.add(cursor.getString(0));
            status.add(cursor.getString(1));

        } else Log.d("Result == ", "NO");

        cursor.close();
        return status;
    }

    public void markedSyncRecord(JSONObject data){
        /*THIS FUNCTION IS USED TO KEEP TRACK WHICH RECORD HAS BEEN SYNC
        * IF SYNC THEN REMOVE THE RECORD FROM THIS TABLE
        * ELSE KEEP IT AND WAIT FOR IT TO BE SYNCED*/
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> status = new ArrayList<String>();

            ContentValues markedCheckin = new ContentValues();
            try {
                markedCheckin.put(STATUS, data.getInt("check"));
                Log.d("database", data.getString("student_id") + data.getString("course_id"));
                db.update(TABLE_ENROLL_HANDLER, markedCheckin, STUDENT_ID + "= ? AND "+ COURSE_ID + " = ?", new String[]{data.getString("student_id"), data.getString("course_id")});
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public String getStatus(String student_id, String course_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String GET_STATUS =
                String.format("SELECT %s FROM %s WHERE %s = '%s' AND %s = '%s'",
                        STATUS, TABLE_ENROLL_HANDLER,
                        STUDENT_ID, student_id,
                        COURSE_ID, course_id);

        Cursor cursor = db.rawQuery(GET_STATUS, null);

        if (cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
            Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));
            String status = cursor.getString(0);
            cursor.close();
            db.close();
            return status;
        } else {
            db.close();
            return "0";
        }


    }

    public String CheckCourseTime(String course_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String GET_TIME =
                String.format("SELECT * FROM %s WHERE %s = '%s'",
                        TABLE_COURSE,
                        COURSE_ID, course_id);

        Cursor cursor = db.rawQuery(GET_TIME, null);
        Log.d("Result coursetime", DatabaseUtils.dumpCursorToString(cursor));
//        cursor.moveToNext()
        if ((cursor != null) && (cursor.getCount() > 0)){
            cursor.moveToFirst();

            String date = cursor.getString(3);
            String s_time = cursor.getString(4);
            String e_time = cursor.getString(5);

            String st_time = String.format("%s %s", date, s_time);
            String ed_time = String.format("%s %s", date, e_time);

            String start_time = validate_start_time(st_time);
            String end_time = validate_end_time(ed_time);

            cursor.close();
            db.close();

            if(start_time.equals("0") && end_time.equals("0"))
                return Config.AVAILABLE;
            else
                return Config.UNAVAILABLE;

        } else {
            db.close();
            return Config.UNAVAILABLE;
        }
    }

    private String validate_end_time(String format) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String current = getDateTime();

        Date date = null;
        Date now = null;
        String status = "0";

        try {

            date = dateFormat.parse(format);
            now = dateFormat.parse(current);

            long diff = now.getTime() - date.getTime();
            status = calculate_time(diff);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return status;
    }

    private String calculate_time(long diff){

        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        Log.d("start_min", " " + diffMinutes);
        Log.d("start_hour", " " + diffHours);
        Log.d("start_day", " " + diffDays);

        if(diffDays>0) {
            Log.d("Time", diffDays + " days ago");
            return String.valueOf(diffDays);
        } else if(diffHours>1) {
            Log.d("Time", diffHours + " hours ago");
            return String.valueOf(diffHours);
        } else if(diffMinutes>30) {
            Log.d("Time", diffMinutes + " minutes ago");
            return String.valueOf(diffMinutes);
        } else {
            Log.d("Time", String.valueOf("Now"));
            return String.valueOf(0);
        }
    }

    private String validate_start_time(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String current = getDateTime();

        Date date = null;
        Date now = null;
        String status = "0";

        try {

            date = dateFormat.parse(format);
            now = dateFormat.parse(current);

            long diff = date.getTime() - now.getTime();
            Log.d("start", " " + diff);
            status = calculate_time(diff);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return status;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
