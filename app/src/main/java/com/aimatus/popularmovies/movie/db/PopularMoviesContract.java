package com.aimatus.popularmovies.movie.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class PopularMoviesContract {

    public static final String AUTHORITY = "com.aimatus.popularmoviesoreo";
    public static final String PATH_MOVIES = "movies";
    public static final String DATABASE_NAME = "popularmovies.db";
    public static final int DATABASE_VERSION = 1;
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_CUSTOM_ID = "custom_id";
        public static final String COLUMN_JSON_MOVIE = "movie_json";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static final String CREATE_TABLE
                = String.format("CREATE TABLE %1$s (" +
                        "%2$s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%3$s INTEGER NOT NULL, " +
                        "%4$s TEXT NOT NULL, " +
                        "%5$s TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
                TABLE_NAME, _ID, COLUMN_CUSTOM_ID, COLUMN_JSON_MOVIE, COLUMN_TIMESTAMP);

        public static final String DELETE_TABLE
                = String.format("DROP TABLE IF EXISTS %1$s", TABLE_NAME);

    }

}
