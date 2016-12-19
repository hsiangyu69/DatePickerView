package com.example.hsiangyuchen.customdatepiacker;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hsiangyuchen on 2016/12/16.
 */

public class DatePickerView extends RelativeLayout {
    //  DatePickerView Type
    public static final int DATEPICKERVIEW_TYPE_BIRTHDAYPICKER = 0;
    public static final int DATEPICKERVIEW_TYPE_CARDEXPIRYPICKER = 1;

    // Dummy data, in order for recyclerview can scroll the top and the bottom data to center
    private static final int DUMMY_DATA_COUNT = 2;
    private static final String DUMMY_DATA = "";

    // Constant
    private final static int MAX_MONTH_COUNT = 12;


    @IntDef({
            DATEPICKERVIEW_TYPE_BIRTHDAYPICKER,
            DATEPICKERVIEW_TYPE_CARDEXPIRYPICKER

    })
    public @interface DatePickerViewType {

    }

    // UI widgets
    private Button button_cancel;
    private Button button_done;
    private RecyclerView recyclerView_month;
    private RecyclerView recyclerView_date;
    private RecyclerView recyclerView_year;
    private RelativeLayout relativeLayout_date;
    private TextView textView_month;
    private TextView textView_date;
    private TextView textView_year;
    private TextView textView_slash;

    // Callback
    private DatePickerListener mDatePickerListener;

    // Adapter
    private DateAdapter dateAdapter;
    private MonthAdapter monthAdapter;
    private YearAdapter yearAdapter;

    // Date
    private int mDate;
    private int mYear;
    private int mMonth;

    // ViewType
    private int mDatePickerViewType;

    // Datesoure
    private ArrayList<String> monthArrayList = new ArrayList();
    private ArrayList<String> dateArrayList = new ArrayList<>();
    private ArrayList<String> yearArrayList = new ArrayList<>();

    /* ------------------------------ Interface */
    public interface DatePickerListener {
        void onDone();

        void onCancel();
    }

    public void setDatePickerListener(DatePickerListener datePickerListener) {
        mDatePickerListener = datePickerListener;
    }

    /* ------------------------------ Constructor */

