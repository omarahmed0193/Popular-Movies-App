package com.udacity.popularmovies.retrofit;


import com.udacity.popularmovies.beans.MoviesResult;
import com.udacity.popularmovies.beans.ReviewsResults;
import com.udacity.popularmovies.beans.TrailerResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBApi {

    @GET("/3/movie/{id}/videos")
    Call<TrailerResults> getMovieTrailersList(
            @Path("id") String movieID,
            @Query("api_key") String key
    );

    @GET("/3/movie/{id}/reviews")
    Call<ReviewsResults> getMovieReviewsList(
            @Path("id") String movieID,
            @Query("api_key") String key
    );

    @GET("/3/movie/{sort_by}")
    Call<MoviesResult> getMoviesList(
            @Path("sort_by") String sortBy,
            @Query("api_key") String key
    );
}
