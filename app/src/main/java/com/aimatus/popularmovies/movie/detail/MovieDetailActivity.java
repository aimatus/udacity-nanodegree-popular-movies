package com.aimatus.popularmovies.movie.detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aimatus.popularmovies.R;
import com.aimatus.popularmovies.movie.PopularMovie;
import com.aimatus.popularmovies.movie.db.PopularMovieDao;
import com.aimatus.popularmovies.review.MovieReviewsActivity;
import com.aimatus.popularmovies.utils.NetworkUtils;
import com.aimatus.popularmovies.video.MovieVideo;
import com.aimatus.popularmovies.video.MovieVideosQueryResult;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Object>, MovieVideosOnClickHandler {

    private static final int LOADER_IS_FAVORITE = 1;
    private static final int LOADER_FETCH_MOVIE_TRAILERS = 2;
    private static final int LOADER_ADD_TO_FAVORITES = 3;
    private static final int LOADER_REMOVE_FROM_FAVORITES = 4;

    private Activity mParentActivity;
    private TextView mMovieTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mRatingTextView;
    private TextView mOverviewTextView;
    private TextView mTrailersError;
    private ImageView mPosterImageView;
    private RatingBar mMovieRating;
    private Button mReviewsButton;
    private ImageButton mFavoriteImageButton;
    private Toast mToast;
    private List<MovieVideo> mTrailers;
    private PopularMovie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        initViews();
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.movie_key))) {
            this.movie = (PopularMovie) intent.getSerializableExtra(getString(R.string.movie_key));
            setMovieDetails(savedInstanceState);
        }
    }

    private void setMovieDetails(Bundle savedInstanceState) {
        setTitle(this.movie.getTitle());
        setMovieViewsContent();
        setReviewsButtonOnClickListener();
        boolean savedInstanceContainsFavoriteTag = savedInstanceState != null && savedInstanceState.containsKey(getString(R.string.favorite_tag));
        if (savedInstanceContainsFavoriteTag) {
            setupFavoriteButton(savedInstanceState);
        } else {
            getSupportLoaderManager().initLoader(LOADER_IS_FAVORITE, null, this);
        }
        getSupportLoaderManager().initLoader(LOADER_FETCH_MOVIE_TRAILERS, null, this);
    }

    private void setMovieViewsContent() {
        String ratingText = getString(R.string.rating_text, movie.getVoteAverage().toString());
        int yearPosition = 0;
        String releaseYear = movie.getReleaseDate().split("-")[yearPosition];
        mMovieTitleTextView.setText(movie.getTitle());
        mReleaseDateTextView.setText(releaseYear);
        mRatingTextView.setText(ratingText);
        mOverviewTextView.setText(movie.getOverview());
        mMovieRating.setRating(getStarsRating());
        String posterUrl = getString(R.string.the_movie_db_poster_path, movie.getPosterPath());
        Picasso.get().load(posterUrl).into(mPosterImageView);
    }

    private float getStarsRating() {
        return movie.getVoteAverage().divide(new BigDecimal(2), 2).floatValue();
    }

    private void setupFavoriteButton(Bundle savedInstanceState) {
        mFavoriteImageButton.setTag(savedInstanceState.getSerializable(getString(R.string.favorite_tag)));
        if (mFavoriteImageButton.getTag().equals(getString(R.string.is_favorite_tag))) {
            mFavoriteImageButton.setImageResource(R.drawable.ic_favorite_24dp);
        } else {
            mFavoriteImageButton.setImageResource(R.drawable.ic_favorite_border_24dp);
        }
    }

    private void initViews() {
        mParentActivity = this;
        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mOverviewTextView = findViewById(R.id.tv_overview);
        mRatingTextView = findViewById(R.id.tv_rating);
        mReleaseDateTextView = findViewById(R.id.tv_release_date);
        mTrailersError = findViewById(R.id.tv_trailers_error);
        mPosterImageView = findViewById(R.id.iv_movie_poster_detail);
        mMovieRating = findViewById(R.id.rb_movie_rating);
        mReviewsButton = findViewById(R.id.b_reviews);
        mFavoriteImageButton = findViewById(R.id.ib_favorite);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.favorite_tag), mFavoriteImageButton.getTag().toString());
    }

    private void setReviewsButtonOnClickListener() {
        mReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailActivity.this, MovieReviewsActivity.class);
                intent.putExtra(getString(R.string.movie_id_key), movie.getId());
                startActivity(intent);
            }
        });
    }

    private void setupFavoriteIcon(boolean isFavorite) {
        if (isFavorite) {
            mFavoriteImageButton.setImageResource(R.drawable.ic_favorite_24dp);
            mFavoriteImageButton.setTag(getString(R.string.is_favorite_tag));
        } else {
            mFavoriteImageButton.setImageResource(R.drawable.ic_favorite_border_24dp);
            mFavoriteImageButton.setTag(getString(R.string.is_not_favorite_tag));
        }
    }

    public void favoriteOnClickAction(View view) {
        cancelToastMessageDisplaying();
        if (mFavoriteImageButton.getTag().equals(getString(R.string.is_favorite_tag))) {
            mFavoriteImageButton.setImageResource(R.drawable.ic_favorite_border_24dp);
            mFavoriteImageButton.setTag(getString(R.string.is_not_favorite_tag));
            getSupportLoaderManager().initLoader(LOADER_REMOVE_FROM_FAVORITES, null, this);
        } else if (mFavoriteImageButton.getTag().equals(getResources().getString(R.string.is_not_favorite_tag))) {
            mFavoriteImageButton.setImageResource(R.drawable.ic_favorite_24dp);
            mFavoriteImageButton.setTag(getString(R.string.is_favorite_tag));
            getSupportLoaderManager().initLoader(LOADER_ADD_TO_FAVORITES, null, this);
        }
    }

    private void cancelToastMessageDisplaying() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_item:
                shareMovieTrailer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareMovieTrailer() {
        if (mTrailers != null && mTrailers.size() > 0) {
            MovieVideo video = mTrailers.get(0);
            Uri youtubeUri = Uri.parse(getString(R.string.youtube_video_url, video.getKey()));
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType(getString(R.string.trailer_mime_type))
                    .setChooserTitle(R.string.trailer_chooser_title)
                    .setText(youtubeUri.toString())
                    .startChooser();
        } else {
            cancelToastMessageDisplaying();
            mToast = Toast.makeText(this, getString(R.string.error_share_toast), Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Object> onCreateLoader(final int id, Bundle args) {

        return new AsyncTaskLoader<Object>(this) {

            Object object = null;

            @Override
            protected void onStartLoading() {
                if (object != null) {
                    deliverResult(object);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Object loadInBackground() {
                PopularMovieDao dao = new PopularMovieDao(mParentActivity);
                switch (id) {
                    case LOADER_FETCH_MOVIE_TRAILERS:
                        if (NetworkUtils.hasNoConnectivity(mParentActivity)) {
                            return null;
                        }
                        return fetchMovieTrailers();
                    case LOADER_IS_FAVORITE:
                        return dao.isFavorite(movie);
                    case LOADER_REMOVE_FROM_FAVORITES:
                        dao.removeMovieFromFavorites(movie);
                        break;
                    case LOADER_ADD_TO_FAVORITES:
                        dao.addMovieToFavorites(movie);
                        break;
                }
                return null;
            }

            private MovieVideosQueryResult fetchMovieTrailers() {
                URL moviesQuery = NetworkUtils.getMovieVideosUrl(movie.getId());
                try {
                    String jsonPopularMovies = NetworkUtils.getResponseFromHttpUrl(moviesQuery);
                    Gson gson = new Gson();
                    return gson.fromJson(jsonPopularMovies, MovieVideosQueryResult.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Object data) {
                object = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object object) {
        switch (loader.getId()) {
            case LOADER_FETCH_MOVIE_TRAILERS:
                initMovieTrailers((MovieVideosQueryResult) object);
                break;
            case LOADER_IS_FAVORITE:
                setupFavoriteIcon((boolean) object);
                break;
            case LOADER_ADD_TO_FAVORITES:
                mToast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.added_to_favorites), Toast.LENGTH_SHORT);
                mToast.show();
                break;
            case LOADER_REMOVE_FROM_FAVORITES:
                mToast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT);
                mToast.show();
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {
    }

    @Override
    public void onClick(MovieVideo video) {
        openYoutubeVideo(video.getKey());
    }

    private void initMovieTrailers(MovieVideosQueryResult movieVideosQueryResult) {
        if (movieVideosQueryResult == null) {
            mTrailersError.setVisibility(View.VISIBLE);
            return;
        }
        mTrailersError.setVisibility(View.INVISIBLE);
        mTrailers = new ArrayList<>();
        for (MovieVideo video : movieVideosQueryResult.getResults()) {
            if (video.getType().equals(getString(R.string.trailer))
                    && video.getSite().equals(getString(R.string.youtube))) {
                mTrailers.add(video);
            }
        }
        RecyclerView trailersRecyclerView = findViewById(R.id.rv_movie_trailers);
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MovieVideosAdapter movieVideosAdapter = new MovieVideosAdapter(mTrailers, this);
        trailersRecyclerView.setAdapter(movieVideosAdapter);
    }

    private void openYoutubeVideo(String videoKey) {
        Uri youtubeUri = Uri.parse(getString(R.string.youtube_video_url, videoKey));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(youtubeUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            mToast = Toast.makeText(this, getString(R.string.error_no_app_available), Toast.LENGTH_SHORT);
            mToast.show();
        }
    }
}
