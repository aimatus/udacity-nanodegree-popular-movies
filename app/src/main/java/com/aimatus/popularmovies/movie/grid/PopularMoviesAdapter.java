package com.aimatus.popularmovies.movie.grid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aimatus.popularmovies.R;
import com.aimatus.popularmovies.movie.PopularMovie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesViewHolder> {

    public final PopularMoviesAdapterOnClickHandler onClickHandler;
    public List<PopularMovie> movies;
    private Context context;

    PopularMoviesAdapter(PopularMoviesAdapterOnClickHandler onClickHandler) {
        this.onClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public PopularMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        int layoutIdForMovieItem = R.layout.movie_grid_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;
        View itemView = layoutInflater.inflate(layoutIdForMovieItem, parent, shouldAttachToParentImmediately);
        return new PopularMoviesViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularMoviesViewHolder holder, int position) {
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

    public interface PopularMoviesAdapterOnClickHandler {
        void onClick(PopularMovie movie);
    }
}
