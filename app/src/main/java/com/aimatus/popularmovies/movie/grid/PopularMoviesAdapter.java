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

    public final PopularMoviesAdapterOnClickHandler onClickHandler;
    public List<PopularMovie> movies;
    private Context context;

    public PopularMoviesAdapter(PopularMoviesAdapterOnClickHandler onClickHandler) {
        this.onClickHandler = onClickHandler;
    }

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

    @Override
    public void onBindViewHolder(PopularMoviesViewHolder holder, int position) {
        PopularMovie movie = this.movies.get(position);
        String url = context.getString(R.string.the_movie_db_poster_path, movie.getPosterPath());
        Picasso.get().load(url).into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        }
        return movies.size();
    }

    public void setMovies(List<PopularMovie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}
