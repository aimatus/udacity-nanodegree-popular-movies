package com.aimatus.popularmovies.movie.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    PopularMoviesDbHelper(Context context) {
        super(context, PopularMoviesContract.DATABASE_NAME, null, PopularMoviesContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PopularMoviesContract.MovieEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldDbVersion, int newDbVersion) {
        sqLiteDatabase.execSQL(PopularMoviesContract.MovieEntry.DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
