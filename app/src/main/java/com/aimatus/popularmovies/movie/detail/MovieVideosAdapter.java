package com.aimatus.popularmovies.movie.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aimatus.popularmovies.R;
import com.aimatus.popularmovies.video.MovieVideo;

import java.util.List;

/**
 * Adapter for the popular movies trailers RecyclerView.
 *
 * @author Abraham Matus
 */
public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.TrailersViewHolder> {

    private static MovieVideosOnClickHandler mMovieVideosOnClickHandler;
    private static List<MovieVideo> trailers;

    public MovieVideosAdapter(List<MovieVideo> trailers, MovieVideosOnClickHandler movieVideosOnClickHandler) {
        this.trailers = trailers;
        this.mMovieVideosOnClickHandler = movieVideosOnClickHandler;
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForMovieItem = R.layout.movie_trailer_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;

        View itemView = layoutInflater.inflate(layoutIdForMovieItem, parent, shouldAttachToParentImmediately);

        return new TrailersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder holder, int position) {
        holder.trailerNameTextView.setText(trailers.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (trailers == null) {
            return 0;
        }
        return trailers.size();
    }

    public static class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView trailerNameTextView;

        public TrailersViewHolder(View itemView) {
            super(itemView);
            trailerNameTextView = (TextView) itemView.findViewById(R.id.tv_movie_trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieVideo trailer = trailers.get(adapterPosition);
            mMovieVideosOnClickHandler.onClick(trailer);
        }
    }

}
