package edu.uic.cs478.gmasca2.project5app2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Glenn on 28-Apr-18.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    final static String DATABASE_NAME = "Money_Database";
    final static String TABLE_NAME = "Money_Table";

    final static String COLUMN_ID = "_id";
    final static String COLUMN_YEAR = "Year";
    final static String COLUMN_MONTH = "Month";
    final static String COLUMN_DAY = "Day";
    final static String COLUMN_DAY_OF_WEEK = "Day_of_Week";
    final static String COLUMN_OPEN_BALANCE = "Open_Balance";
    final static String COLUMN_CLOSE_BALANCE = "Close_Balance";

    private Context mContext;

    private final static String CREATE_CMD =
            "CREATE TABLE " + TABLE_NAME + "("+ COLUMN_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_YEAR + " INT, " + COLUMN_MONTH + " INT, " + COLUMN_DAY + " INT, "
                    + COLUMN_DAY_OF_WEEK + " TEXT, " + COLUMN_OPEN_BALANCE + " INT, "
                    + COLUMN_CLOSE_BALANCE + " INT)";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // drop earlier previous instance of table if exists
        db.execSQL("DROP TABLE IF EXISTS Money_Table");

        // create empty table by executing command
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    void deleteDatabase() {
        mContext.deleteDatabase(DATABASE_NAME);
    }
}
