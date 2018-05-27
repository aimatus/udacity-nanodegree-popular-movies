package com.aimatus.popularmovies.movie.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.aimatus.popularmovies.movie.PopularMovie;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access object class for popular mvoies.
 *
 * @author Abraham Matus
 */
public class PopularMovieDao {

    private Context context;
    private Gson gson;

    public PopularMovieDao(Context context) {
        this.context = context;
        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(context);
        gson = new Gson();
    }

    public List<PopularMovie> getFavoriteMovies() {

        Gson gson = new Gson();

        Cursor cursor = context.getContentResolver()
                .query(PopularMoviesContract.MovieEntry.CONTENT_URI,
                        null, null, null,
                        PopularMoviesContract.MovieEntry.COLUMN_TIMESTAMP);

        List<PopularMovie> movies = new ArrayList<>();

        while (cursor.moveToNext()) {
            String movieJson = cursor.getString(
                    cursor.getColumnIndex(PopularMoviesContract.MovieEntry.COLUMN_JSON_MOVIE));
            PopularMovie movie = gson.fromJson(movieJson, PopularMovie.class);
            movies.add(movie);
        }

        cursor.close();

        return movies;
    }

    public Uri addMovieToFavorites(PopularMovie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PopularMoviesContract.MovieEntry.COLUMN_CUSTOM_ID, movie.getId());
        contentValues.put(PopularMoviesContract.MovieEntry.COLUMN_JSON_MOVIE, gson.toJson(movie));
        Uri uri = context.getContentResolver().insert(PopularMoviesContract.MovieEntry.CONTENT_URI, contentValues);
        return uri;
    }

    public int removeMovieFromFavorites(PopularMovie movie) {
        Uri uri = PopularMoviesContract.MovieEntry.CONTENT_URI;
        String id = Integer.toString(movie.getId());
        uri = uri.buildUpon().appendPath(id).build();
        return context.getContentResolver().delete(uri, null, null);
    }

    public boolean isFavorite(PopularMovie movie) {

        Uri uri = PopularMoviesContract.MovieEntry.CONTENT_URI;
        String id = Integer.toString(movie.getId());
        uri = uri.buildUpon().appendPath(id).build();

        Cursor cursor = context.getContentResolver().query(uri, null,
                PopularMoviesContract.MovieEntry.COLUMN_CUSTOM_ID + " = " + movie.getId(), null, null);

        if (cursor.moveToNext()) {
            return true;
        }

        cursor.close();

        return false;
    }

}
