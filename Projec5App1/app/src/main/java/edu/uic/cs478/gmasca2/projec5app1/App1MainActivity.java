package edu.uic.cs478.gmasca2.projec5app1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import edu.uic.cs478.gmasca2.project5app2common.BalanceServiceAIDL;
import edu.uic.cs478.gmasca2.project5app2common.DailyCash;

public class App1MainActivity extends Activity {

    int day, month, year, dateRange;
    boolean mIsBound = false;
    private BalanceServiceAIDL mBalanceService;
    public static final String TAG = "Client";
    boolean bool = false;
    String dateValue, dayValue, monthValue;
    int count = 0;
    DailyCash[] dailyCashArray;
    ArrayList<DailyCash> dailyCashArrayList;
    DailyCash dailyCashInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app1_main);

        // find views
        Button createDatabase = (Button) findViewById(R.id.create_database_button);
        Button viewResults = (Button) findViewById(R.id.view_results_button);
        final EditText dayInput = (EditText) findViewById(R.id.day_input);
        final EditText monthInput = (EditText) findViewById(R.id.month_input);
        final EditText yearInput = (EditText) findViewById(R.id.year_input);
        final EditText dateRangeInput = (EditText) findViewById(R.id.date_range_input);

        if(!mIsBound){
            boolean b;
            Intent intent = new Intent(BalanceServiceAIDL.class.getName());
            ResolveInfo info = getPackageManager().resolveService(intent, 0);
            intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
//            intent.setComponent(new ComponentName("edu.uic.cs478.gmasca2.project5app2","edu.uic.cs478.gmasca2.project5app2.BalanceService"));

            b = bindService(intent, this.mConnection, Context.BIND_AUTO_CREATE);

            if(b){
                Log.i(TAG, "bindService() succeeded!");
            }else{
                Log.i(TAG, "bindService() failed!");
            }
        }

        dailyCashArrayList = new ArrayList<>();

        // on click Create Database button
        createDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count == 0) {
                    try {
                        if (mIsBound) {
                            // call Service's API method createDatabase()
                            bool = mBalanceService.createDatabase();

                            Log.i(TAG, "mIsBound = " + mIsBound);
                            Log.i(TAG, "Bool = " + bool);

                            if (bool) {
                                count++;
                                Toast.makeText(getApplicationContext(),
                                        "Database Created Successfully!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Log.i(TAG, "Database not created. Try again!");
                        } else
                            Log.i(TAG, "The service was not bound!");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Database already created!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // on click View Results button
        viewResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // handling null inputs
                if(dayInput.getText().toString().matches("")|| monthInput.getText().toString().matches("") ||
                        yearInput.getText().toString().matches("")||dateRangeInput.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(), "Input(s) missing",Toast.LENGTH_SHORT).show();
                }
                else if(bool){
                    day = Integer.parseInt(dayInput.getText().toString());
                    month = Integer.parseInt(monthInput.getText().toString());
                    year = Integer.parseInt(yearInput.getText().toString());
                    dateRange = Integer.parseInt(dateRangeInput.getText().toString());

                    // the below code snippet is for the purpose of disallowing inputs beyond the date range present
                    if(dayInput.getText().toString().length()==1)
                        dayValue = "0"+dayInput.getText().toString();
                    else
                        dayValue = dayInput.getText().toString();
                    if(monthInput.getText().toString().length()==1)
                        monthValue = "0"+monthInput.getText().toString();
                    else
                        monthValue = monthInput.getText().toString();
                    dateValue = yearInput.getText().toString()+monthValue+ dayValue;


                    if (validateInput(day, month, year, dateRange, Integer.parseInt(dateValue))){
                        try{
                            if(mIsBound){
                                // call Service's API method dailyCash
                                dailyCashArray = mBalanceService.dailyCash(day, month, year, dateRange);
                            }
                            else
                                Log.i(TAG, "The service was not bound!");
                            } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        // if remaining entries are less than specified range
                        if(dailyCashArray == null){
                            Toast.makeText(getApplicationContext(),"Remaining entries are less than range!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // store contents of dailyCashArray to List in order to pass in intent
                            for (int i = 0; i < dailyCashArray.length; i++) {
                                dailyCashInstance = dailyCashArray[i];
                                dailyCashArrayList.add(dailyCashInstance);
                            }

                            // pass List via Intent to second activity
                            Intent intent = new Intent(App1MainActivity.this, ResultActivity.class);
                            intent.putParcelableArrayListExtra("DATA", dailyCashArrayList);
                            startActivity(intent);
                            dailyCashArrayList.clear();
                        }
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Invalid Date/ Data Entered", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Database NOT Created!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean validateInput(int dayTest, int monthTest, int yearTest, int dateRangeTest, int dateValue){

        if((monthTest == 4 || monthTest == 6 || monthTest == 9 || monthTest == 11)&&(dayTest == 31))
            return false;
        if((monthTest == 2) && (dayTest == 29 || dayTest == 30 || dayTest == 31))
            return false;
        if((dayTest>=1 && dayTest <=31) && (monthTest >=1 && monthTest <=12) && (yearTest == 2017 || yearTest ==2018)
                && (dateRangeTest>=1 && dateRangeTest <=30)&&(dateValue <= 20180302))
            return true;
        else
            return false;
    }

    @Override
    protected void onStart() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onStart();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBalanceService = BalanceServiceAIDL.Stub.asInterface(service);
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBalanceService = null;
            mIsBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mIsBound) {
            unbindService(this.mConnection);
        }
        super.onDestroy();
    }
}
