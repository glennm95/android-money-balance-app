package edu.uic.cs478.gmasca2.projec5app1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import edu.uic.cs478.gmasca2.project5app2common.DailyCash;

public class ResultActivity extends Activity {

    ArrayList<DailyCash> dailyCashArrayList;
    public static final String TAG = "Result Activity";
    ListView lv;
    CustomAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // get the list view
        lv = (ListView) findViewById(R.id.list_view);

        // get the received intent
        Intent intent = getIntent();
        dailyCashArrayList = new ArrayList<>();
        dailyCashArrayList = intent.getParcelableArrayListExtra("DATA");

        Log.i(TAG, "size = "+dailyCashArrayList.size());

        myAdapter = new CustomAdapter();
        lv.setAdapter(myAdapter);
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dailyCashArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "SetTextI18n"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.list_item_layout, null);

            TextView tv = (TextView) view.findViewById(R.id.list_item_text);

            tv.setText(dailyCashArrayList.get(i).mYear+"/"+dailyCashArrayList.get(i).mMonth+"/"+
                    dailyCashArrayList.get(i).mDay+", "+dailyCashArrayList.get(i).mDayOfWeek+": Balance = $"+
                    dailyCashArrayList.get(i).mCash+" million");
            return view;
        }
    }

}
