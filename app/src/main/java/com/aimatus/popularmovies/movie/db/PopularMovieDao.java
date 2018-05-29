package com.aimatus.popularmovies.movie.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.aimatus.popularmovies.movie.PopularMovie;
import com.aimatus.popularmovies.movie.db.PopularMoviesContract.MovieEntry;
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
        Cursor cursor = context.getContentResolver().query(MovieEntry.CONTENT_URI,
                        null, null, null, MovieEntry.COLUMN_TIMESTAMP);
        List<PopularMovie> movies = new ArrayList<>();
        populateMoviesList(gson, cursor, movies);
        closeCursor(cursor);
        return movies;
    }

    private void populateMoviesList(Gson gson, Cursor cursor, List<PopularMovie> movies) {
        while (cursor != null && cursor.moveToNext()) {
            String movieJson = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_JSON_MOVIE));
            PopularMovie movie = gson.fromJson(movieJson, PopularMovie.class);
            movies.add(movie);
        }
    }

    public void addMovieToFavorites(PopularMovie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_CUSTOM_ID, movie.getId());
        contentValues.put(MovieEntry.COLUMN_JSON_MOVIE, gson.toJson(movie));
        context.getContentResolver().insert(MovieEntry.CONTENT_URI, contentValues);
    }

    public void removeMovieFromFavorites(PopularMovie movie) {
        Uri uri = MovieEntry.CONTENT_URI;
        String id = Integer.toString(movie.getId());
        uri = uri.buildUpon().appendPath(id).build();
        context.getContentResolver().delete(uri, null, null);
    }

    public boolean isFavorite(PopularMovie movie) {
        String id = Integer.toString(movie.getId());
        Uri uri = MovieEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        String selection = MovieEntry.COLUMN_CUSTOM_ID + " = " + movie.getId();
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor == null || !cursor.moveToNext()) {
            closeCursor(cursor);
            return false;
        }
        return true;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
