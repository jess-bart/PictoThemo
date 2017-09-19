package com.jessy_barthelemy.pictothemo.Activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetPicturesInfoTask;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.util.Calendar;

public class SearchActivity extends BaseActivity implements IAsyncApiObjectResponse {

    private Button search;
    private EditText user;
    private EditText theme;
    private EditText voteCount;
    private CheckBox potd;
    private DatePicker startingDate;
    private DatePicker endingDate;
    private ProgressBar searchProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout contentFrameLayout = (RelativeLayout) findViewById(R.id.content_main);
        getLayoutInflater().inflate(R.layout.activity_search, contentFrameLayout);

        this.search = (Button)this.findViewById(R.id.search_action);
        this.user = (EditText)this.findViewById(R.id.search_user);
        this.theme = (EditText)this.findViewById(R.id.search_theme);
        this.voteCount = (EditText)this.findViewById(R.id.search_voteCount);
        this.potd = (CheckBox) this.findViewById(R.id.search_potd);
        this.startingDate = (DatePicker) this.findViewById(R.id.search_starting_date);
        this.endingDate = (DatePicker) this.findViewById(R.id.search_ending_date);
        this.searchProgress = (ProgressBar) this.findViewById(R.id.search_progress);

        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.this.search.setTextColor(ContextCompat.getColor(SearchActivity.this, android.R.color.transparent));
                SearchActivity.this.search.setEnabled(false);
                SearchActivity.this.searchProgress.setVisibility(View.VISIBLE);

                Calendar startingCalendar = SearchActivity.this.getDate(SearchActivity.this.startingDate);
                Calendar endingCalendar = SearchActivity.this.getDate(SearchActivity.this.endingDate);
                String user = SearchActivity.this.user.getText().toString();
                String theme = SearchActivity.this.theme.getText().toString();
                String voteCountStr = SearchActivity.this.voteCount.getText().toString();
                Integer voteCount = voteCountStr.isEmpty()? null : Integer.parseInt(voteCountStr);

                String flags = ApiHelper.FLAG_COMMENTS;
                if(SearchActivity.this.potd.isChecked())
                    flags += "|"+ApiHelper.FLAG_POTD;
                GetPicturesInfoTask getPicturesInfosTask = new GetPicturesInfoTask(startingCalendar, endingCalendar,
                                                            theme, user, voteCount, flags
                                                            , SearchActivity.this);
                getPicturesInfosTask.execute();
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        this.startingDate.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar date = SearchActivity.this.getDate(datePicker);
                SearchActivity.this.endingDate.setMinDate(0);
                SearchActivity.this.endingDate.setMinDate(date.getTimeInMillis());
            }
        });
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
        this.search.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        super.asyncTaskSuccess(response);
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        this.search.setEnabled(true);
        super.asyncTaskFail(errorMessage);
    }
}
