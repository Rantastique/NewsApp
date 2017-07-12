package com.example.android.newsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by sr on 10.07.17.
 */

public class NewsAdapter extends ArrayAdapter<NewsItem> {
    public static final String LOG_TAG = NewsAdapter.class.getSimpleName();
    private static final String DATE_SEPARATOR = "T";
    public ArrayList<NewsItem> news;

    //creates a new NewsAdapter object
    public NewsAdapter(Context context, List<NewsItem> news) {
        super(context, 0, news);
    }

    //inflates the ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //check if an existing view is being reused, otherwise inflate the view
        View newsView = convertView;
        if (newsView == null) {
            newsView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        //currently shown NewItem
        NewsItem currentNews = getItem(position);

        //get category TextView and set category
        TextView category = (TextView) newsView.findViewById(R.id.category);
        category.setText(currentNews.getCategory());

        //get title TextView set title
        TextView title = (TextView) newsView.findViewById(R.id.title);
        title.setText(currentNews.getTitle());

        //find author TextView
        //set author if existing, otherwise set the respective TextView's visibility to GONE
        TextView author = (TextView) newsView.findViewById(R.id.author);
        if (currentNews.getAuthor() != ""){
            author.setText(currentNews.getAuthor());
        }
        else {
            author.setVisibility(View.GONE);
        }

        //get the current NewsItem's date String
        String originalDate = currentNews.getDate();
        //find the date and time TextViews
        TextView dateView = (TextView) newsView.findViewById(R.id.date);
        TextView timeView = (TextView) newsView.findViewById(R.id.time);
        //if there is a publication date available set date and time on the TextViews
        if (originalDate != ""){
            dateView.setText(formatDate(originalDate));
            timeView.setText(formatTime(originalDate));
        }
        else {
            //display "Date unknown" and set the time TextView's visibility to GONE
            dateView.setText(R.string.date_unknown);
            timeView.setVisibility(View.GONE);
        }

        return newsView;
    }

    //I've got the idea for this method from a fellow student
    //I first used a String seperator method and String manipulation to display the
    //date and time. However, the code didn't look pretty, so I skipped through other student's
    //projects in the forum. This is her code:
    //https://github.com/AKBwebdev/NewsApp
    //I modified it for my use, comments are by me.

    public String formatDate(String date) {
        String dateFormatted = "";
        //gets the String's characters from index 0 to 10 (yyyy-MM-dd)
        String dateNew = date.substring(0, 10);

        //set input and output format
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyyy");
        //format the date data (or catch exception)
        try {
            Date dt = inputFormat.parse(dateNew);
            dateFormatted = newFormat.format(dt);
        } catch (ParseException pe) {
            Log.e(LOG_TAG, "Problem formatting the date");
        }

        return dateFormatted;
    }

    public String formatTime(String date) {

        String timeFormatted = "";
        //gets the String's characters from index 11 to 18 (HH:mm:ss)
        String dateNew = date.substring(11, 18);

        //set input and output format
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        //format the time data (or catch exception)
        try {
            Date dt = inputFormat.parse(dateNew);
            timeFormatted = newFormat.format(dt);
        } catch (ParseException pe) {
            Log.e(LOG_TAG, "Problem formatting the time");
        }

        return timeFormatted;
    }
}

