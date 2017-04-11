package com.example.hsiangyuchen.customdatepiacker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    /* ------------------------------ View */

    private void initView() {
        Button buttonShowBirthdayPicker = (Button) findViewById(R.id.button_show_birthday_picker);
        Button buttonShowCreditCardPicker = (Button) findViewById(R.id.button_show_credit_card_picker);
        buttonShowBirthdayPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(DatePickerView.VIEW_TYPE_BIRTHDAY);
            }
        });
        buttonShowCreditCardPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(DatePickerView.VIEW_TYPE_CARD_EXPIRY);
            }
        });
    }

    /**
     * Build and show alert dialog and set date picker as custom view
     *
     * @param dataPickerType the type want to show
     */

    private void showAlertDialog(int dataPickerType) {
        final DatePickerView datePickerView = new DatePickerView(this, dataPickerType
                , System.currentTimeMillis(), 0, 0);
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(datePickerView)
                .create();
        // Button Done
        Button button_done = datePickerView.getDoneButton();
        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long selectUnixTime = datePickerView.getSelectDateUnixTime();
                Log.e(TAG, String.valueOf(selectUnixTime));

            }
        });

        // Button Cancel
        Button button_cancel = datePickerView.getCancelButton();
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // Show Dialog
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
