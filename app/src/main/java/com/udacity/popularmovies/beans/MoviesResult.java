
package com.udacity.popularmovies.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MoviesResult {

    @SerializedName("results")
    @Expose
    private List<Movie> movies = new ArrayList<Movie>();


    /**
     * @return The movies
     */
    public List<Movie> getResults() {
        return movies;
    }

    /**
     * @param movies The movies
     */
    public void setResults(List<Movie> movies) {
        this.movies = movies;
    }


}
