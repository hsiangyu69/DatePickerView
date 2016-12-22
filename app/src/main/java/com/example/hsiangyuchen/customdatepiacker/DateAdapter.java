package com.example.hsiangyuchen.customdatepiacker;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hsiangyuchen on 2016/12/14.
 */

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DayViewHolder> {

    // Constants
    public static int ITEM_NUM = 3;

    // Date source
    private ArrayList<String> dataSource;

    // UI layoutparams
    private int recyclerviewHeight;

    // Flag
    private int mHighlightItemPosition = 1;

    /* ------------------------------ ViewHolder */
    public class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_date;

        public DayViewHolder(View itemView) {
            super(itemView);
            textView_date = (TextView) itemView.findViewById(R.id.textview_date);

        }
    }

    /* ------------------------------ Constructor */
    public DateAdapter(ArrayList<String> dataSource, int recyclerviewHeight) {
        this.dataSource = dataSource;
        this.recyclerviewHeight = recyclerviewHeight;
    }

    /* ------------------------------ Override */
    @Override
    public DateAdapter.DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cell_textview, parent, false);
        DayViewHolder dayViewHolder = new DayViewHolder(itemView);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.height = (int) getItemHeight();
        return dayViewHolder;
    }

    @Override
    public void onBindViewHolder(DateAdapter.DayViewHolder holder, int position) {
        holder.textView_date.setText(dataSource.get(position));
        if (isHighlight(position)) {
            holder.textView_date.setTextColor(ContextCompat.getColor(holder.textView_date.getContext(), R.color.colorBlack));
        } else {
            holder.textView_date.setTextColor(ContextCompat.getColor(holder.textView_date.getContext(), R.color.colorGray));
        }
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }


    /**
     * Get each item height
     */
    public float getItemHeight() {
        return recyclerviewHeight / ITEM_NUM;
    }

    /**
     * Decide is highlight item (in the center) or not
     */
    private boolean isHighlight(int position) {
        return mHighlightItemPosition == position;
    }

    /**
     * Set the highlight item
     */
    public void setHighlightItem(int position) {
        mHighlightItemPosition = position;
        int offset = ITEM_NUM / 2;
        for (int i = position - offset; i <= position + offset; ++i)
            notifyItemChanged(i);
    }

    /**
     * Set date date soure, because the select month will change
     */
    public void setDateSource(ArrayList<String> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    /**
     * get highlight item postion
     */
    public String getHighlightItem() {
        return dataSource.get(mHighlightItemPosition);
    }
}
