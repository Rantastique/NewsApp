package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by sr on 11.07.17.
 */

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {
    //query url
    private String mUrl;
    //constructor
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading () {
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground(){
        if (mUrl == null) {
            return null;
        }

        //perform network request, parse the response, extract list of NewsItems
        List<NewsItem> news = QueryUtils.fetchNewsData(mUrl);
        return news;
    }

}
