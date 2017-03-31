package com.example.hsiangyuchen.customdatepiacker.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.example.hsiangyuchen.customdatepiacker.R;


class DatePickerViewHolder extends RecyclerView.ViewHolder {

    // UI Widget
    private TextView textView_display;

    /* ------------------------------ Constructor */

    DatePickerViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_display = (TextView) itemView.findViewById(R.id.textView_date);
    }

    /* ------------------------------ Main Method */

    /**
     * @param displayString the String use for display
     * @param highLight     indicate is high light or not
     */
    public void setUp(@NonNull String displayString,
                      boolean highLight) {
        textView_display.setText(displayString);
        textView_display.setTextColor(ContextCompat.getColor(itemView.getContext(),
                highLight ? R.color.colorBlack : R.color.colorGray));
    }
}
