package com.aimatus.popularmovies.review;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aimatus.popularmovies.R;

import java.util.List;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsViewHolder> {

    private static List<MovieReview> reviews;

    MovieReviewsAdapter(List<MovieReview> reviews) {
        MovieReviewsAdapter.reviews = reviews;
    }

    @NonNull
    @Override
    public MovieReviewsAdapter.MovieReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForMovieItem = R.layout.movie_review_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;
        View itemView = layoutInflater.inflate(layoutIdForMovieItem, parent, shouldAttachToParentImmediately);
        return new MovieReviewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewsAdapter.MovieReviewsViewHolder holder, int position) {
        String reviewAuthor = "- " + reviews.get(position).getAuthor();
        holder.mAuthorTextView.setText(reviewAuthor);
        holder.mReviewTextView.setText(reviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if (reviews == null)
            return 0;
        return reviews.size();
    }

    static class MovieReviewsViewHolder extends RecyclerView.ViewHolder {

        final TextView mAuthorTextView;
        final TextView mReviewTextView;

        MovieReviewsViewHolder(View itemView) {
            super(itemView);
            this.mAuthorTextView = itemView.findViewById(R.id.tv_review_author);
            this.mReviewTextView = itemView.findViewById(R.id.tv_review_content);
        }
    }
}
