package com.example.lenovo.gridy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context){
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "groupp INTEGER);");
        db.execSQL("INSERT INTO users (name, groupp) VALUES('Антон Талецкий', 453501);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS users"); // удаление базы с созданием новой
        onCreate(db);
    }
}
