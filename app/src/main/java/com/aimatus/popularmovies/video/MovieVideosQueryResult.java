package com.aimatus.popularmovies.video;

import java.util.List;

public class MovieVideosQueryResult {

    private int id;
    private List<MovieVideo> results;

    public MovieVideosQueryResult() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MovieVideo> getResults() {
        return results;
    }

    public void setResults(List<MovieVideo> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "MovieVideosQueryResult{" +
                "id=" + id +
                ", results=" + results +
                '}';
    }
}
