package com.example.hsiangyuchen.customdatepiacker;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hsiangyuchen.customdatepiacker.adapter.DatePickerAdapter;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class DatePickerView extends RelativeLayout {

    //  View Type
    public static final int VIEW_TYPE_BIRTHDAY = 0;
    public static final int VIEW_TYPE_CARD_EXPIRY = 1;
    private int mDatePickerViewType;

    // Dummy data, in order for RecyclerView can scroll the top and the bottom data to center
    private static final int DUMMY_DATA_COUNT = 2;
    private static final String DUMMY_DATA = "";

    // Constant
    private final static int MAX_MONTH_COUNT = 12;
    private final static int MAX_RANGE_BIRTHDAY_YEAR = 150;
    private final static int MAX_RANGE_CARD_EXPIRY_YEAR = 10;

    // UI Widget - Date List
    private RecyclerView recyclerView_year;
    private RecyclerView recyclerView_month;
    private RecyclerView recyclerView_date;

    // UI Widget - Display
    private TextView textView_year;
    private TextView textView_month;
    private TextView textView_date;

    // Data Source - Today
    private int todayYear;
    private int todayMonth;

    // Data Source - User Select Date
    private int selectYear;
    private int selectMonth;
    private int selectDate;

    // Data Source - Birthday Max
    private int birthdayMaxYear;
    private int birthdayMaxMonth;
    private int birthdayMaxDate;

    // Data Source - Birthday Min
    private int birthdayMinYear;
    private int birthdayMinMonth;
    private int birthdayMinDate;

    // Data Source - Credit Card Max
    private int creditCardMaxYear;

    // Data Source - Credit Card Min
    private int creditCardMinYear;
    private int creditCardMinMonth;

    // Generate month short name array (Jan, Feb, Mar....)
    DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
    String[] monthArray = dateFormatSymbols.getShortMonths();


    /* ------------------------------ Constructor */

    /**
     * @param context             UI context
     * @param mDatePickerViewType view type
     * @param selectUnixTime      user select date
     * @param maxUnixTime         date picker max date limit
     * @param minUnixTime         date picker min date limit
     */
    public DatePickerView(Context context,
                            int mDatePickerViewType,
                            long selectUnixTime,
                            long minUnixTime,
                            long maxUnixTime) {
        super(context);
        this.mDatePickerViewType = mDatePickerViewType;

        initDate(selectUnixTime, maxUnixTime, minUnixTime);
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

    /**
     * Initial date information
     *
     * @param selectUnixTime select unit time
     * @param maxUnixTime    max unit time
     * @param minUnixTime    min unit time
     */
    private void initDate(long selectUnixTime, long maxUnixTime, long minUnixTime) {

        // Init select date
        Calendar calendarSelect = Calendar.getInstance();
        calendarSelect.setTimeInMillis(selectUnixTime);
        selectDate = calendarSelect.get(Calendar.DAY_OF_MONTH);
        selectMonth = calendarSelect.get(Calendar.MONTH);
        selectYear = calendarSelect.get(Calendar.YEAR);

        // Init today
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int todayDate = calendar.get(Calendar.DATE);
        todayMonth = calendar.get(Calendar.MONTH);
        todayYear = calendar.get(Calendar.YEAR);

        // Init max date
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.setTimeInMillis(maxUnixTime);

        // Init min date
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.setTimeInMillis(minUnixTime);

        // Set date
        switch (mDatePickerViewType) {
            case VIEW_TYPE_BIRTHDAY:
                //  Set Min birthday date, if mMinUnixTime=0, birthdayMinDate=todayDate, birthdayMinMonth=todayMonth, birthdayMinYear= todayYear-150
                birthdayMinYear = minUnixTime == 0 ? todayYear - MAX_RANGE_BIRTHDAY_YEAR : calendarMin.get(Calendar.YEAR);
                birthdayMinMonth = minUnixTime == 0 ? todayMonth : calendarMin.get(Calendar.MONTH);
                birthdayMinDate = minUnixTime == 0 ? todayDate : calendarMin.get(Calendar.DAY_OF_MONTH);

                //  Set Max birthday date, if mMaxUnixTime=0, birthdayMaxDate, birthdayMaxMonth, birthdayMaxYear = today
                birthdayMaxYear = maxUnixTime == 0 ? todayYear : calendarMax.get((Calendar.YEAR));
                birthdayMaxMonth = maxUnixTime == 0 ? todayMonth : calendarMax.get(Calendar.MONTH);
                birthdayMaxDate = maxUnixTime == 0 ? todayDate : calendarMax.get(Calendar.DAY_OF_MONTH);
                break;

            case VIEW_TYPE_CARD_EXPIRY:
                // Set min credit card
                creditCardMinYear = minUnixTime == 0 ? todayYear : calendarMin.get(Calendar.YEAR);
                creditCardMinMonth = minUnixTime == 0 ? todayMonth : calendarMin.get(Calendar.MONTH);

                // Set Max credit card  , if mMaxUnixTime=0, maxCreditCard = creditCardMinYear + 10
                creditCardMaxYear = creditCardMinYear + MAX_RANGE_CARD_EXPIRY_YEAR;
                break;
        }
    }

    /* ------------------------------ Views */

    /**
     * Initial TS date picker view
     */
    private void initViews() {
        inflate(getContext(), R.layout.view_date_picker, this);

        // Display
        textView_year = (TextView) findViewById(R.id.textView_year);
        textView_month = (TextView) findViewById(R.id.textView_month);
        textView_date = (TextView) findViewById(R.id.textView_date);
        textView_date.setVisibility(mDatePickerViewType == VIEW_TYPE_BIRTHDAY ? VISIBLE : GONE);
        TextView textView_slash = (TextView) findViewById(R.id.textView_slash);
        textView_slash.setVisibility(mDatePickerViewType == VIEW_TYPE_BIRTHDAY ? GONE : VISIBLE);

        // List - Year
        recyclerView_year = (RecyclerView) findViewById(R.id.recyclerView_year);
        recyclerView_year.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_year.setAdapter(mDatePickerViewType == VIEW_TYPE_BIRTHDAY
                ? new DatePickerAdapter(generateYearDataSourceForBirthdayDatePicker(), dpToPx(120))
                : new DatePickerAdapter(generateYearDataSourceForCardExpiryDatePicker(), dpToPx(120)));
        recyclerView_year.addOnScrollListener(onScrollListener);

        // List - Month
        recyclerView_month = (RecyclerView) findViewById(R.id.recyclerView_month);
        recyclerView_month.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_month.setAdapter(mDatePickerViewType == VIEW_TYPE_BIRTHDAY
                ? new DatePickerAdapter(generateMonthDataSourceForBirthdayDatePicker(selectYear), dpToPx(120))
                : new DatePickerAdapter(generateMonthDataSourceForCardExpiryDatePicker(Integer.parseInt(getWeekYearFormat(selectYear))), dpToPx(120)));
        recyclerView_month.addOnScrollListener(onScrollListener);

        // List - Day
        RelativeLayout layout_date = (RelativeLayout) findViewById(R.id.layout_date);
        layout_date.setVisibility(mDatePickerViewType == VIEW_TYPE_BIRTHDAY ? VISIBLE : GONE);
        recyclerView_date = (RecyclerView) findViewById(R.id.recyclerView_date);
        recyclerView_date.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_date.setAdapter(new DatePickerAdapter(generateDayDataSource(selectYear, selectMonth), dpToPx(120)));
        recyclerView_date.addOnScrollListener(onScrollListener);

        setRecyclerViewScrollToSpecifyDate();
        setDisplayDate();
    }


    /**
     * Set RecyclerView scroll to specify date at first
     */
    public void setRecyclerViewScrollToSpecifyDate() {
        switch (mDatePickerViewType) {
            case VIEW_TYPE_BIRTHDAY:
                // year
                int yearPosition = ((DatePickerAdapter) recyclerView_year.getAdapter()).getDataSource().indexOf(String.valueOf(selectYear));
                ((LinearLayoutManager) recyclerView_year.getLayoutManager()).scrollToPositionWithOffset(yearPosition - 1, 0);
                ((DatePickerAdapter) recyclerView_year.getAdapter()).setHighlightItem(yearPosition);
                // month
                ((LinearLayoutManager) recyclerView_month.getLayoutManager()).scrollToPositionWithOffset(
                        ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().indexOf(monthArray[selectMonth]) - 1, 0);
                ((DatePickerAdapter) recyclerView_month.getAdapter()).setHighlightItem(
                        ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().indexOf(monthArray[selectMonth]));
                // date
                ((LinearLayoutManager) recyclerView_date.getLayoutManager()).scrollToPositionWithOffset(((DatePickerAdapter) recyclerView_date.getAdapter()).getDataSource().indexOf(String.valueOf(selectDate)) - 1, 0);
                ((DatePickerAdapter) recyclerView_date.getAdapter()).setHighlightItem(((DatePickerAdapter) recyclerView_date.getAdapter()).getDataSource().indexOf(String.valueOf(selectDate)));

                break;

            case VIEW_TYPE_CARD_EXPIRY:
                // The month data source is 01, 02, 03 ..., need to convert select
                String selectMonthStr = getMonthFormatForCardExpiry(selectMonth);
                int monthPosition = ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().indexOf(selectMonthStr);
                ((LinearLayoutManager) recyclerView_month.getLayoutManager()).scrollToPositionWithOffset(monthPosition - 1, 0);
                ((DatePickerAdapter) recyclerView_month.getAdapter()).setHighlightItem(monthPosition);
                // year
                int cardYearPosition = ((DatePickerAdapter) recyclerView_year.getAdapter()).getDataSource().indexOf(getWeekYearFormat(selectYear));
                ((LinearLayoutManager) recyclerView_year.getLayoutManager()).scrollToPositionWithOffset(cardYearPosition - 1, 0);
                ((DatePickerAdapter) recyclerView_year.getAdapter()).setHighlightItem(cardYearPosition);
                break;
        }
    }

    /**
     * Set textView_date, text_month, text_year to specify date at first
     */
    public void setDisplayDate() {
        switch (mDatePickerViewType) {
            case VIEW_TYPE_BIRTHDAY:
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, selectMonth);
                String monthShort = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
                textView_year.setText(String.valueOf(selectYear));
                textView_month.setText(monthShort);
                textView_date.setText(String.valueOf(selectDate));
                break;
            case VIEW_TYPE_CARD_EXPIRY:
                textView_year.setText(getWeekYearFormat(selectYear));
                textView_month.setText(getMonthFormatForCardExpiry(selectMonth));
                break;
        }
    }

    /* ------------------------------ Data Source - Year */

    /**
     * Generate Year Data Source for BirthdayDatePicker
     */
    @NonNull
    public List<String> generateYearDataSourceForBirthdayDatePicker() {
        List<String> yearList = new ArrayList<>();
        int dataCount = birthdayMaxYear - birthdayMinYear + 1;
        int finalCount = dataCount + DUMMY_DATA_COUNT;
        for (int i = 0; i < finalCount; i++) {
            yearList.add(i == 0 || i == finalCount - 1
                    ? DUMMY_DATA
                    : String.valueOf(birthdayMaxYear - (dataCount - i)));
        }
        return yearList;

    }

    /**
     * Generate Year Data Source for CardExpiryDatePicker
     * The year data source is 16,15,14....
     */
    @NonNull
    public List<String> generateYearDataSourceForCardExpiryDatePicker() {
        List<String> yearList = new ArrayList<>();
        int totalCount = creditCardMaxYear - creditCardMinYear + DUMMY_DATA_COUNT;
        for (int i = 0; i < totalCount; i++) {
            yearList.add(i == 0 || i == totalCount - 1
                    ? DUMMY_DATA
                    : getWeekYearFormat(creditCardMinYear + i - 1));
        }
        return yearList;
    }

    /* ------------------------------ Data Source - Month */

    /**
     * Generate Month Data Source For BirthdayDatePicker
     * The month data source is Jan, Feb, ....,Nov, Dec
     *
     * @param selectYear the year you select
     */
    @NonNull
    public List<String> generateMonthDataSourceForBirthdayDatePicker(int selectYear) {

        List<String> monthList = new ArrayList<>();

        // calucated month count
        int monthCount = MAX_MONTH_COUNT;
        if (selectYear == birthdayMaxYear) {
            monthCount = birthdayMaxMonth + 1;
        } else if (selectYear == birthdayMinYear) {
            monthCount = MAX_MONTH_COUNT;
        }

        // calucalute month start index
        int monthStartIndex = selectYear == birthdayMinYear ? birthdayMinMonth : 0;

        // all of month before todayMonth, for example todayMonth is 3, all of month is 01,02,03,04
        for (int i = monthStartIndex; i < monthCount + DUMMY_DATA_COUNT; i++) {
            monthList.add(i == monthStartIndex || i == monthCount + DUMMY_DATA_COUNT - 1
                    ? DUMMY_DATA
                    : monthArray[i - (DUMMY_DATA_COUNT / 2)]);
        }
        return monthList;
    }

    /**
     * Generate Month Data Source For CardExpiry
     * The month data source is 01, 02, 03 ...., 11, 12
     *
     * @param selectYear the year you select, and the format is week year 2016 -> 16
     */
    @NonNull
    public List<String> generateMonthDataSourceForCardExpiryDatePicker(int selectYear) {
        List<String> monthList = new ArrayList<>();
        int monthCount;
        // Need to check just in few months,
        if (selectYear == Integer.valueOf(getWeekYearFormat(creditCardMinYear))) {
            monthCount = (MAX_MONTH_COUNT - (creditCardMinMonth + 1)) + 1;
            for (int i = 0; i < monthCount + DUMMY_DATA_COUNT; i++) {
                if (i == 0 || i == monthCount + DUMMY_DATA_COUNT - 1) {
                    monthList.add(i, DUMMY_DATA);
                } else {
                    monthList.add(i, getMonthFormatForCardExpiry(creditCardMinMonth + i - 1));
                }
            }

        } else {
            for (int i = 0; i < MAX_MONTH_COUNT + DUMMY_DATA_COUNT; i++) {
                if (i == 0 || i == MAX_MONTH_COUNT + DUMMY_DATA_COUNT - 1) {
                    monthList.add(i, DUMMY_DATA);
                } else {
                    monthList.add(i, getMonthFormatForCardExpiry(i - 1));
                }
            }
        }
        return monthList;
    }

    /* ------------------------------ Data Source - Day */

    /**
     * Generate Date Data Sourece
     *
     * @param selectYear  now you select year
     * @param selectMonth now you select month
     */
    @NonNull
    public List<String> generateDayDataSource(int selectYear,
                                              int selectMonth) {
        List<String> dayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, selectMonth);
        calendar.set(Calendar.YEAR, selectYear);

        // Calcalate day of month count
        int dayOfMonthCount;
        if (selectYear == birthdayMaxYear && selectMonth == birthdayMaxMonth) {
            dayOfMonthCount = birthdayMaxDate;
        } else if (selectYear == birthdayMinYear && selectMonth == birthdayMinMonth) {
            dayOfMonthCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            // Fix for day of month over select month
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            dayOfMonthCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        // Calculate day start index
        int dayStartIndex = selectYear == birthdayMinYear && selectMonth == birthdayMinMonth
                ? birthdayMinDate
                : 1;
        for (int i = dayStartIndex; i <= dayOfMonthCount + DUMMY_DATA_COUNT; i++) {
            dayList.add(i == dayStartIndex || i == dayOfMonthCount + DUMMY_DATA_COUNT
                    ? DUMMY_DATA
                    : String.valueOf(i - (DUMMY_DATA_COUNT / 2)));

        }
        return dayList;
    }





    /* ------------------------------ Add RecyclerView scroll listener */

    /**
     * Scroll listener
     */
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                switch (recyclerView.getId()) {
                    // Year
                    case R.id.recyclerView_year:
                        ((DatePickerAdapter) recyclerView_year.getAdapter()).setHighlightItem(getYearMiddlePosition());
                        ((LinearLayoutManager) recyclerView_year.getLayoutManager()).scrollToPositionWithOffset(getYearScrollPosition(), 0);
                        switch (mDatePickerViewType) {
                            case VIEW_TYPE_BIRTHDAY:
                                // Set month data source
                                ((DatePickerAdapter) recyclerView_month.getAdapter()).setDateSource(generateMonthDataSourceForBirthdayDatePicker(
                                        Integer.parseInt(((DatePickerAdapter) recyclerView_year.getAdapter()).getDataSource().get(getYearMiddlePosition()))));

                                String nowSelectMonthForBirthday = textView_month.getText().toString();

                                // Because the traveller type , the month data source will changed
                                // For example, if the month data source is start from Mar which is smaller than the data source is start from Jan
                                // Or now you select the month Jan, but the month data source didn't have this data.
                                // Thus,  nowSelectMonthForBirthday need to set the start month of this data source.
                                if (((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().size() - DUMMY_DATA_COUNT < getMonthMiddlePosition()
                                        || ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().indexOf(nowSelectMonthForBirthday) == -1) {
                                    String startMonth = ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().get(1);
                                    nowSelectMonthForBirthday = startMonth;
                                }

                                // Convert short month type to integer type
                                int nowMonth = Arrays.asList(monthArray).indexOf(nowSelectMonthForBirthday);

                                // Refresh month position
                                refreshMonthMiddlePositionForBirthdayDatePicker(nowSelectMonthForBirthday);

                                // Set date data source
                                ((DatePickerAdapter) recyclerView_date.getAdapter()).setDateSource(generateDayDataSource(Integer.parseInt(((DatePickerAdapter) recyclerView_year.getAdapter()).getDataSource().get(getYearMiddlePosition())), nowMonth));

                                // Rfresh date position
                                refreshDateMiddlePosition();
                                textView_year.setText(((DatePickerAdapter) recyclerView_year.getAdapter()).getHighlightItem());
                                break;

                            case VIEW_TYPE_CARD_EXPIRY:
                                String nowSelectMonth = ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().get(getMonthMiddlePosition());
                                ((DatePickerAdapter) recyclerView_month.getAdapter()).setDateSource(
                                        generateMonthDataSourceForCardExpiryDatePicker(Integer.parseInt(((DatePickerAdapter) recyclerView_year.getAdapter()).getDataSource().get(getYearMiddlePosition()))));
                                refreshMonthMiddlePositionForCardExpiryDatePicker(nowSelectMonth);
                                textView_year.setText(((DatePickerAdapter) recyclerView_year.getAdapter()).getHighlightItem());
                                break;
                        }
                        break;

                    // Month
                    case R.id.recyclerView_month:
                        ((DatePickerAdapter) recyclerView_month.getAdapter()).setHighlightItem(getMonthMiddlePosition());
                        ((LinearLayoutManager) recyclerView_month.getLayoutManager()).scrollToPositionWithOffset(getMonthScrollPosition(), 0);
                        // Month is calculated form 0, so need to -1, and when state is idle, the date data source need to refresh
                        String monthStr = ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().get(getMonthMiddlePosition());
                        int nowMonth = Arrays.asList(monthArray).indexOf(monthStr);
                        ((DatePickerAdapter) recyclerView_date.getAdapter()).setDateSource(generateDayDataSource(
                                Integer.parseInt(((DatePickerAdapter) recyclerView_year.getAdapter()).getDataSource().get(getYearMiddlePosition())), nowMonth));
                        refreshDateMiddlePosition();
                        textView_month.setText(((DatePickerAdapter) recyclerView_month.getAdapter()).getHighlightItem());
                        break;

                    // Day
                    case R.id.recyclerView_date:
                        ((DatePickerAdapter) recyclerView_date.getAdapter()).setHighlightItem(getDateMiddlePosition());
                        ((LinearLayoutManager) recyclerView_date.getLayoutManager()).scrollToPositionWithOffset(getDateScrollPosition(), 0);
                        textView_date.setText(((DatePickerAdapter) recyclerView_date.getAdapter()).getHighlightItem());

                        break;
                }
            }
        }
    };

    /* ------------------------------ Get middle position */

    private int getYearMiddlePosition() {
        return getYearScrollPosition() + (DatePickerAdapter.ITEM_NUM / 2);
    }

    private int getMonthMiddlePosition() {
        return getMonthScrollPosition() + (DatePickerAdapter.ITEM_NUM / 2);
    }

    private int getDateMiddlePosition() {
        return getDateScrollPosition() + (DatePickerAdapter.ITEM_NUM / 2);
    }

    /* ------------------------------ Get scroll position */

    private int getYearScrollPosition() {
        return (int) (((double) recyclerView_year.computeVerticalScrollOffset()
                / (double) ((DatePickerAdapter) recyclerView_year.getAdapter()).getItemHeight()) + 0.5f);
    }

    private int getMonthScrollPosition() {
        return (int) (((double) recyclerView_month.computeVerticalScrollOffset()
                / (double) ((DatePickerAdapter) recyclerView_month.getAdapter()).getItemHeight()) + 0.5f);
    }

    private int getDateScrollPosition() {
        return (int) (((double) recyclerView_date.computeVerticalScrollOffset()
                / (double) ((DatePickerAdapter) recyclerView_date.getAdapter()).getItemHeight()) + 0.5f);
    }



    /* ------------------------------ Calculation */

    /**
     * Get select date and convert it to unix time
     */
    public long getSelectDateUnixTime() {
        Calendar calendar = Calendar.getInstance();

        switch (mDatePickerViewType) {
            case VIEW_TYPE_BIRTHDAY:
                String targetDateString = (((DatePickerAdapter) recyclerView_year.getAdapter()).getHighlightItem()
                        + ((DatePickerAdapter) recyclerView_month.getAdapter()).getHighlightItem()
                        + ((DatePickerAdapter) recyclerView_date.getAdapter()).getHighlightItem());
                long unixTime = getUnixTime(targetDateString, "yyyyMMMd");
                if (unixTime == 0) {
                    calendar.set(Calendar.YEAR, birthdayMaxYear);
                    calendar.set(Calendar.MONTH, birthdayMaxMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, birthdayMaxDate);
                } else {
                    calendar.setTimeInMillis(unixTime);
                }
                break;

            case VIEW_TYPE_CARD_EXPIRY:
                String targetDateStringForCard = ((DatePickerAdapter) recyclerView_year.getAdapter()).getHighlightItem()
                        + ((DatePickerAdapter) recyclerView_month.getAdapter()).getHighlightItem();
                long unixTimeForCard = getUnixTime(targetDateStringForCard, "yyMM");
                if (unixTimeForCard == 0) {
                    calendar.set(Calendar.YEAR, creditCardMinYear);
                    calendar.set(Calendar.MONTH, creditCardMinMonth);
                } else {
                    calendar.setTimeInMillis(unixTimeForCard);
                }
                break;

        }
        return calendar.getTimeInMillis();
    }

    /**
     * Refresh month middle position for birthday date picker
     * When year update, the month need to refresh
     * For example, if you now select 3/Feb/2016 , when you scroll year to 2017 (the month just have Jan),
     * the new monthList didn't has the month Jan, need to scroll to todayMonth
     */
    public void refreshMonthMiddlePositionForBirthdayDatePicker(String nowSelectMonth) {
        int indexMonthPosition = ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().indexOf(nowSelectMonth);
        ((LinearLayoutManager) recyclerView_month.getLayoutManager()).scrollToPositionWithOffset(indexMonthPosition - 1, 0);
        ((DatePickerAdapter) recyclerView_month.getAdapter()).setHighlightItem(indexMonthPosition);
        textView_month.setText(nowSelectMonth);
    }


    /**
     * Refresh date middle position
     * When month or year update, the date need to refresh
     * For example, November has 30 days, and October has 31 days, thus the date data source will change
     * and RecyclerView_date need to scroll to bottom, and highlight the last date.
     */
    public void refreshDateMiddlePosition() {
        int nowDateMiddlePosition = getDateMiddlePosition();
        List<String> dataSource = ((DatePickerAdapter) recyclerView_date.getAdapter()).getDataSource();
        if (nowDateMiddlePosition > dataSource.size() - DUMMY_DATA_COUNT) {
            recyclerView_date.scrollToPosition(dataSource.size() - 1);
            ((DatePickerAdapter) recyclerView_date.getAdapter()).setHighlightItem(dataSource.size() - DUMMY_DATA_COUNT);
            nowDateMiddlePosition = dataSource.size() - DUMMY_DATA_COUNT;
            textView_date.setText(String.valueOf(nowDateMiddlePosition));

        } else {
            ((DatePickerAdapter) recyclerView_date.getAdapter()).setHighlightItem(nowDateMiddlePosition);
        }
    }


    /**
     * Refresh month middle position for card expiry date picker
     * When year update, the month need to refresh
     * For example, if you now select 04/17 , when you scroll year to 16 (the month just have December),
     * the new monthList didn't has the month 04, need to scroll to creditCardMinMonth
     */
    public void refreshMonthMiddlePositionForCardExpiryDatePicker(String nowSelectMonth) {
        int monthPosition = ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().indexOf(nowSelectMonth);
        //  Can't find nowselectmonth or nowselectMonth is dummy data
        if (monthPosition != -1 && monthPosition != 0 && monthPosition != ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().size() - 1) {
            ((LinearLayoutManager) recyclerView_month.getLayoutManager()).scrollToPositionWithOffset(monthPosition - 1, 0);
            ((DatePickerAdapter) recyclerView_month.getAdapter()).setHighlightItem(monthPosition);

        } else {
            // monthList is 01, 02, 03 ..., need to convert selectMonth format
            String todayMonthStr = getMonthFormatForCardExpiry(creditCardMinMonth);
            int todayMonthPosition = ((DatePickerAdapter) recyclerView_month.getAdapter()).getDataSource().indexOf(todayMonthStr);
            recyclerView_month.scrollToPosition(todayMonthPosition);
            ((DatePickerAdapter) recyclerView_month.getAdapter()).setHighlightItem(todayMonthPosition);
            textView_month.setText(todayMonthStr);
        }
    }

    /**
     * Convert year to week year, for example 2016 -> 16
     *
     * @param year, for example 2016
     */
    public String getWeekYearFormat(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy", Locale.ENGLISH); // Just the year, with 2 digits
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * Convert monthFormat for CardExpiry
     * In order the CardExpiryDatePickerType's month format is 01,02,03.......10,11,12
     *
     * @param month, for example 0,1..10,11
     */
    public String getMonthFormatForCardExpiry(int month) {
        return (month + 1) / 10 == 0
                ? String.valueOf(0) + String.valueOf(month + 1)
                : String.valueOf(month + 1);
    }

    /**
     * Convert dp to px
     *
     * @param dp the dp you want to convert (in this view we fixed 150dp)
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Convert Date string to unix time
     *
     * @param targetDateString the target date String you want to convert
     * @param pattern          the date String pattern, e.g. yyyy/MM/dd
     */
    public long getUnixTime(@NonNull String targetDateString,
                            @NonNull String pattern) {
        if (TextUtils.isEmpty(targetDateString) || TextUtils.isEmpty(pattern)) {
            return 0;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);

        // Start format
        long unixTime = 0;
        try {
            Date date = simpleDateFormat.parse(targetDateString);
            unixTime = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return unixTime;
    }
}
