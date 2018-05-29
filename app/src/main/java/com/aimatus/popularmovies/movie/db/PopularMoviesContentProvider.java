package com.aimatus.popularmovies.movie.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aimatus.popularmovies.movie.db.PopularMoviesContract.MovieEntry;

public class PopularMoviesContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopularMoviesDbHelper popularMoviesDbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        popularMoviesDbHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase sqLiteDatabase = popularMoviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor = getQueryCursor(uri, projection, selection, selectionArgs, sortOrder, sqLiteDatabase, match);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getQueryCursor(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, SQLiteDatabase sqLiteDatabase, int match) {
        Cursor cursor;
        switch (match) {
            case MOVIES:
                cursor = sqLiteDatabase.query(MovieEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case MOVIES_WITH_ID:
                cursor = getMovieCursorById(uri, sqLiteDatabase);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    private Cursor getMovieCursorById(@NonNull Uri uri, SQLiteDatabase sqLiteDatabase) {
        Cursor cursor;
        String customId = uri.getPathSegments().get(1);
        String selectByCustomId = MovieEntry.COLUMN_CUSTOM_ID + " = " + customId;
        cursor = sqLiteDatabase.query(MovieEntry.TABLE_NAME, null, selectByCustomId,
                null, null, null, null, null);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase = popularMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        return getInsertUri(uri, contentValues, sqLiteDatabase, match);
    }

    private Uri getInsertUri(@NonNull Uri uri, @Nullable ContentValues contentValues, SQLiteDatabase sqLiteDatabase, int match) {
        switch (match) {
            case MOVIES:
                return getInsertedMovieUri(uri, contentValues, sqLiteDatabase);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Uri getInsertedMovieUri(@NonNull Uri uri, @Nullable ContentValues contentValues, SQLiteDatabase sqLiteDatabase) {
        long id = sqLiteDatabase.insert(MovieEntry.TABLE_NAME, null, contentValues);
        if (id > 0) {
            return ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
        } else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase sqLiteDatabase = popularMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deletedMovie = getDeletedMovies(uri, sqLiteDatabase, match);
        if (deletedMovie != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedMovie;
    }

    private int getDeletedMovies(@NonNull Uri uri, SQLiteDatabase sqLiteDatabase, int match) {
        switch (match) {
            case MOVIES_WITH_ID:
                return getDeletedMovieId(uri, sqLiteDatabase);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private int getDeletedMovieId(@NonNull Uri uri, SQLiteDatabase sqLiteDatabase) {
        String id = uri.getPathSegments().get(1);
        String whereClause = MovieEntry.COLUMN_CUSTOM_ID + "= ?";
        String[] whereArgs = {id};
        int deletedMovie = sqLiteDatabase.delete(MovieEntry.TABLE_NAME, whereClause, whereArgs);
        return deletedMovie;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
