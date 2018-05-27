package com.aimatus.popularmovies.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.aimatus.popularmovies.BuildConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Util for build URLs, get HTTP responses and check Internet connectivity.
 *
 * @author Abraham Matus
 * @author Udacity
 */
public class NetworkUtils {

    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String POPULAR_MOVIES_URL = "http://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED_MOVIES_URL = "http://api.themoviedb.org/3/movie/top_rated";
    private static final String API_KEY_PARAM = "api_key";
    private static final String API_KEY_ERROR_MESSAGE = "Please, provide an API Key in order to consume TheMovieDB API.";
    private static final String TESTING_URL = "http://m.google.com";
    private static final String INVALID_URL_ERROR_MESSAGE = "Invalid URL provided.";

    // TODO Use your own API Key here.
    private static final String APY_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;

    /**
     * Builds the URL used to fetch popular movies from TheMovieDB.
     * Based on Udacity's Sunshine exercises.
     *
     * @return The URL to use to fetch popular movies.
     */
    public static URL getPopularMoviesUrl() {

        if (APY_KEY.isEmpty()) {
            Log.e(NetworkUtils.class.getSimpleName(), API_KEY_ERROR_MESSAGE);
        }

        Uri builtUri = Uri.parse(POPULAR_MOVIES_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, APY_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to fetch top rated movies from TheMovieDB.
     * Based on Udacity's Sunshine exercises.
     *
     * @return The URL to use to fetch popular movies.
     */
    public static URL getTopRatedMoviesUrl() {

        Uri builtUri = Uri.parse(TOP_RATED_MOVIES_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, APY_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to fetch movie videos from TheMovieDB.
     * Based on Udacity's Sunshine exercises.
     *
     * @return The URL to use to fetch popular movies.
     */
    public static URL getMovieVideosUrl(int movieId) {

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + movieId + "/videos").buildUpon()
                .appendQueryParameter(API_KEY_PARAM, APY_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
            System.out.println(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to fetch movie reviews from TheMovieDB.
     * Based on Udacity's Sunshine exercises.
     *
     * @return The URL to use to fetch popular movies.
     */
    public static URL getMovieReviewsUrl(int movieId) {

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + movieId + "/reviews").buildUpon()
                .appendQueryParameter(API_KEY_PARAM, APY_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
            System.out.println(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response. Taken from Udacity's Sunshine exercises.
     *
     * @param url The URL to fetch the HTTP response from.
     *
     * @return The contents of the HTTP response.
     *
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            Log.e(NetworkUtils.class.getSimpleName(), INVALID_URL_ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Utility to check if the device is connected to Internet based on the combination of methods
     * exposed in: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     *
     * @param activity parent activity which provides the ConnectivityManager.
     *
     * @return state of the validation.
     */
    public static boolean hasConnectivity(Activity activity) {

        // If the device is nos connected to a network. E.g. WiFi, LTE.
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            return false;
        }

        // If the device is connected to a hotspot, but doesn't have Internet access.
        final int timeoutMilliseconds = 5000;
        final String connectionTestingUrl = TESTING_URL;

        try {
            URLConnection urlConnection = new URL(connectionTestingUrl).openConnection();
            urlConnection.setConnectTimeout(timeoutMilliseconds);
            urlConnection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

}
