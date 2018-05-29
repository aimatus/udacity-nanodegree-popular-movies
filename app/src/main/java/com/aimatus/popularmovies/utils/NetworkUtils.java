package com.aimatus.popularmovies.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
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

public class NetworkUtils {

    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String POPULAR_MOVIES_URL = "http://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED_MOVIES_URL = "http://api.themoviedb.org/3/movie/top_rated";
    private static final String API_KEY_PARAM = "api_key";
    private static final String TESTING_URL = "http://m.google.com";
    private static final String INVALID_URL_ERROR_MESSAGE = "Invalid URL provided.";
    private static final String APY_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;

    public static URL getPopularMoviesUrl() {
        return getUrl(POPULAR_MOVIES_URL);
    }

    public static URL getTopRatedMoviesUrl() {
        return getUrl(TOP_RATED_MOVIES_URL);
    }

    public static URL getMovieVideosUrl(int movieId) {
        return getUrl(MOVIE_BASE_URL + movieId + "/videos");
    }

    public static URL getMovieReviewsUrl(int movieId) {
        return getUrl(MOVIE_BASE_URL + movieId + "/reviews");
    }

    @Nullable
    private static URL getUrl(String popularMoviesUrl) {
        Uri builtUri = Uri.parse(popularMoviesUrl).buildUpon().appendQueryParameter(API_KEY_PARAM, APY_KEY).build();
        try {
            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            return getResponseString(urlConnection);
        } catch (FileNotFoundException e) {
            Log.e(NetworkUtils.class.getSimpleName(), INVALID_URL_ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    @Nullable
    private static String getResponseString(HttpURLConnection urlConnection) throws IOException {
        InputStream in = urlConnection.getInputStream();
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter("\\A");
        boolean hasInput = scanner.hasNext();
        if (hasInput) {
            return scanner.next();
        } else {
            return null;
        }
    }

    public static boolean hasNoConnectivity(Activity activity) {
        return !isDeviceConnectedToNetwork(activity) || !deviceHasInternetAccess();
    }

    private static boolean isDeviceConnectedToNetwork(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static boolean deviceHasInternetAccess() {
        final int timeoutMilliseconds = 5000;
        try {
            URLConnection urlConnection = new URL(TESTING_URL).openConnection();
            urlConnection.setConnectTimeout(timeoutMilliseconds);
            urlConnection.connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
