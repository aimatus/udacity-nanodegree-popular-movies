package com.aimatus.popularmovies.movie;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Class that represents a result query from TheMovieDB.
 *
 * @author Abraham Matus
 */
public class PopularMoviesQueryResult {

    private int page;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;

    private List<PopularMovie> results;

    public PopularMoviesQueryResult() {
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<PopularMovie> getResults() {
        return results;
    }

    public void setResults(List<PopularMovie> results) {
        this.results = results;
    }
}
