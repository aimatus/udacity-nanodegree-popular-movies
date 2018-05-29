package com.aimatus.popularmovies.movie.grid;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;

import com.aimatus.popularmovies.R;
import com.aimatus.popularmovies.movie.PopularMovie;

public class PopularMoviesViewHolder extends ViewHolder implements View.OnClickListener {

    public final ImageView posterImageView;
    private final PopularMoviesAdapter popularMoviesAdapter;

    PopularMoviesViewHolder(View itemView, PopularMoviesAdapter popularMoviesAdapter) {
        super(itemView);
        this.posterImageView = itemView.findViewById(R.id.iv_movie_poster);
        this.popularMoviesAdapter = popularMoviesAdapter;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int adapterPosition = getAdapterPosition();
        PopularMovie movie = popularMoviesAdapter.movies.get(adapterPosition);
        popularMoviesAdapter.onClickHandler.onClick(movie);
    }
}
