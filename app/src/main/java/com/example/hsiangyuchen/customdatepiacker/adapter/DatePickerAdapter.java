package com.example.hsiangyuchen.customdatepiacker.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hsiangyuchen.customdatepiacker.R;

import java.util.List;



public class DatePickerAdapter extends RecyclerView.Adapter<DatePickerViewHolder> {

    // Constants
    public static final int ITEM_NUM = 3;

    // Flag
    private int mHighlightItemPosition = 1;

    // UI layout params
    private int recyclerViewHeight;

    // Data Source
    private List<String> dataSource;

    /* ------------------------------ Constructor */

    public DatePickerAdapter(@NonNull List<String> dataSource,
                             int recyclerViewHeight) {
        this.dataSource = dataSource;
        this.recyclerViewHeight = recyclerViewHeight;
    }

    /* ------------------------------ Override */

    @Override
    public DatePickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_date_picker, parent, false);
        DatePickerViewHolder datePickerViewHolder = new DatePickerViewHolder(view);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int) getItemHeight();
        return datePickerViewHolder;
    }

    @Override
    public void onBindViewHolder(DatePickerViewHolder holder, int position) {
        holder.setUp(dataSource.get(position), isHighlight(position));
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    /* ------------------------------ Main Methods */

    /**
     * Get each item height
     */
    public float getItemHeight() {

        return recyclerViewHeight / ITEM_NUM;
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
        for (int i = position - offset; i <= position + offset; ++i) {
            notifyItemChanged(i);
        }
    }

    /**
     * Get highlight item
     */
    public String getHighlightItem() {
        return dataSource.get(mHighlightItemPosition);
    }


    /* ------------------------------ Getter & Setter */

    /**
     * Set date date source, because the select month will change
     */
    public void setDateSource(@NonNull List<String> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    @NonNull
    public List<String> getDataSource() {
        return dataSource;
    }
}
