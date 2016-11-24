package com.udacity.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.udacity.popularmovies.beans.Movie;

import java.util.List;


public class Util {

    public static boolean isConnectionAvailabe(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static String getPreferedSortingType(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.
                getString(context.getString(R.string.pref_sort_key),
                        context.getString(R.string.pref_sort_by_popularity));
    }

    public static void setPreferedSortingType(Context context, int sortByResource) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(context.getString(R.string.pref_sort_key),
                context.getString(sortByResource)).apply();
    }

    public static void setGenres(List<Movie> movies) {
        for (Movie movie : movies) {
            List<Integer> genresList = movie.getGenreIds();
            String genres = "";
            for (int i = 0; i < genresList.size(); i++) {
                String genre = "";
                switch (genresList.get(i)) {
                    case 28:
                        genre = "Action";
                        break;
                    case 12:
                        genre = "Adventure";
                        break;
                    case 16:
                        genre = "Animation";
                        break;
                    case 35:
                        genre = "Comedy";
                        break;
                    case 80:
                        genre = "Crime";
                        break;
                    case 99:
                        genre = "Documentary";
                        break;
                    case 18:
                        genre = "Drama";
                        break;
                    case 10751:
                        genre = "Family";
                        break;
                    case 14:
                        genre = "Fantasy";
                        break;
                    case 10769:
                        genre = "Foreign";
                        break;
                    case 36:
                        genre = "History";
                        break;
                    case 27:
                        genre = "Horror";
                        break;
                    case 10402:
                        genre = "Music";
                        break;
                    case 9648:
                        genre = "Mystery";
                        break;
                    case 10749:
                        genre = "Romance";
                        break;
                    case 878:
                        genre = "Science Fiction";
                        break;
                    case 10770:
                        genre = "TV Movie";
                        break;
                    case 53:
                        genre = "Thriller";
                        break;
                    case 10752:
                        genre = "War";
                        break;
                    case 37:
                        genre = "Western";
                        break;
                }
                if (i == genresList.size() - 1) {
                    genres += genre + ".";
                } else {
                    genres += genre + ", ";
                }
            }

            movie.setGenres(genres);
        }
    }

}
