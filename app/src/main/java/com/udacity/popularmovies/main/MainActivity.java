package com.udacity.popularmovies.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.Util;
import com.udacity.popularmovies.beans.Movie;
import com.udacity.popularmovies.beans.MoviesResult;
import com.udacity.popularmovies.detail.DetailsActivity;
import com.udacity.popularmovies.detail.DetailsFragment;
import com.udacity.popularmovies.retrofit.ServiceGenerator;
import com.udacity.popularmovies.retrofit.TMDBApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    @BindView(R.id.movie_list_recycler_view)
    RecyclerView mMovieListRecyclerView;
    @BindView(R.id.empty_state_text_view)
    TextView mEmptyStateTextView;


    private static final String EXTRA_MOVIES_LIST = "extra_movies";
    private static final String EXTRA_SORT = "extra_sort";

    private boolean mIsInTwoPaneMode;
    private ArrayList<Movie> mMovieList;
    private Realm mRealm;
    private MoviesAdapter mMoviesAdapter;
    private SharedPreferences mSharedPreferences;
    private String mCurrentSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        if (findViewById(R.id.movie_details_container) != null) {
            mIsInTwoPaneMode = true;
        }

        if (savedInstanceState == null) {
            mMovieList = new ArrayList<>();
            initRecyclerView();
            mCurrentSort = Util.getPreferedSortingType(this);
            if (mCurrentSort.equals(getString(R.string.pref_sort_by_favourite))) {
                getFavouriteMoviesList();
            } else {
                getMoviesList(mCurrentSort);
            }
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES_LIST);
            mCurrentSort = savedInstanceState.getString(EXTRA_SORT);
            initRecyclerView();
        }

    }

    private void initRecyclerView() {
        if (getResources().getConfiguration().orientation == 1) {
            mMovieListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mMovieListRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
        mMoviesAdapter = new MoviesAdapter(mMovieList, this);
        mMovieListRecyclerView.setAdapter(mMoviesAdapter);
        mMoviesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (mMoviesAdapter.getItemCount() == 0) {
                    if (!Util.isConnectionAvailabe(MainActivity.this)
                            && !mCurrentSort.equals(getString(R.string.pref_sort_by_favourite))) {
                        mEmptyStateTextView.setText(R.string.message_no_connection_available);
                        mEmptyStateTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_cloud_off);
                        mEmptyStateTextView.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyStateTextView.setText(R.string.no_favourite_data);
                        mEmptyStateTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_favorite_big);
                        mEmptyStateTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mEmptyStateTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_MOVIES_LIST, mMovieList);
        outState.putString(EXTRA_SORT, mCurrentSort);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }

        if (mCurrentSort.equals(getString(R.string.pref_sort_by_favourite))) {
            getFavouriteMoviesList();
        }

        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mRealm.isClosed()) {
            mRealm.close();
        }

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_by_popularity) {
            clearDetailsPane();
            Util.setPreferedSortingType(this, R.string.pref_sort_by_popularity);
        } else if (id == R.id.action_sort_by_top_rated) {
            clearDetailsPane();
            Util.setPreferedSortingType(this, R.string.pref_sort_by_top_rated);
        } else if (id == R.id.action_sort_by_favourite) {
            clearDetailsPane();
            Util.setPreferedSortingType(this, R.string.pref_sort_by_favourite);
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearDetailsPane() {
        if (mIsInTwoPaneMode) {
            DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
                    .findFragmentByTag(DetailsFragment.DETAILS_FRAGMENT_TAG);
            if (detailsFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(detailsFragment).commit();
            }
        }
    }

    private void getMoviesList(String sortBy) {
        TMDBApi tmdbApi = ServiceGenerator.createService(TMDBApi.class);
        tmdbApi.getMoviesList(sortBy, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                .enqueue(new Callback<MoviesResult>() {
                    @Override
                    public void onResponse(Call<MoviesResult> call, Response<MoviesResult> response) {
                        mMovieList.clear();
                        if (response.isSuccessful()) {
                            mMovieList.addAll(response.body().getResults());
                            Util.setGenres(mMovieList);
                        }
                        mMoviesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<MoviesResult> call, Throwable t) {
                        mMovieList.clear();
                        mMoviesAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void getFavouriteMoviesList() {
        mMovieList.clear();
        mMovieList.addAll(mRealm.copyFromRealm(mRealm.where(Movie.class).findAll()));
        mMoviesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        String sortBy = sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_by_popularity));
        if (!sortBy.equals(mCurrentSort)) {
            mCurrentSort = sortBy;
            if (sortBy.equals(getString(R.string.pref_sort_by_favourite))) {
                getFavouriteMoviesList();
            } else {
                getMoviesList(sortBy);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMovieSelectedClickListener(Movie movie) {
        if (mIsInTwoPaneMode) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container,
                            DetailsFragment.newInstance(movie),
                            DetailsFragment.DETAILS_FRAGMENT_TAG).commit();

        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(DetailsActivity.EXTRA_MOVIE, movie);
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoviewRemovedFromFavouriteClickListner(String movieName) {
        if (mCurrentSort.equals(getString(R.string.pref_sort_by_favourite))) {
            getFavouriteMoviesList();
            clearDetailsPane();
        }
    }
}
