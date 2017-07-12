package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sr on 10.07.17.
 */

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
        //left blank on purpose, no one should ever create an instance of QueryUtils
    }

    //query the Guardian dataset and return a list of NewsItem objects
    public static List<NewsItem> fetchNewsData(String requestUrl) {
        //create url
        URL url = createUrl(requestUrl);

        //perform http request to concerning URL, receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<NewsItem> news = extractFeatureFromJson(jsonResponse);

        return news;
    }

    //creates a url from the GUARDIAN_QUERY_LINK
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    //make http request to the url and return a String as response
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //convert the input stream to a string which contains the JSONresponse from the server
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //return a list of NewsItem objects based on the parsing results
    private static List<NewsItem> extractFeatureFromJson(String newsJSON) {
        //if JSON string is empty or null -> return early
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        //creates an empty ArrayList to store the NewsItem objects in
        List<NewsItem> news = new ArrayList<NewsItem>();

        //try parsing the JSONrespose String
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            if (baseJsonResponse.has("response")){
                JSONObject resultObject = baseJsonResponse.getJSONObject("response");
                JSONArray newsArray = resultObject.getJSONArray("results");
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject currentNews = newsArray.getJSONObject(i);
                    String date = "";
                    if (currentNews.has("webPublicationDate")){
                        date = currentNews.getString("webPublicationDate");
                    }
                    String category = currentNews.getString("sectionName");
                    String title = currentNews.getString("webTitle");
                    String url = currentNews.getString("webUrl");
                    String author = "";
                    if (currentNews.has("tags")) {
                        JSONArray tags = currentNews.getJSONArray("tags");
                        for (int j = 0; j < tags.length(); j++) {
                            JSONObject authorInfo = tags.getJSONObject(j);
                            author = authorInfo.getString("webTitle");
                        }
                    }

                    NewsItem newsItem = new NewsItem(date, category, title, author, url);

                    news.add(newsItem);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        }

        //return a list with the NewsItem objects
        return news;
    }

}

