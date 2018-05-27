package com.aimatus.popularmovies.review;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Activity which displays movie reviews.
 *
 * @author Abraham Matus
 */
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

        mParentActivity = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_reviews_loading_indicator);
        mReviewsErrorTextView = (TextView) findViewById(R.id.tv_reviews_error);
        mFrameLayout = (FrameLayout) findViewById(R.id.fl_reviews);

        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.movie_id_key))) {
            movieId = intent.getIntExtra(getString(R.string.movie_id_key), 0);
            getSupportLoaderManager().initLoader(getResources().getInteger(R.integer.movie_reviews_loader_id), null, this);
        }
    }

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

                if (!NetworkUtils.hasConnectivity(mParentActivity)) {
                    return null;
                }

                URL moviesQuery = NetworkUtils.getMovieReviewsUrl(movieId);

                try {
                    String jsonMovieReviews = NetworkUtils
                            .getResponseFromHttpUrl(moviesQuery);

                    Gson gson = new Gson();

                    MovieReviewsQueryResult movieReviewsQueryResult
                            = gson.fromJson(jsonMovieReviews, MovieReviewsQueryResult.class);

                    return movieReviewsQueryResult;

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
    public void onLoadFinished(Loader<MovieReviewsQueryResult> loader, MovieReviewsQueryResult movieReviewsQueryResult) {

        if (movieReviewsQueryResult == null) {
            setErrorMessage(getString(R.string.error_message_no_internet_connection));
            return;
        }

        List<MovieReview> reviews = movieReviewsQueryResult.getResults();

        if (reviews.size() == 0) {
            setErrorMessage(getString(R.string.no_reviews_available));
            return;
        }

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
    public void onLoaderReset(Loader<MovieReviewsQueryResult> loader) {

    }
}
