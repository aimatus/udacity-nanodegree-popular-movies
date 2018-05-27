package com.aimatus.popularmovies.movie.grid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    public static final String POPULAR_CRITERIA = "popular";
    public static final String TOP_RATED_CRITERIA = "top_rated";
    public static final String FAVORITES_CRITERIA = "favorites";

    /**
     * A reference to this activity.
     */
    private Activity mParentActivity;

    /**
     * Container for the "swipe to refresh" action.
     */
    private SwipeRefreshLayout swipeContainer;

    /**
     * RecyclerView for the movies grid.
     */
    private RecyclerView mRecyclerView;

    /**
     * Adapter for the RecyclerView.
     */
    private PopularMoviesAdapter mPopularMoviesAdapter;

    /**
     * TextView for displaying errors at fetching movies.
     */
    private TextView mErrorMessageTextView;

    /**
     * ProgressBar to show fetching movies task.
     */
    private ProgressBar mProgressBar;

    /**
     * Sorting criteria member variable.
     */
    private String mQueryCriteria;

    /**
     * OnCreate method that initializes UI elements and retrieves popular movies.
     *
     * @param savedInstanceState default saved instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_grid);

        this.mParentActivity = this;

        this.swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        this.mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message_display);
        this.mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        this.mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_grid);
        this.mPopularMoviesAdapter = new PopularMoviesAdapter(this);

        final int MOVIES_GRID_COLUMNS = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, MOVIES_GRID_COLUMNS);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);
        mRecyclerView.setHasFixedSize(true);

        initSwipeContainerRefreshListener(this);

        mProgressBar.setVisibility(View.VISIBLE);

        if (savedInstanceState != null && savedInstanceState.containsKey(getString(R.string.grid_criteria))) {
            mQueryCriteria = savedInstanceState.getString(getString(R.string.grid_criteria));
        } else {
            mQueryCriteria = POPULAR_CRITERIA;
        }

        getSupportLoaderManager().initLoader(getResources().getInteger(R.integer.popular_movies_loader_id), null, this);

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

    /**
     * Initializes the onRefresh() method for the SwipeContainer.
     *
     * @param moviesGridActivity parent activity.
     */
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

    /**
     * Sorting method by the given criteria.
     *
     * @param criteria sorting criteria: popular or top_rated;
     */
    private void sortBy(String criteria) {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mQueryCriteria = criteria;
        getSupportLoaderManager().restartLoader(getResources().getInteger(R.integer.popular_movies_loader_id), null, this);
    }

    private void getFavorites() {
        PopularMovieDao dao = new PopularMovieDao(this);
        swipeContainer.setRefreshing(false);
        mProgressBar.setVisibility(View.INVISIBLE);
        mQueryCriteria = getString(R.string.favorites_criteria);
        getSupportLoaderManager().restartLoader(getResources().getInteger(R.integer.popular_movies_loader_id), null, this);
    }

    /**
     * Starts a new activity with the movie detailed information.
     *
     * @param movie movie o display its details.
     */
    @Override
    public void onClick(PopularMovie movie) {
        Intent intent = new Intent(MoviesGridActivity.this, MovieDetailActivity.class);
        intent.putExtra(getString(R.string.movie_key), movie);
        startActivity(intent);
    }

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
                    String jsonPopularMovies = NetworkUtils
                            .getResponseFromHttpUrl(moviesQuery);
                    Gson gson = new Gson();
                    PopularMoviesQueryResult popularMoviesQueryResult
                            = gson.fromJson(jsonPopularMovies, PopularMoviesQueryResult.class);
                    return popularMoviesQueryResult;
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
    public void onLoadFinished(Loader<PopularMoviesQueryResult> loader, PopularMoviesQueryResult popularMoviesQueryResult) {
        if (popularMoviesQueryResult != null) {
            mPopularMoviesAdapter.setMovies(popularMoviesQueryResult.getResults());
            swipeContainer.setRefreshing(false);
            showMoviesDataView();
        } else {
            mErrorMessageTextView.setText(getString(R.string.error_message_no_internet_connection));
            showErrorMessage();
        }
        mProgressBar.setVisibility(View.INVISIBLE);

        switch (mQueryCriteria) {
            case POPULAR_CRITERIA:
                setTitle(getString(R.string.sort_by_popularity));
                break;
            case TOP_RATED_CRITERIA:
                setTitle(getString(R.string.sort_by_rating));
                break;
            case FAVORITES_CRITERIA:
                setTitle(getString(R.string.get_favorites));
                if (mPopularMoviesAdapter.movies.size() == 0) {
                    mErrorMessageTextView.setText(getString(R.string.no_favorites_available));
                    showErrorMessage();
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<PopularMoviesQueryResult> loader) {
        mPopularMoviesAdapter.setMovies(null);
        mPopularMoviesAdapter.notifyDataSetChanged();
    }

    /**
     * Shows movies grid and hides error messages.
     */
    private void showMoviesDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows error message and hides movie grid.
     */
    private void showErrorMessage() {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }
}

