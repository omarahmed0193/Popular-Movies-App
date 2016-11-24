package com.udacity.popularmovies.detail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.beans.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

    @BindView(R.id.detail_toolbar)
    Toolbar mDetailToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(mDetailToolbar);

        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);

        getSupportActionBar().setTitle(movie.getTitle());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container,
                            DetailsFragment.newInstance(movie),
                            DetailsFragment.DETAILS_FRAGMENT_TAG).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
