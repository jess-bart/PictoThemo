package com.jessy_barthelemy.pictothemo.Fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetPicturesInfoTask;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.util.Calendar;

public class SearchFragment extends BaseFragment implements IAsyncApiObjectResponse {

    private Button search;
    private EditText user;
    private EditText theme;
    private EditText voteCount;
    private CheckBox potd;
    private DatePicker startingDate;
    private DatePicker endingDate;
    private ProgressBar searchProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        this.search = (Button)view.findViewById(R.id.search_action);
        this.user = (EditText)view.findViewById(R.id.search_user);
        this.theme = (EditText)view.findViewById(R.id.search_theme);
        this.voteCount = (EditText)view.findViewById(R.id.search_voteCount);
        this.potd = (CheckBox) view.findViewById(R.id.search_potd);
        this.startingDate = (DatePicker) view.findViewById(R.id.search_starting_date);
        this.endingDate = (DatePicker) view.findViewById(R.id.search_ending_date);
        this.searchProgress = (ProgressBar) view.findViewById(R.id.search_progress);

        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchFragment.this.search.setTextColor(ContextCompat.getColor(SearchFragment.this.getActivity(), android.R.color.transparent));
                SearchFragment.this.search.setEnabled(false);
                SearchFragment.this.searchProgress.setVisibility(View.VISIBLE);

                Calendar startingCalendar = SearchFragment.this.getDate(SearchFragment.this.startingDate);
                Calendar endingCalendar = SearchFragment.this.getDate(SearchFragment.this.endingDate);
                String user = SearchFragment.this.user.getText().toString();
                String theme = SearchFragment.this.theme.getText().toString();
                String voteCountStr = SearchFragment.this.voteCount.getText().toString();
                Integer voteCount = voteCountStr.isEmpty()? null : Integer.parseInt(voteCountStr);

                String flags = ApiHelper.FLAG_COMMENTS;
                if(SearchFragment.this.potd.isChecked())
                    flags += "|"+ApiHelper.FLAG_POTD;
                GetPicturesInfoTask getPicturesInfosTask = new GetPicturesInfoTask(startingCalendar, endingCalendar,
                                                            theme, user, voteCount, flags
                                                            , SearchFragment.this);
                getPicturesInfosTask.execute();
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        this.startingDate.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar date = SearchFragment.this.getDate(datePicker);
                SearchFragment.this.endingDate.setMinDate(0);
                SearchFragment.this.endingDate.setMinDate(date.getTimeInMillis());
            }
        });

        return view;
    }

    private Calendar getDate(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar;
    }

    @Override
    public void asyncTaskSuccess(Object response) {
        this.search.setEnabled(true);
        this.searchProgress.setVisibility(View.GONE);
        this.search.setTextColor(ContextCompat.getColor(this.getActivity(), android.R.color.white));

        super.asyncTaskSuccess(response);
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        this.search.setEnabled(true);
        super.asyncTaskFail(errorMessage);
    }
}
