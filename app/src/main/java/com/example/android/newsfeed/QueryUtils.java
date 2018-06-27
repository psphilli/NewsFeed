package com.example.android.newsfeed;

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

final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getName();
    private static final int TEN_SECONDS = 10000;
    private static final int FIFTEEN_SECONDS = 15000;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian News and return a list of {@link Article} objects.
     */
    public static List<Article> fetchNewsData(String requestUrl) {
        Log.i(LOG_TAG, "fetchNewsData");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and return a list of {@link Article}s
        return extractNewsFromJson(jsonResponse);
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Article> extractNewsFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles
        List<Article> newsItems = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON).getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of news articles.
            JSONArray resultArray = baseJsonResponse.getJSONArray("results");

            // For each article in the resultArray, create an {@link Article} object
            for (int i = 0; i < resultArray.length(); i++) {

                // Get a single article at position i within the list of results
                JSONObject currentArticle = resultArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String title = currentArticle.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String sectionName = currentArticle.getString("sectionName");

                // Extract the JSONArray associated with the key called "tags",
                // which represents a list of tags associated to the article.
                JSONArray tagArray = currentArticle.getJSONArray("tags");

                // Among the tags, find the first author and extract
                String author = null;
                for (int j = 0; j < tagArray.length(); j++) {
                    JSONObject tag = tagArray.getJSONObject(j);
                    String type = tag.getString("type");
                    if (type != null && type.equals("contributor")) {
                        author = tag.getString("webTitle");
                        break;
                    }
                }

                // Extract the value for the key called "webPublicationDate"
                String publicationDate = currentArticle.getString("webPublicationDate");

                // Extract the value for the key called "url"
                String url = currentArticle.getString("webUrl");

                // Create a new {@link Article} object with the title, publication date,
                // and url of the article.
                Article newsItem = new Article(title, sectionName, author, publicationDate, url);

                // Add the new {@link Article} to the list of newsItems.
                newsItems.add(newsItem);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news item JSON results", e);
        }

        // Return the list of newsItems
        return newsItems;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(TEN_SECONDS);
            urlConnection.setConnectTimeout(FIFTEEN_SECONDS);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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
}