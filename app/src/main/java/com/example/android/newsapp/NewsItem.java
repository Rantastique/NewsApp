package com.example.android.newsapp;

/**
 * Created by sr on 10.07.17.
 */

public class NewsItem {
    private String mDate;
    private String mCategory;
    private String mTitle;
    private String mAuthor;
    private String mUrl;

    //constructor
    public NewsItem(String date, String category, String title, String author, String url){
        mDate = date;
        mCategory = category;
        mTitle = title;
        mAuthor = author;
        mUrl = url;
    }

    //get methods
    public String getDate(){
        return mDate;
    }

    public String getCategory(){
        return mCategory;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public String getUrl(){
        return mUrl;
    }





}
