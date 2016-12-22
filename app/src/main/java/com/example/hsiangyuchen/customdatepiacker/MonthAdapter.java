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

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {

    // Constants
    public static final int ITEM_NUM = 3;

    // UI layoutparams
    private int recyclerviewHeight;

    // Flag
    private int mHighlightItemPosition = 1;

    // Data Source
    private ArrayList<String> dataSource;

    /* ------------------------------ ViewHolder */
    public class MonthViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_month;

        public MonthViewHolder(View itemView) {
            super(itemView);
            textView_month = (TextView) itemView.findViewById(R.id.textview_date);
        }
    }

    /* ------------------------------ Constructor */
    public MonthAdapter(ArrayList<String> dataSource, int recyclerviewHeight) {
        this.dataSource = dataSource;
        this.recyclerviewHeight = recyclerviewHeight;
    }


    /* ------------------------------ Override */
    public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cell_textview, parent, false);
        MonthViewHolder monthViewHolder = new MonthViewHolder(itemView);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.height = (int) getItemHeight();
        return monthViewHolder;
    }

    @Override
    public void onBindViewHolder(MonthViewHolder holder, int position) {

        holder.textView_month.setText(dataSource.get(position));
        if (isHighlight(position)) {
            holder.textView_month.setTextColor(ContextCompat.getColor(holder.textView_month.getContext(), R.color.colorBlack));
        } else {
            holder.textView_month.setTextColor(ContextCompat.getColor(holder.textView_month.getContext(), R.color.colorGray));
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
