package com.aimatus.popularmovies.movie.grid;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;

import com.aimatus.popularmovies.movie.PopularMovie;
import com.aimatus.popularmovies.R;

/**
 * Cache of the children views for a popular movies list item.
 *
 * @author Abraham Matus
 */
public class PopularMoviesViewHolder extends ViewHolder implements View.OnClickListener {

    public final ImageView posterImageView;
    private final PopularMoviesAdapter popularMoviesAdapter;

    /**
     * Constructor which receives an view and an adapter.
     *
     * @param itemView             movie poster ImageView.
     * @param popularMoviesAdapter popular movies adapter.
     */
    public PopularMoviesViewHolder(View itemView, PopularMoviesAdapter popularMoviesAdapter) {
        super(itemView);
        this.posterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
        itemView.setOnClickListener(this);
        this.popularMoviesAdapter = popularMoviesAdapter;
    }

    /**
     * OnClick action that detects the movie clicked and passes to the adapter.
     *
     * @param v clicked view.
     */
    @Override
    public void onClick(View v) {
        int adapterPosition = getAdapterPosition();
        PopularMovie movie = popularMoviesAdapter.movies.get(adapterPosition);
        popularMoviesAdapter.onClickHandler.onClick(movie);
    }
}