    public DatePickerView(Context context, int mDatePickerViewType) {
        super(context);
        this.mDatePickerViewType = mDatePickerViewType;
        initViews();
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    /* ------------------------------ Views */
    private void initViews() {
        inflate(getContext(), R.layout.layout_datepicker, this);
        textView_date = (TextView) findViewById(R.id.textview_date);
        textView_month = (TextView) findViewById(R.id.textview_month);
        textView_year = (TextView) findViewById(R.id.textview_year);
        textView_slash = (TextView) findViewById(R.id.textview_slash);
        relativeLayout_date = (RelativeLayout) findViewById(R.id.relativelayout_date);
        recyclerView_month = (RecyclerView) findViewById(R.id.recyclerview_month);
        recyclerView_date = (RecyclerView) findViewById(R.id.recyclerview_date);
        recyclerView_year = (RecyclerView) findViewById(R.id.recyclerview_year);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_done = (Button) findViewById(R.id.button_done);
        button_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDatePickerListener != null) {
                    mDatePickerListener.onCancel();
                }
            }
        });
        button_done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDatePickerListener != null) {
                    mDatePickerListener.onDone();
                }
            }
        });
        LinearLayoutManager linearLayoutManagerMonth = new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManagerDay = new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManagerYear = new LinearLayoutManager(getContext());
        recyclerView_month.setLayoutManager(linearLayoutManagerMonth);
        recyclerView_date.setLayoutManager(linearLayoutManagerDay);
        recyclerView_year.setLayoutManager(linearLayoutManagerYear);
        dateAdapter = new DateAdapter(generateDateDataSource(mMonth), dpToPx(120));

        switch (mDatePickerViewType) {
            case DATEPICKERVIEW_TYPE_BIRTHDAYPICKER:
                yearAdapter = new YearAdapter(generateYearDataSourceForBirthdayDatePicker(0, 0, 0), dpToPx(120));
                monthAdapter = new MonthAdapter(generateMonthDataSourseForBirthdayDatePicker(), dpToPx(120));
                break;
            case DATEPICKERVIEW_TYPE_CARDEXPIRYPICKER:
                relativeLayout_date.setVisibility(GONE);
                textView_date.setVisibility(GONE);
                textView_slash.setVisibility(VISIBLE);
                yearAdapter = new YearAdapter(generateYearDataSourceForCardExpiryDatePicker(0, 0, 0), dpToPx(120));
                monthAdapter = new MonthAdapter(generateMonthDataSourseForCardExpiryDatePicker(), dpToPx(120));
                break;
            default:
                break;

        }

        recyclerView_month.setAdapter(monthAdapter);
        recyclerView_date.setAdapter(dateAdapter);
        recyclerView_year.setAdapter(yearAdapter);
        addRecyclerviewScrollListener();
    }


    /* ------------------------------ Add recyclerview scroll listener */

    private void addRecyclerviewScrollListener() {
        recyclerView_month.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    monthAdapter.setHighlightItem(getMonthMiddlePosition());
                    ((LinearLayoutManager) recyclerView_month.getLayoutManager()).scrollToPositionWithOffset(getMonthScrollPosition(), 0);
//                   month is calculate form 0, so need to -1, and when state is idle, the date datasoure need to refresh
                    dateAdapter.setDateSource(generateDateDataSource(getMonthMiddlePosition() - 1));
                    textView_month.setText(String.valueOf(monthArrayList.get(getMonthMiddlePosition())));

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        recyclerView_date.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    dateAdapter.setHighlightItem(getDateMiddlePostion());
                    ((LinearLayoutManager) recyclerView_date.getLayoutManager()).scrollToPositionWithOffset(getDateScrollPosition(), 0);
                    textView_date.setText(String.valueOf((dateArrayList.get(getDateMiddlePostion()))));

                }
            }
        });

        recyclerView_year.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    yearAdapter.setHighlightItem(getYearMiddlePostion());
                    ((LinearLayoutManager) recyclerView_year.getLayoutManager()).scrollToPositionWithOffset(getYearScrollPosition(), 0);
                    textView_year.setText(String.valueOf((yearArrayList.get(getYearMiddlePostion()))));

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /* ------------------------------ get MonthAdapter middle position */
    private int getMonthMiddlePosition() {
        return getMonthScrollPosition() + (monthAdapter.ITEM_NUM / 2);
    }

    /* ------------------------------ get MonthAdapter scroll position */
    private int getMonthScrollPosition() {
        return (int) (((double) recyclerView_month.computeVerticalScrollOffset()
                / (double) monthAdapter.getItemHeight()) + 0.5f);
    }

    /* ------------------------------ get DateAdapter middle position */

    private int getDateMiddlePostion() {
        return getDateScrollPosition() + (dateAdapter.ITEM_NUM / 2);
    }

    /* ------------------------------ get DateAdapter scroll position */

    private int getDateScrollPosition() {
        return (int) (((double) recyclerView_date.computeVerticalScrollOffset()
                / (double) dateAdapter.getItemHeight()) + 0.5f);
    }

     /* ------------------------------ get YearAdapter middle position */

    private int getYearMiddlePostion() {
        return getYearScrollPosition() + (dateAdapter.ITEM_NUM / 2);
    }

    /* ------------------------------ get YearAdapter scroll position */

    private int getYearScrollPosition() {
        return (int) (((double) recyclerView_year.computeVerticalScrollOffset()
                / (double) yearAdapter.getItemHeight()) + 0.5f);
    }


    /**
     * Generate Month Data Source For BirthdayDatePicker
     */

    public ArrayList<String> generateMonthDataSourseForBirthdayDatePicker() {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());

        String[] monthArray = dateFormatSymbols.getShortMonths();
        for (int i = 0; i < monthArray.length + DUMMY_DATA_COUNT; i++) {
            if (i == 0) {
                monthArrayList.add(i, DUMMY_DATA);
            } else if (i == monthArray.length + 1) {
                monthArrayList.add(i, DUMMY_DATA);
            } else {
                monthArrayList.add(i, monthArray[i - 1]);
            }
        }
        return monthArrayList;
    }

    /**
     * Generate Month Data Source For CardExpiry
     */

    public ArrayList<String> generateMonthDataSourseForCardExpiryDatePicker() {

        for (int i = 0; i < MAX_MONTH_COUNT + DUMMY_DATA_COUNT; i++) {
            if (i == 0) {
                monthArrayList.add(i, DUMMY_DATA);
            } else if (i == MAX_MONTH_COUNT + DUMMY_DATA_COUNT - 1) {
                monthArrayList.add(i, DUMMY_DATA);
            } else {
                if (String.valueOf(i).length() == 1) {
                    monthArrayList.add(i, String.valueOf("0") + String.valueOf(i));
                } else {
                    monthArrayList.add(i, String.valueOf(i));

                }


            }
        }
        return monthArrayList;
    }

    /**
     * Generate Date Data Sourece
     */

    public ArrayList<String> generateDateDataSource(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        dateArrayList = new ArrayList<>();
        int dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < dayOfMonth + DUMMY_DATA_COUNT; i++) {
            if (i == 0) {
                dateArrayList.add(i, DUMMY_DATA);
            } else if (i == dayOfMonth + 1) {
                dateArrayList.add(i, DUMMY_DATA);
            } else {
                dateArrayList.add(i, String.valueOf(i));
            }
        }


        return dateArrayList;
    }

    /**
     * Generate Year Data Source for BirthdayDatePicker
     * If selectUnixTime is 0  = Today,
     * The year range is from this year to this year - 150
     * TODO
     */

    public ArrayList<String> generateYearDataSourceForBirthdayDatePicker(long selectUnixTime, long minUnixTime, long maxUnixTIme) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        yearArrayList = new ArrayList<>();
        if (selectUnixTime == 0) {
            for (int i = 0; i < 150 + DUMMY_DATA_COUNT; i++) {
                if (i == 0) {
                    yearArrayList.add(DUMMY_DATA);
                } else if (i == 150 + 1) {
                    yearArrayList.add(DUMMY_DATA);
                } else {
                    yearArrayList.add(i, String.valueOf(calendar.get(Calendar.YEAR) - i + 1));
                }

            }

        }
        return yearArrayList;

    }

    /**
     * Generate Year Data Source for CardExpiryDatePicker
     * The year range from this year to this year + 10
     */

    public ArrayList<String> generateYearDataSourceForCardExpiryDatePicker(long selectUnixTime, long minUnixTime, long maxUnixTIme) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        yearArrayList = new ArrayList<>();
        if (selectUnixTime == 0) {
            for (int i = 0; i < 10 + DUMMY_DATA_COUNT; i++) {
                if (i == 0) {
                    yearArrayList.add(i, DUMMY_DATA);
                } else if (i == 10 + 1) {
                    yearArrayList.add(i, DUMMY_DATA);
                } else {
                    String tempYear = String.valueOf(String.valueOf(calendar.get(Calendar.YEAR) + i - 1));
                    yearArrayList.add(i, tempYear.substring(2));

                }
            }

        }
        return yearArrayList;

    }


    /**
     * Set time you want to display when start to open alert
     *
     * @param unixTime the target unix time you want
     */

    public void setUnixTime(long unixTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime);
        mDate = calendar.get(Calendar.DATE);
        mMonth = calendar.get(Calendar.MONTH);
        if (mDatePickerViewType == DATEPICKERVIEW_TYPE_BIRTHDAYPICKER) {
            mYear = calendar.get(Calendar.YEAR);
        } else {
            String tempYear = String.valueOf(calendar.get(Calendar.YEAR));
            mYear = Integer.parseInt(tempYear.substring(2));
        }
        setRecyclerviewScrollToSpecifyDate();
        setDisplayDate();

    }

    /**
     * Set recyclerview sroll to specify date at first
     */
    public void setRecyclerviewScrollToSpecifyDate() {
        ((LinearLayoutManager) recyclerView_date.getLayoutManager()).scrollToPositionWithOffset(mDate - 1, 0);
        dateAdapter.setHighlightItem(mDate);
        ((LinearLayoutManager) recyclerView_month.getLayoutManager()).scrollToPositionWithOffset(mMonth, 0);
        monthAdapter.setHighlightItem(mMonth + 1);
        int yearPos = yearArrayList.indexOf(String.valueOf(mYear));
        ((LinearLayoutManager) recyclerView_year.getLayoutManager()).scrollToPositionWithOffset(yearPos - 1, 0);
        yearAdapter.setHighlightItem(yearPos);

    }

    /**
     * Set textview_date, text_month, text_year to specify date at first
     */
    public void setDisplayDate() {
        textView_year.setText(String.valueOf(mYear));
        textView_month.setText(monthArrayList.get(mMonth + 1));
        textView_date.setText(String.valueOf(mDate));
    }

    /**
     * Get select date and convert it to unix time
     */
    public long getSelectDateUnixTime() {
        Calendar calendar = Calendar.getInstance();
        // TODO ask how to do is better
        if (mDatePickerViewType == DATEPICKERVIEW_TYPE_BIRTHDAYPICKER) {
            calendar.set(Calendar.YEAR, Integer.parseInt(yearArrayList.get(yearAdapter.getHighlightItem())));
        } else {
            calendar.set(Calendar.YEAR, Integer.parseInt(String.valueOf("20") + yearArrayList.get(yearAdapter.getHighlightItem())));
        }
        // month need to - 1
        calendar.set(Calendar.MONTH, monthAdapter.getHighlightItem() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, (dateAdapter.getHighlightItem()));
        long dateUnixTime = calendar.getTimeInMillis();
        return dateUnixTime;
    }


    /**
     * Convert dp to px
     *
     * @param dp the dp you want to convert (in this view we fixed 150dp)
     */

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


}
