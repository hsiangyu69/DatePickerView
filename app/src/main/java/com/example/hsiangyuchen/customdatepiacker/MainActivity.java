package com.example.hsiangyuchen.customdatepiacker;

import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerView.DatePickerListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private View customDatePickerView;
    private AlertDialog alertDialog;
    private Button button;
    private DatePickerView datePickerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildAlertDialog();
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });

    }


    private void buildAlertDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, 4);
        calendar.set(Calendar.DAY_OF_MONTH, 22);

        datePickerView = new DatePickerView(this, DatePickerView.DATEPICKERVIEW_TYPE_CARDEXPIRYPICKER
                , System.currentTimeMillis(), 0, 0);
        alertDialog = new AlertDialog.Builder(this)
                .setView(datePickerView)
                .create();


    }


    @Override
    protected void onResume() {
        datePickerView.setDatePickerListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onDone() {
        long dateDateUnixTime = 0;

        dateDateUnixTime = datePickerView.getSelectDateUnixTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateDateUnixTime);

        Log.e(TAG, String.valueOf(calendar.get(Calendar.MONTH)));
        Log.e(TAG, String.valueOf(calendar.get(Calendar.YEAR)));
        Log.e(TAG, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    }

    @Override
    public void onCancel() {
        alertDialog.dismiss();
    }
}
