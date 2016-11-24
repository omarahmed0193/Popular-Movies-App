
package com.udacity.popularmovies.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReviewsResults {

    @SerializedName("results")
    @Expose
    private List<Review> reviews = new ArrayList<Review>();

    /**

    /**
     * 
     * @return
     *     The reviews
     */
    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * 
     * @param reviews
     *     The reviews
     */
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

}
