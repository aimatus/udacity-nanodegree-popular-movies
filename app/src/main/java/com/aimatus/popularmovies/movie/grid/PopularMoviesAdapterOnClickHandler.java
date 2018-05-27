package com.aimatus.popularmovies.movie.grid;

import com.aimatus.popularmovies.movie.PopularMovie;

/**
 * Interface for overwriting the onClick action.
 *
 * @author Abraham Matus
 */
public interface PopularMoviesAdapterOnClickHandler {

    /**
     * Method that receives a PopularMovie.
     *
     * @param movie passed movie.
     */
    void onClick(PopularMovie movie);

}
