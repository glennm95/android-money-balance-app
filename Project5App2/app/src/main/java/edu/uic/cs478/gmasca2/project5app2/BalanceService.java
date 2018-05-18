package edu.uic.cs478.gmasca2.project5app2;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.uic.cs478.gmasca2.project5app2common.BalanceServiceAIDL;
import edu.uic.cs478.gmasca2.project5app2common.DailyCash;

/**
 * Created by Glenn on 29-Apr-18.
 */

public class BalanceService extends Service {

    MyDatabaseHelper myDatabaseHelper;
    SQLiteDatabase database;
    DailyCash dailyCashInstance;
    DailyCash[] dailyCashArray;
    Object lock1, lock2;
//    Output output;
//    List<Output> outputList;

    private final static String TAG = "Balance Service";


//    final class ServiceThread{
//        int service_id;
//        ServiceThread(int service_id){
//            this.service_id = service_id;
//        }
//    }


    private final BalanceServiceAIDL.Stub mBinder = new BalanceServiceAIDL.Stub() {

        @Override
        public boolean createDatabase() throws RemoteException {
            Runnable R1 = new Runnable() {
                @Override
                public void run() {

                }
            };


            Log.i(TAG,"createDatabase() entered");

            // initialize SQLiteOpenHelper class and pass context
            myDatabaseHelper = new MyDatabaseHelper(getApplicationContext());

            // clear the table of entries
            clearAll();

            // fetch writable database from SQLiteOpenHelper object
            database = myDatabaseHelper.getWritableDatabase();

//            outputList = new ArrayList<>();

            // initialize ContentValaues object to insert rows into database
            ContentValues values = new ContentValues();

            try {
                // read the text file into BufferedReader object
                BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("treasury_io_final.txt")));
                String line;

                // read every line of file until null
                while((line = br.readLine())!=null){

                    // split line on commas and store into lineArray
                    String[] lineArray = line.split(",");

                    // put into ContentValues object
                    values.put(MyDatabaseHelper.COLUMN_YEAR,lineArray[0]);
                    values.put(MyDatabaseHelper.COLUMN_MONTH,lineArray[1]);
                    values.put(MyDatabaseHelper.COLUMN_DAY,lineArray[2]);
                    values.put(MyDatabaseHelper.COLUMN_DAY_OF_WEEK,lineArray[3]);
                    values.put(MyDatabaseHelper.COLUMN_OPEN_BALANCE,lineArray[4]);
                    values.put(MyDatabaseHelper.COLUMN_CLOSE_BALANCE,lineArray[5]);

                    // insert values into table
                    database.insert(MyDatabaseHelper.TABLE_NAME,null,values);

                    // clear ContentValues object for reuse
                    values.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // display data from database for testing purposes
//            String[] projection = {MyDatabaseHelper.COLUMN_ID, MyDatabaseHelper.COLUMN_YEAR, MyDatabaseHelper.COLUMN_MONTH,
//                    MyDatabaseHelper.COLUMN_DAY, MyDatabaseHelper.COLUMN_DAY_OF_WEEK, MyDatabaseHelper.COLUMN_OPEN_BALANCE,
//                      MyDatabaseHelper.COLUMN_CLOSE_BALANCE};

//            Cursor cursor = database.query(MyDatabaseHelper.TABLE_NAME, projection, null, null, null, null, null);

//            while (cursor.moveToNext()){
//                output = new Output(
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID))
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_YEAR)),
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_MONTH)),
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DAY)),
//                        cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DAY_OF_WEEK)),
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_OPEN_BALANCE)),
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_CLOSE_BALANCE)));
//
//                Log.i(TAG, ""+cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper._ID))+", "+
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_YEAR))+", "+
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_MONTH))+ ", "+
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DAY))+ ", "+
//                        cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DAY_OF_WEEK))+ ", "+
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_OPEN_BALANCE))+ ", "+
//                        cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_CLOSE_BALANCE)));
//                outputList.add(output);
//            }

            if(database==null)
                return false;
            else{
                Log.i(TAG,"Database Created Successfully!");
                return true;
            }
        }

        @Override
        public DailyCash[] dailyCash(int day, int month, int year, int number_of_days) throws RemoteException {

            Log.i(TAG,"dailyCash() entered");

            dailyCashArray = new DailyCash[number_of_days];

            // do if database is created
            if(database != null){
                database = myDatabaseHelper.getReadableDatabase();

                String[] projection = {MyDatabaseHelper.COLUMN_ID, MyDatabaseHelper.COLUMN_YEAR, MyDatabaseHelper.COLUMN_MONTH,
                MyDatabaseHelper.COLUMN_DAY, MyDatabaseHelper.COLUMN_DAY_OF_WEEK, MyDatabaseHelper.COLUMN_CLOSE_BALANCE};

                String selection = MyDatabaseHelper.COLUMN_YEAR + " = ? AND "+  MyDatabaseHelper.COLUMN_MONTH + " = ? AND "+
                        MyDatabaseHelper.COLUMN_DAY + " = ?";
                String[] selectionArgs = {Integer.toString(year), Integer.toString(month), Integer.toString(day)};

                // query the database for the particular input. Cursor returns corresponding row for the specified input
                Cursor cursor = database.query(MyDatabaseHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

                // if cursor doesn't contain rows, implies that specified date is not present (non-working day)
                // if so, get the next nearest working day
                if(cursor.getCount()==0){
                    if((month == 2)&&(day == 28)){
                            day = 1;
                            month = 3;
                            String[] selectionArgsFix1 = {Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                            cursor = database.query(MyDatabaseHelper.TABLE_NAME, projection, selection, selectionArgsFix1, null, null, null);
                    }
                    else if ((month == 4 || month == 6 || month == 9 || month == 11) && (day == 30)){
                        int i = 0;
                        month = month + 1;
                        do {
                            day = 1+i;
                            String[] selectionArgsFix2 = {Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                            cursor = database.query(MyDatabaseHelper.TABLE_NAME, projection, selection, selectionArgsFix2, null, null, null);
                            i++;
                        }while (cursor.getCount()==0);
                    }
                    else if ((month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
                            && (day == 30)){
                        int j = 0,k = 0;
                        do {
                            if(day == 30)
                                day = day + 1;
                            else {
                                day = j;
                                if(k == 1){
                                    if(month == 12){
                                        month = 1;
                                        year = 2018;
                                    }
                                    else
                                        month = month + 1;
                                }
                            }
                            String[] selectionArgsFix3 = {Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                            cursor = database.query(MyDatabaseHelper.TABLE_NAME, projection, selection, selectionArgsFix3, null, null, null);
                            j++;
                            k++;
                        }while (cursor.getCount()==0);
                    }
                    else if(day == 31 && month == 12) {
                        year = 2018;
                        month = 1;
                        day = 2;
                        String[] selectionArgsFix4 = {Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                        cursor = database.query(MyDatabaseHelper.TABLE_NAME, projection, selection, selectionArgsFix4, null, null, null);
                    }
                    else {
                        do{
                            day = (day+1)%31;
                            String[] selectionArgsFix = {Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                            cursor = database.query(MyDatabaseHelper.TABLE_NAME, projection, selection, selectionArgsFix, null, null, null);
                        }while(cursor.getCount()==0);
                    }

                }

                // move cursor from -1 to 0 for reading entry
                cursor.moveToNext();

                int dayResult, monthResult, yearResult, closeBalanceResult,id;
                String dayOfWeekResult;

                // reading data from Cursor object
                id = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID));
                yearResult = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_YEAR));
                monthResult = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_MONTH));
                dayResult = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DAY));
                dayOfWeekResult = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DAY_OF_WEEK));
                closeBalanceResult = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_CLOSE_BALANCE));

                // show output in log
                Log.i(TAG,""+id+", "+yearResult+", "+monthResult+", "+dayResult+", "+dayOfWeekResult+
                ", "+closeBalanceResult);

                // check if remaining entries are within range; if not, return null
                if(id+number_of_days>291){
                    return null;
                }

                // call to DailyCash constructor to store input in instance
                dailyCashInstance = new DailyCash(dayResult, monthResult, yearResult, closeBalanceResult, dayOfWeekResult);

                // put instance in Array
                dailyCashArray[0] = dailyCashInstance;

                // get the remaining data by incrementing id and querying the database
                for (int i = 0; i < number_of_days-1; i++){
                    String nextSelection = MyDatabaseHelper.COLUMN_ID + " = ?";

                    String newSelectionArgs [] = {Integer.toString(id+1)};

                    Cursor c = database.query(MyDatabaseHelper.TABLE_NAME,projection,nextSelection,
                            newSelectionArgs,null,null,null);

                    c.moveToNext();

                    id = c.getInt(c.getColumnIndex(MyDatabaseHelper.COLUMN_ID));
                    yearResult = c.getInt(c.getColumnIndex(MyDatabaseHelper.COLUMN_YEAR));
                    monthResult = c.getInt(c.getColumnIndex(MyDatabaseHelper.COLUMN_MONTH));
                    dayResult = c.getInt(c.getColumnIndex(MyDatabaseHelper.COLUMN_DAY));
                    dayOfWeekResult = c.getString(c.getColumnIndex(MyDatabaseHelper.COLUMN_DAY_OF_WEEK));
                    closeBalanceResult = c.getInt(c.getColumnIndex(MyDatabaseHelper.COLUMN_CLOSE_BALANCE));

                    dailyCashInstance = new DailyCash(dayResult, monthResult, yearResult, closeBalanceResult, dayOfWeekResult);
                    dailyCashArray[i+1] = dailyCashInstance;

                    // show output in log
                    Log.i(TAG,""+id+", "+yearResult+", "+monthResult+", "+dayResult+", "+dayOfWeekResult+
                            ", "+closeBalanceResult);
                }
            }
            return dailyCashArray;
        }
    };

    private void clearAll() {
        // Call SQLiteDatabase.delete() -- null arg deletes all rows in arg table.
        myDatabaseHelper.getWritableDatabase().delete(myDatabaseHelper.TABLE_NAME, null, null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Service App", "On Bind entered");
        return mBinder;
    }

    @Override
    public void onDestroy() {

        myDatabaseHelper.getWritableDatabase().close();
        myDatabaseHelper.deleteDatabase();

        super.onDestroy();
    }
}
