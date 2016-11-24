package com.udacity.popularmovies.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.beans.Movie;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {


    private List<Movie> mMovieList;
    private Context mContext;

    public MoviesAdapter(List<Movie> movieList, Context context) {
        mMovieList = movieList;
        mContext = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        final Movie movie = mMovieList.get(position);
        String URL = "http://image.tmdb.org/t/p/w342/" + movie.getPosterPath();
        Picasso.with(mContext).load(URL).transform(new RoundedCornersTransformation(8,8))
                .into(holder.mMovieThumbnail);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_thumbnail)
        ImageView mMovieThumbnail;
        View mView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
        }
    }
}
