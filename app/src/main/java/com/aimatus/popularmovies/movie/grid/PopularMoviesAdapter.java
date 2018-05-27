package com.aimatus.popularmovies.movie.grid;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aimatus.popularmovies.movie.PopularMovie;
import com.aimatus.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter for the popular movies grid RecyclerView.
 *
 * @author Abraham Matus
 */
public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesViewHolder> {

    /**
     * On click handler for the popular movies grid.
     */
    public final PopularMoviesAdapterOnClickHandler onClickHandler;

    /**
     * List with the popular movies.
     */
    public List<PopularMovie> movies;

    /**
     * Parent context.
     */
    private Context context;

    /**
     * Creates a PopularMoviesAdapter.
     *
     * @param onClickHandler The on-click handler for this adapter. This single handler is called
     *                       when an item is clicked.
     */
    public PopularMoviesAdapter(PopularMoviesAdapterOnClickHandler onClickHandler) {
        this.onClickHandler = onClickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent   The ViewGroup that these ViewHolders are contained within.
     * @param viewType If your RecyclerView has more than one type of item (which ours doesn't) you
     *                 can use this viewType integer to provide a different layout. See
     *                 {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                 for more details.
     *
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public PopularMoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context = parent.getContext();
        int layoutIdForMovieItem = R.layout.movie_grid_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;

        View itemView = layoutInflater.inflate(layoutIdForMovieItem, parent, shouldAttachToParentImmediately);

        PopularMoviesViewHolder popularMoviesViewHolder = new PopularMoviesViewHolder(itemView, this);

        return popularMoviesViewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the
     *                 contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(PopularMoviesViewHolder holder, int position) {
        PopularMovie movie = this.movies.get(position);
        String url = context.getString(R.string.the_movie_db_poster_path, movie.getPosterPath());
        Picasso.get().load(url).into(holder.posterImageView);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        }
        return movies.size();
    }

    /**
     * Set popular movies and notify changes to the adapter.
     *
     * @param movies movies to be set to the adapter.
     */
    public void setMovies(List<PopularMovie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}
