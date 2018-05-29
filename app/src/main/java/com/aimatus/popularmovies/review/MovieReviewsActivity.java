package com.aimatus.popularmovies.review;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aimatus.popularmovies.R;
import com.aimatus.popularmovies.utils.NetworkUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MovieReviewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieReviewsQueryResult> {

    private Activity mParentActivity;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mReviewsErrorTextView;
    private FrameLayout mFrameLayout;

    private int movieId;
    private MovieReviewsQueryResult mMovieReviewsQueryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);
        initViews();
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.movie_id_key))) {
            loadMovieReviews(intent);
        }
    }

    private void loadMovieReviews(Intent intent) {
        movieId = intent.getIntExtra(getString(R.string.movie_id_key), 0);
        getSupportLoaderManager().initLoader(getResources().getInteger(R.integer.movie_reviews_loader_id), null, this);
    }

    private void initViews() {
        mParentActivity = this;
        mRecyclerView = findViewById(R.id.rv_reviews);
        mProgressBar = findViewById(R.id.pb_reviews_loading_indicator);
        mReviewsErrorTextView = findViewById(R.id.tv_reviews_error);
        mFrameLayout = findViewById(R.id.fl_reviews);
    }

    @NonNull
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<MovieReviewsQueryResult> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<MovieReviewsQueryResult>(this) {

            @Override
            protected void onStartLoading() {
                mProgressBar.setVisibility(View.VISIBLE);
                if (mMovieReviewsQueryResult != null) {
                    deliverResult(mMovieReviewsQueryResult);
                } else {
                    mReviewsErrorTextView.setVisibility(View.INVISIBLE);
                    forceLoad();
                }
            }

            @Override
            public MovieReviewsQueryResult loadInBackground() {
                if (NetworkUtils.hasNoConnectivity(mParentActivity)) {
                    return null;
                }
                URL moviesQuery = NetworkUtils.getMovieReviewsUrl(movieId);
                return getMovieReviewsQueryResult(moviesQuery);
            }

            @Nullable
            private MovieReviewsQueryResult getMovieReviewsQueryResult(URL moviesQuery) {
                try {
                    String jsonMovieReviews = NetworkUtils.getResponseFromHttpUrl(moviesQuery);
                    Gson gson = new Gson();
                    return gson.fromJson(jsonMovieReviews, MovieReviewsQueryResult.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(MovieReviewsQueryResult data) {
                mMovieReviewsQueryResult = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<MovieReviewsQueryResult> loader, MovieReviewsQueryResult movieReviewsQueryResult) {
        if (movieReviewsQueryResult == null) {
            setErrorMessage(getString(R.string.error_message_no_internet_connection));
            return;
        }
        List<MovieReview> reviews = movieReviewsQueryResult.getResults();
        if (reviews.size() == 0) {
            setErrorMessage(getString(R.string.no_reviews_available));
            return;
        }
        populateReviewsRecyclerView(reviews);
    }

    private void populateReviewsRecyclerView(List<MovieReview> reviews) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MovieReviewsAdapter movieReviewsAdapter = new MovieReviewsAdapter(reviews);
        mRecyclerView.setAdapter(movieReviewsAdapter);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setErrorMessage(String errorMessage) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mReviewsErrorTextView.setText(errorMessage);
        mReviewsErrorTextView.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mFrameLayout.getLayoutParams();
        params.gravity = Gravity.CENTER;
        mFrameLayout.setLayoutParams(params);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<MovieReviewsQueryResult> loader) {

    }
}
