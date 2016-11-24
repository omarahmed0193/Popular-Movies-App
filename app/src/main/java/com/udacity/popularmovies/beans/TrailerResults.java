
package com.udacity.popularmovies.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrailerResults {

    @SerializedName("results")
    @Expose
    private List<Trailer> trailers = new ArrayList<Trailer>();


    /**
     * @return The trailers
     */
    public List<Trailer> getTrailers() {
        return trailers;
    }

    /**
     * @param trailers The trailers
     */
    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

}
