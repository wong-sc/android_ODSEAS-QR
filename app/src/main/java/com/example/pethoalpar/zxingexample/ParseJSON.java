package com.example.pethoalpar.zxingexample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseJSON {
    public static String[] ids;
    public static String[] names;

    public static final String JSON_ARRAY = "result";
    public static final String KEY_ID = "stud_id";
    public static final String KEY_NAME = "stud_name";


    private JSONArray users = null;

    private String json;

    public ParseJSON(String json){
        this.json = json;
    }

    protected void parseJSON(){
        JSONObject jsonObject=null;
        try {
            jsonObject = new JSONObject(json);
            users = jsonObject.getJSONArray(JSON_ARRAY);


            ids = new String[users.length()];
            names = new String[users.length()];

            for(int i=0;i<users.length();i++){
                JSONObject jo = users.getJSONObject(i);
                ids[i] = jo.getString(KEY_ID);
                names[i] = jo.getString(KEY_NAME);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
