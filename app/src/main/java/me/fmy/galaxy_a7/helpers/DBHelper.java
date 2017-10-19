package me.fmy.galaxy_a7.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import me.fmy.galaxy_a7.helpers.DBConst;
import me.fmy.galaxy_a7.models.User;

/**
 * Created by Femmy on 7/24/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, DBConst.DATABASE_NAME, null, DBConst.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + DBConst.TABLE_USER + "("
                + DBConst.COL_ID + " INTEGER PRIMARY KEY," + DBConst.COL_NAME + " TEXT,"
                + DBConst.COL_EMAIL + " TEXT,"+ DBConst.COL_IG + " TEXT,"
                + DBConst.COL_SONG + " INTEGER,"+ DBConst.COL_VIDEO + " TEXT" +")";
        db.execSQL(CREATE_USER_TABLE);
    }

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBConst.COL_NAME, user.name); // Contact Name
        values.put(DBConst.COL_EMAIL, user.email); // Contact Name
        values.put(DBConst.COL_IG, user.instagram); // Contact Name
        values.put(DBConst.COL_SONG, user.song); // Contact Name
        values.put(DBConst.COL_VIDEO, user.video); // Contact Name
        // Inserting Row
        Long insert_id = db.insert(DBConst.TABLE_USER, null, values);
        db.close(); // Closing database connection
        return insert_id;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBConst.TABLE_USER);
        // Create tables again
        onCreate(db);
    }

    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBConst.TABLE_USER, new String[] { DBConst.COL_ID,
                        DBConst.COL_NAME, DBConst.COL_EMAIL, DBConst.COL_IG }, DBConst.COL_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(
                        cursor.getInt(0)
                        ,cursor.getString(1)
                        , cursor.getString(2)
                        , cursor.getString(3)
                        , cursor.getInt(4)
                        , cursor.getString(5)
                    );
        return user;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DBConst.TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.id = cursor.getInt(0);
                user.name = cursor.getString(1);
                user.email = cursor.getString(2);
                user.instagram = cursor.getString(3);

                // Adding contact to list
                userList.add(user);
            } while (cursor.moveToNext());
        }

        // return contact list
        return userList;
    }
}
