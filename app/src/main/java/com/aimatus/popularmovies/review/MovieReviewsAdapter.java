package com.aimatus.popularmovies.review;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aimatus.popularmovies.R;

import java.util.List;

/**
 * Adapter for the movie reviews RecyclerView.
 *
 * @author Abraham Matus
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsViewHolder> {

    public static List<MovieReview> reviews;

    public MovieReviewsAdapter(List<MovieReview> reviews) {
        this.reviews = reviews;
    }

    @Override
    public MovieReviewsAdapter.MovieReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForMovieItem = R.layout.movie_review_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;

        View itemView = layoutInflater.inflate(layoutIdForMovieItem, parent, shouldAttachToParentImmediately);

        return new MovieReviewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapter.MovieReviewsViewHolder holder, int position) {
        holder.mAuthorTextView.setText("- " + reviews.get(position).getAuthor());
        holder.mReviewTextView.setText(reviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if (reviews == null)
            return 0;
        return reviews.size();
    }

    public static class MovieReviewsViewHolder extends RecyclerView.ViewHolder {

        public TextView mAuthorTextView;
        public TextView mReviewTextView;

        public MovieReviewsViewHolder(View itemView) {
            super(itemView);
            this.mAuthorTextView = (TextView) itemView.findViewById(R.id.tv_review_author);
            this.mReviewTextView = (TextView) itemView.findViewById(R.id.tv_review_content);
        }

    }

}
