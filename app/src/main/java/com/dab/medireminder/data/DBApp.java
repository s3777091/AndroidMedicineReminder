package com.dab.medireminder.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.dab.medireminder.data.model.BloodPressure;
import com.dab.medireminder.data.model.Medicine;
import com.dab.medireminder.data.model.MedicineTimer;

import java.util.ArrayList;


//Sql database
public class DBApp extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MedicineReminder.db";
    private static final String TABLE_TIMER = "tblTimer";
    private static final String TABLE_MEDICINE = "tblMedicine";
    private static final String TABLE_BLOOD_PRESSURE = "tblBloodPressure";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DOSE = "dose";
    private static final String COLUMN_TIME = "timer";
    private static final String COLUMN_REPEAT = "repeat";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_BLOOD_MIN = "blood_min";
    private static final String COLUMN_BLOOD_MAX = "blood_max";

    private static final int DB_VERSION = 1;

    public DBApp(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    public DBApp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //While start create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TIMER
                + "(" + COLUMN_ID + " Text PRIMARY KEY, "
                + COLUMN_NAME + " Text, "
                + COLUMN_DOSE + " Text, "
                + COLUMN_TIME + " Text, "
                + COLUMN_REPEAT + " Text, "
                + COLUMN_IMAGE + " Text "
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MEDICINE
                + "(" + COLUMN_ID + " Text PRIMARY KEY, "
                + COLUMN_NAME + " Text, "
                + COLUMN_DOSE + " Text, "
                + COLUMN_IMAGE + " Text "
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BLOOD_PRESSURE
                + "(" + COLUMN_ID + " Text PRIMARY KEY, "
                + COLUMN_BLOOD_MAX + " Integer, "
                + COLUMN_BLOOD_MIN + " Integer, "
                + COLUMN_TIME + " Text "
                + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //Function add time medicine to database
    public boolean addTimer(MedicineTimer medicineTimer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, medicineTimer.getId());
        contentValues.put(COLUMN_NAME, medicineTimer.getName());
        contentValues.put(COLUMN_DOSE, medicineTimer.getDose());
        contentValues.put(COLUMN_TIME, medicineTimer.getTimer());
        contentValues.put(COLUMN_REPEAT, medicineTimer.getRepeat());
        contentValues.put(COLUMN_IMAGE, medicineTimer.getIcon());
        long success = db.insert(TABLE_TIMER, null, contentValues);
        db.close();
        if (success != -1) {
            return true;
        }
        return false;
    }

    //Function delete time in database
    public void deleteTimer(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TIMER, COLUMN_ID + "=?", new String[]{id});
        db.close();
    }

    //Get all list of medicine timmer
    public ArrayList<MedicineTimer> getMedicineTimer() {
        ArrayList<MedicineTimer> itemList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_TIMER, null);
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    MedicineTimer medicineTimer = new MedicineTimer();
                    medicineTimer.setId(res.getString(res.getColumnIndex(COLUMN_ID)));
                    medicineTimer.setName(res.getString(res.getColumnIndex(COLUMN_NAME)));
                    medicineTimer.setDose(res.getString(res.getColumnIndex(COLUMN_DOSE)));
                    medicineTimer.setTimer(res.getString(res.getColumnIndex(COLUMN_TIME)));
                    medicineTimer.setRepeat(res.getString(res.getColumnIndex(COLUMN_REPEAT)));
                    medicineTimer.setIcon(res.getString(res.getColumnIndex(COLUMN_IMAGE)));
                    itemList.add(medicineTimer);
                    res.moveToNext();
                }
                res.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemList;
    }

    //Get one medicine timer
    public MedicineTimer getMedicineTimer(String id) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_TIMER + " WHERE " + COLUMN_ID + " = '" + id + "'", null);
            MedicineTimer medicineTimer = new MedicineTimer();
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    medicineTimer.setId(res.getString(res.getColumnIndex(COLUMN_ID)));
                    medicineTimer.setName(res.getString(res.getColumnIndex(COLUMN_NAME)));
                    medicineTimer.setTimer(res.getString(res.getColumnIndex(COLUMN_TIME)));
                    medicineTimer.setDose(res.getString(res.getColumnIndex(COLUMN_DOSE)));
                    medicineTimer.setRepeat(res.getString(res.getColumnIndex(COLUMN_REPEAT)));
                    medicineTimer.setIcon(res.getString(res.getColumnIndex(COLUMN_IMAGE)));
                    break;
                }
                res.close();
            }
            db.close();

            if (!TextUtils.isEmpty(medicineTimer.getName())) {
                return medicineTimer;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //add one medicine
    public boolean addMedicine(Medicine medicine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, medicine.getName());
        contentValues.put(COLUMN_NAME, medicine.getName());
        contentValues.put(COLUMN_DOSE, medicine.getDose());
        contentValues.put(COLUMN_IMAGE, medicine.getImage());
        long success = db.insert(TABLE_MEDICINE, null, contentValues);
        db.close();
        if (success != -1) {
            return true;
        }
        return false;
    }

    //delete one medicine
    public void deleteMedicine(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICINE, COLUMN_ID + "=?", new String[]{id});
        db.close();
    }

    //get one medicine
    public Medicine getMedicine(String id) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_MEDICINE + " WHERE " + COLUMN_ID + " = '" + id + "'", null);
            Medicine medicine = new Medicine();
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    medicine.setName(res.getString(res.getColumnIndex(COLUMN_NAME)));
                    medicine.setImage(res.getString(res.getColumnIndex(COLUMN_IMAGE)));
                    medicine.setDose(res.getString(res.getColumnIndex(COLUMN_DOSE)));
                    break;
                }
                res.close();
            }
            db.close();

            if (!TextUtils.isEmpty(medicine.getName())) {
                return medicine;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //get a list of medicine
    public ArrayList<Medicine> getMedicine() {
        ArrayList<Medicine> itemList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_MEDICINE, null);
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    Medicine medicineTimer = new Medicine();
                    medicineTimer.setName(res.getString(res.getColumnIndex(COLUMN_NAME)));
                    medicineTimer.setDose(res.getString(res.getColumnIndex(COLUMN_DOSE)));
                    medicineTimer.setImage(res.getString(res.getColumnIndex(COLUMN_IMAGE)));
                    itemList.add(medicineTimer);
                    res.moveToNext();
                }
                res.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemList;
    }


    //add blood pressure to database
    public boolean addBloodPressure(BloodPressure medicine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, String.valueOf(medicine.getTimer()));
        contentValues.put(COLUMN_BLOOD_MAX, medicine.getMax());
        contentValues.put(COLUMN_BLOOD_MIN, medicine.getMin());
        contentValues.put(COLUMN_TIME, String.valueOf(medicine.getTimer()));
        long success = db.insert(TABLE_BLOOD_PRESSURE, null, contentValues);
        db.close();
        if (success != -1) {
            return true;
        }
        return false;
    }

    //delete blood pressure in database
    public void deleteBloodPressure(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BLOOD_PRESSURE, COLUMN_ID + "=?", new String[]{id});
        db.close();
    }

    //get list of blood pressure
    public ArrayList<BloodPressure> getBloodPressure() {
        ArrayList<BloodPressure> itemList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_BLOOD_PRESSURE, null);
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    BloodPressure bloodPressure = new BloodPressure();
                    bloodPressure.setMax(res.getInt(res.getColumnIndex(COLUMN_BLOOD_MAX)));
                    bloodPressure.setMin(res.getInt(res.getColumnIndex(COLUMN_BLOOD_MIN)));
                    bloodPressure.setTimer(Long.parseLong(res.getString(res.getColumnIndex(COLUMN_TIME))));
                    itemList.add(bloodPressure);
                    res.moveToNext();
                }
                res.close();
            }
            db.close();
        } catch (Exception e) {

        }
        return itemList;
    }


}