package com.udacity.popularmovies.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.beans.Movie;
import com.udacity.popularmovies.beans.ReviewsResults;
import com.udacity.popularmovies.beans.TrailerResults;
import com.udacity.popularmovies.retrofit.ServiceGenerator;
import com.udacity.popularmovies.retrofit.TMDBApi;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailsFragment extends Fragment {

    @BindView(R.id.details_thumbnail)
    ImageView mDetailsThumbnail;
    @BindView(R.id.details_title)
    TextView mDetailsTitle;
    @BindView(R.id.details_rating)
    TextView mDetailsRating;
    @BindView(R.id.details_release_year)
    TextView mDetailsReleaseYear;
    @BindView(R.id.detail_overview)
    TextView mDetailOverview;
    @BindView(R.id.detail_title_trailer)
    TextView mDetailTitleTrailer;
    @BindView(R.id.details_trailer_recyclerView)
    RecyclerView mDetailsTrailerRecyclerView;
    @BindView(R.id.detail_title_review)
    TextView mDetailTitleReview;
    @BindView(R.id.details_review_recyclerView)
    RecyclerView mDetailsReviewRecyclerView;


    public static final String ARGS_MOVIE = "args_movie";
    public static final String DETAILS_FRAGMENT_TAG = "tag";

    private Movie mMovie;
    private Realm mRealm;
    private ShareActionProvider mShareActionProvider;
    private MenuItem mShareMenuItem;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_MOVIE, movie);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);

        mRealm = Realm.getDefaultInstance();

        setHasOptionsMenu(true);

        mMovie = getArguments().getParcelable(ARGS_MOVIE);

        initDetailsCard();

        getReviews();

        getTrailers();

        return view;
    }

    private void initDetailsCard() {
        mDetailsRating.setText(mMovie.getVoteAverage() + "/10");
        mDetailsTitle.setText(mMovie.getTitle());
        mDetailOverview.setText(mMovie.getOverview());
        mDetailsReleaseYear.setText(mMovie.getReleaseDate().substring(0, 4));
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w342/" +
                mMovie.getPosterPath()).into(mDetailsThumbnail);
    }

    private void getReviews() {
        TMDBApi tmdbApi = ServiceGenerator.createService(TMDBApi.class);
        tmdbApi.getMovieReviewsList(mMovie.getId().toString(),
                BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                .enqueue(new Callback<ReviewsResults>() {
                    @Override
                    public void onResponse(Call<ReviewsResults> call, Response<ReviewsResults> response) {
                        if (response.isSuccessful() && !response.body().getReviews().isEmpty()) {
                            mDetailsReviewRecyclerView.setNestedScrollingEnabled(false);
                            mDetailsReviewRecyclerView.setVisibility(View.VISIBLE);
                            mDetailTitleReview.setVisibility(View.VISIBLE);
                            mDetailsReviewRecyclerView.setAdapter(new ReviewsAdapter(getActivity(),
                                    response.body().getReviews()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewsResults> call, Throwable t) {

                    }
                });
    }

    private void getTrailers() {
        TMDBApi tmdbApi = ServiceGenerator.createService(TMDBApi.class);
        tmdbApi.getMovieTrailersList(mMovie.getId().toString(),
                BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                .enqueue(new Callback<TrailerResults>() {
                    @Override
                    public void onResponse(Call<TrailerResults> call, Response<TrailerResults> response) {
                        if (response.isSuccessful() && !response.body().getTrailers().isEmpty()) {
                            setShareIntent(response.body().getTrailers().get(0).getKey());
                            mShareMenuItem.setVisible(true);
                            mDetailsTrailerRecyclerView.setNestedScrollingEnabled(false);
                            mDetailsTrailerRecyclerView.setVisibility(View.VISIBLE);
                            mDetailTitleTrailer.setVisibility(View.VISIBLE);
                            mDetailsTrailerRecyclerView.setAdapter(new TrailersAdapter(getActivity(),
                                    response.body().getTrailers()));
                        }
                    }

                    @Override
                    public void onFailure(Call<TrailerResults> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_details, menu);
        mShareMenuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mShareMenuItem);
    }

    private void setShareIntent(String key) {
        String trailerURL = "https://www.youtube.com/watch?v=" +
                key + "\n#Popular_Movie_App";
        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, trailerURL);
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favourite) {
            if (isFavourite()) {
                mRealm.beginTransaction();
                mRealm.where(Movie.class).equalTo("id", mMovie.getId()).findFirst().deleteFromRealm();
                mRealm.commitTransaction();
                item.setIcon(R.drawable.ic_favorite_border);
            } else {
                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(mMovie);
                mRealm.commitTransaction();
                item.setIcon(R.drawable.ic_favorite);
            }
            EventBus.getDefault().post(mMovie.getTitle());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemFavourite = menu.findItem(R.id.action_favourite);
        if (isFavourite()) {
            menuItemFavourite.setIcon(R.drawable.ic_favorite);
        } else {
            menuItemFavourite.setIcon(R.drawable.ic_favorite_border);
        }
    }

    private boolean isFavourite() {
        Movie movie = mRealm.where(Movie.class).equalTo("id", mMovie.getId()).findFirst();
        return movie != null;
    }
}
