package com.example.assignment1;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBhelper extends SQLiteOpenHelper {
    public DBhelper(Context context) {
        super(context, "RaviramMamidi.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL(" CREATE TABLE IF NOT EXISTS Userdetails( Heartrate FLOAT,Respiration FLOAT,Nausea FLOAT, Headache FLOAT, SoreThroat FLOAT,Diarrhea FLOAT,Fever FLOAT,MuscleAche FLOAT,LossOfSmellOrTaste FLOAT,Cough FLOAT,ShortnessOfBreath FLOAT,FeelingTired FLOAT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Userdetails");
    }

    public Boolean insertuserdata( Float Heartrate, Float Respiration, Float Nausea, Float Headache, Float SoreThroat, Float Diarrhea, Float Fever, Float MuscleAche, Float LossOfSmellOrTaste, Float Cough, Float ShortnessOfBreath, Float FeelingTired )
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Heartrate",Heartrate);
        contentValues.put("Respiration",Respiration);
        contentValues.put("Nausea",Nausea);
        contentValues.put("Headache",Headache);
        contentValues.put("SoreThroat",SoreThroat);
        contentValues.put("Diarrhea",Diarrhea);
        contentValues.put("Fever",Fever);
        contentValues.put("MuscleAche",MuscleAche);
        contentValues.put("LossOfSmellOrTaste",LossOfSmellOrTaste);
        contentValues.put("Cough",Cough);
        contentValues.put("ShortnessOfBreath",ShortnessOfBreath);
        contentValues.put("FeelingTired",FeelingTired);

        long result=DB.insert("Userdetails", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

//
//    public Boolean updateuserdata(String symptom, Float value) {
//        SQLiteDatabase DB = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("value", value);
//        Cursor cursor = DB.rawQuery("Select * from Userdetails where symptom = ?", new String[]{symptom});
//        if (cursor.getCount() > 0) {
//            long result = DB.update("Userdetails", contentValues, "symptom=?", new String[]{symptom});
//            if (result == -1) {
//                return false;
//            } else {
//                return true;
//            }
//        } else {
//            return false;
//        }}


    public Cursor getdata ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Userdetails", null);
        return cursor;
    }
}