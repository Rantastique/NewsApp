package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int NEWS_LOADER_ID = 1;

    private static final String GUARDIAN_QUERY_LINK = "http://content.guardianapis.com/search?q=debate&tag=politics/politics&from-date=2016-01-01&api-key=test&show-tags=contributor&order-by=newest";

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private NewsAdapter mAdapter;

    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find references for ListView, SwipeRefreshLayout and empty state
        ListView newsListView = (ListView) findViewById(R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty);
        newsListView.setEmptyView(mEmptyStateTextView);

        //new adapter that takes an empty list of NewsItem object as input
        mAdapter = new NewsAdapter(this, new ArrayList<NewsItem>());

        //set the adapter on the ListView
        newsListView.setAdapter(mAdapter);

        //sets an onRefreshListener to the swipeRefreshLayout so that the loader gets
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //if the device is conntected -> restart loader to show newest news
                if (isConnected()) {
                    getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                } else {
                    //if the device is not connected -> set the SwipeRefreshLayout to false, so that
                    //no loading spinner is shown anymore
                    //clear the adapter and show the empty state TextView
                    mSwipeRefreshLayout.setRefreshing(false);
                    mAdapter.clear();
                    mEmptyStateTextView.setText(R.string.no_internet);
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        //set an onItemClickListener on the ListView, which sends an intent to a web browser
        //to open a website with the actual news article
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //find the NewsItem that was clicked on
                NewsItem currentNews = mAdapter.getItem(position);

                //convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                //create a new intent to view the news URI
                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                //send the intent to launch a new activity
                startActivity(webIntent);
            }
        });

        //initialise the loader and fetch data of the device is connected
        if (isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            //display error if the device has no internet connection
            //stop showing the loading spinner and show error message
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet);
        }
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        //create a new loader for the given URL
        return new NewsLoader(this, GUARDIAN_QUERY_LINK);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> news) {
        //find loading spinner and hide it when loading is finished
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);
        //hide the SwipeRefreshLayout's loading spinner after reloading
        mSwipeRefreshLayout.setRefreshing(false);
        mEmptyStateTextView.setText(R.string.no_news);

        //clear the adapter from former data
        mAdapter.clear();

        //if there is a valid list of NewItems, then add them to the adapter's
        //data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    //private boolean that indicates whether there is internet connection or not
    private boolean isConnected() {
        //get ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //get status about whether there is connectivity or not
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        //returns true for when there is an internet connection and false for when there's none
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}