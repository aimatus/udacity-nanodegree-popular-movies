package com.aimatus.popularmovies.movie.grid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aimatus.popularmovies.R;
import com.aimatus.popularmovies.movie.PopularMovie;
import com.aimatus.popularmovies.movie.PopularMoviesQueryResult;
import com.aimatus.popularmovies.movie.db.PopularMovieDao;
import com.aimatus.popularmovies.movie.detail.MovieDetailActivity;
import com.aimatus.popularmovies.utils.NetworkUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

public class MoviesGridActivity extends AppCompatActivity
        implements PopularMoviesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<PopularMoviesQueryResult> {

    private final String POPULAR_CRITERIA = "popular";
    private final String TOP_RATED_CRITERIA = "top_rated";
    private final String FAVORITES_CRITERIA = "favorites";

    private Activity mParentActivity;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView mRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;
    private String mQueryCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_grid);
        this.mParentActivity = this;
        initViews();
        initRecyclerView();
        initSwipeContainerRefreshListener(this);
        setupQueryCriteria(savedInstanceState);
        getSupportLoaderManager().initLoader(getResources().getInteger(R.integer.popular_movies_loader_id), null, this);
    }

    private void setupQueryCriteria(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(getString(R.string.grid_criteria))) {
            mQueryCriteria = savedInstanceState.getString(getString(R.string.grid_criteria));
        } else {
            mQueryCriteria = POPULAR_CRITERIA;
        }
    }

    private void initRecyclerView() {
        final int MOVIES_GRID_COLUMNS = 2;
        this.mPopularMoviesAdapter = new PopularMoviesAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, MOVIES_GRID_COLUMNS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

    private void initViews() {
        swipeContainer = findViewById(R.id.swipe_refresh_layout);
        mErrorMessageTextView = findViewById(R.id.tv_error_message_display);
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.rv_movies_grid);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mQueryCriteria.equals(FAVORITES_CRITERIA)) {
            getSupportLoaderManager().restartLoader(getResources().getInteger(R.integer.popular_movies_loader_id), null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mQueryCriteria.equals(getString(R.string.favorites_criteria))) {
            outState.putString(getString(R.string.grid_criteria), mQueryCriteria);
        }
    }

    private void initSwipeContainerRefreshListener(final MoviesGridActivity moviesGridActivity) {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSupportLoaderManager().restartLoader(
                        getResources().getInteger(R.integer.popular_movies_loader_id),
                        null, moviesGridActivity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_popularity:
                sortBy(POPULAR_CRITERIA);
                return true;
            case R.id.sort_by_rating:
                sortBy(TOP_RATED_CRITERIA);
                return true;
            case R.id.get_favorites:
                getFavorites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortBy(String criteria) {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mQueryCriteria = criteria;
        getSupportLoaderManager().restartLoader(getResources().getInteger(R.integer.popular_movies_loader_id), null, this);
    }

    private void getFavorites() {
        swipeContainer.setRefreshing(false);
        mProgressBar.setVisibility(View.INVISIBLE);
        mQueryCriteria = getString(R.string.favorites_criteria);
        getSupportLoaderManager().restartLoader(getResources().getInteger(R.integer.popular_movies_loader_id), null, this);
    }

    @Override
    public void onClick(PopularMovie movie) {
        Intent intent = new Intent(MoviesGridActivity.this, MovieDetailActivity.class);
        intent.putExtra(getString(R.string.movie_key), movie);
        startActivity(intent);
    }

    @NonNull
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<PopularMoviesQueryResult> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<PopularMoviesQueryResult>(this) {

            PopularMoviesQueryResult mPopularMoviesQueryResult = null;

            @Override
            protected void onStartLoading() {
                if (mPopularMoviesQueryResult != null) {
                    deliverResult(mPopularMoviesQueryResult);
                } else {
                    forceLoad();
                }
            }

            @Override
            public PopularMoviesQueryResult loadInBackground() {

                if (mQueryCriteria.equals(FAVORITES_CRITERIA)) {
                    return getLocalMovies();
                }

                if (!NetworkUtils.hasConnectivity(mParentActivity)) {
                    return null;
                }

                URL moviesQuery = null;

                switch (mQueryCriteria) {
                    case POPULAR_CRITERIA:
                        moviesQuery = NetworkUtils.getPopularMoviesUrl();
                        return getMoviesFromInternet(moviesQuery);
                    case TOP_RATED_CRITERIA:
                        moviesQuery = NetworkUtils.getTopRatedMoviesUrl();
                        return getMoviesFromInternet(moviesQuery);
                    default:
                        return null;
                }

            }

            private PopularMoviesQueryResult getMoviesFromInternet(URL moviesQuery) {
                try {
                    String jsonPopularMovies = NetworkUtils.getResponseFromHttpUrl(moviesQuery);
                    Gson gson = new Gson();
                    return gson.fromJson(jsonPopularMovies, PopularMoviesQueryResult.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            private PopularMoviesQueryResult getLocalMovies() {
                PopularMovieDao dao = new PopularMovieDao(getContext());
                PopularMoviesQueryResult popularMoviesQueryResult = new PopularMoviesQueryResult();
                popularMoviesQueryResult.setResults(dao.getFavoriteMovies());
                return popularMoviesQueryResult;
            }

            @Override
            public void deliverResult(PopularMoviesQueryResult data) {
                mPopularMoviesQueryResult = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<PopularMoviesQueryResult> loader, PopularMoviesQueryResult popularMoviesQueryResult) {
        showMovies(popularMoviesQueryResult);
        mProgressBar.setVisibility(View.INVISIBLE);
        updateActivityTitle();
    }

    private void updateActivityTitle() {
        switch (mQueryCriteria) {
            case POPULAR_CRITERIA:
                setTitle(getString(R.string.sort_by_popularity));
                break;
            case TOP_RATED_CRITERIA:
                setTitle(getString(R.string.sort_by_rating));
                break;
            case FAVORITES_CRITERIA:
                setTitle(getString(R.string.get_favorites));
                showMessageIfMoviesHasNotBeenAddedYet();
                break;
        }
    }

    private void showMessageIfMoviesHasNotBeenAddedYet() {
        if (mPopularMoviesAdapter.movies.isEmpty()) {
            mErrorMessageTextView.setText(getString(R.string.no_favorites_available));
            showErrorMessage();
        }
    }

    private void showMovies(PopularMoviesQueryResult popularMoviesQueryResult) {
        if (popularMoviesQueryResult != null) {
            mPopularMoviesAdapter.setMovies(popularMoviesQueryResult.getResults());
            swipeContainer.setRefreshing(false);
            mErrorMessageTextView.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mErrorMessageTextView.setText(getString(R.string.error_message_no_internet_connection));
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<PopularMoviesQueryResult> loader) {
        mPopularMoviesAdapter.setMovies(null);
        mPopularMoviesAdapter.notifyDataSetChanged();
    }

    private void showErrorMessage() {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }
}

