package com.aimatus.popularmovies.movie.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.aimatus.popularmovies.movie.PopularMovie;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class PopularMovieDao {

    private Context context;
    private Gson gson;

    public PopularMovieDao(Context context) {
        this.context = context;
        gson = new Gson();
    }

    public List<PopularMovie> getFavoriteMovies() {
        Gson gson = new Gson();
        Cursor cursor = context.getContentResolver().query(PopularMoviesContract.MovieEntry.CONTENT_URI,
                        null, null, null, PopularMoviesContract.MovieEntry.COLUMN_TIMESTAMP);
        List<PopularMovie> movies = new ArrayList<>();
        populateMoviesList(gson, cursor, movies);
        closeCursor(cursor);
        return movies;
    }

    private void populateMoviesList(Gson gson, Cursor cursor, List<PopularMovie> movies) {
        while (cursor != null && cursor.moveToNext()) {
            String movieJson = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.COLUMN_JSON_MOVIE));
            PopularMovie movie = gson.fromJson(movieJson, PopularMovie.class);
            movies.add(movie);
        }
    }

    public void addMovieToFavorites(PopularMovie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PopularMoviesContract.MovieEntry.COLUMN_CUSTOM_ID, movie.getId());
        contentValues.put(PopularMoviesContract.MovieEntry.COLUMN_JSON_MOVIE, gson.toJson(movie));
        context.getContentResolver().insert(PopularMoviesContract.MovieEntry.CONTENT_URI, contentValues);
    }

    public void removeMovieFromFavorites(PopularMovie movie) {
        Uri uri = PopularMoviesContract.MovieEntry.CONTENT_URI;
        String id = Integer.toString(movie.getId());
        uri = uri.buildUpon().appendPath(id).build();
        context.getContentResolver().delete(uri, null, null);
    }

    public boolean isFavorite(PopularMovie movie) {
        Uri uri = PopularMoviesContract.MovieEntry.CONTENT_URI;
        String id = Integer.toString(movie.getId());
        uri = uri.buildUpon().appendPath(id).build();
        String selection = PopularMoviesContract.MovieEntry.COLUMN_CUSTOM_ID + " = " + movie.getId();
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null && cursor.moveToNext()) {
            return true;
        }
        closeCursor(cursor);
        return false;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
