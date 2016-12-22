package com.example.hsiangyuchen.customdatepiacker;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hsiangyuchen on 2016/12/16.
 */

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.YearViewHolder> {

    // Constants
    public static final int ITEM_NUM = 3;

    // UI layoutparams
    private int recyclerviewHeight;

    // Flag
    private int mHighlightItemPosition = 1;

    // Date source
    private ArrayList<String> dataSource;

    /* ------------------------------ ViewHolder */
    public class YearViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public YearViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textview_date);
        }
    }

    /* ------------------------------ Constructor */
    public YearAdapter(ArrayList<String> dataSource, int recyclerviewHeight) {
        this.dataSource = dataSource;
        this.recyclerviewHeight = recyclerviewHeight;
    }

    /* ------------------------------ Override */
    @Override
    public YearAdapter.YearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cell_textview, parent, false);
        YearViewHolder yearViewHolder = new YearViewHolder(itemView);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.height = (int) getItemHeight();
        return yearViewHolder;
    }

    @Override
    public void onBindViewHolder(YearAdapter.YearViewHolder holder, int position) {
        holder.textView.setText(dataSource.get(position));
        if (isHighlight(position)) {
            holder.textView.setTextColor(ContextCompat.getColor(holder.textView.getContext(), R.color.colorBlack));
        } else {
            holder.textView.setTextColor(ContextCompat.getColor(holder.textView.getContext(), R.color.colorGray));
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
     * get highlight item postion
     */
    public String getHighlightItem() {
        return dataSource.get(mHighlightItemPosition);
    }


}
