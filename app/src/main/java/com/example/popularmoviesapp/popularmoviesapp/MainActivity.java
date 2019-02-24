package com.example.popularmoviesapp.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;


public class MainActivity extends AppCompatActivity implements PosterAdopter.MovieClickListener {
    TextView tv_error;
    ProgressBar pb_show_progress;
    private RecyclerView mRecyclerView;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String SAVED_TITLE = "savedTitle";
    private static final String SAVED_QUERY = "savedQuery";
    private final static String MENU_SELECTED = "selected";
    private int selected = -1;
    MenuItem menuItem;

    private String queryMovie = "popular";
    private String appTitle = "Popular Movies";
    private Movies[] mMovie = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_error = findViewById(R.id.tv_error);
        pb_show_progress = findViewById(R.id.pb_show_progress);

        mRecyclerView = findViewById(R.id.rv_show_movies_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        setTitle(appTitle);

        if (!isOnline()) {
            networkError();
            return;
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_TITLE) || savedInstanceState.containsKey(SAVED_QUERY)) {

                selected = savedInstanceState.getInt(MENU_SELECTED);
                queryMovie = savedInstanceState.getString(SAVED_QUERY);
                appTitle = savedInstanceState.getString(SAVED_TITLE);
                setTitle(appTitle);

                new MovieFetchTask().execute(queryMovie);
                return;
            }
        }

        new MovieFetchTask().execute(queryMovie);

    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void networkError() {
        pb_show_progress.setVisibility(View.INVISIBLE);
        tv_error.setVisibility(View.VISIBLE);
    }

    private void hideViews() {
        tv_error.setVisibility(View.INVISIBLE);
        pb_show_progress.setVisibility(View.INVISIBLE);
    }


    public void onClickMovie(int position) {

        if (!isOnline()) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            networkError();
            return;
        }

        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra("MovieTitle", mMovie[position].getMovieTitle());
        intent.putExtra("MoviePlot", mMovie[position].getMoviePLot());
        intent.putExtra("MovieRating", mMovie[position].getMovieRating());
        intent.putExtra("ReleaseDate", mMovie[position].getReleaseDate());
        intent.putExtra("MovieImage", mMovie[position].getImage());

        startActivity(intent);

    }

    private class MovieFetchTask extends AsyncTask<String, Void, Movies[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mRecyclerView.setVisibility(View.INVISIBLE);
            pb_show_progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Movies[] doInBackground(String... strings) {

            if (!isOnline()) {
                networkError();
                return null;
            }

            if (NetworkUtils.API_KEY.equals("")) {
                networkError();
                tv_error.setText(R.string.missing_api_key);
                return null;
            }

            String movieQueryResponse;
            URL theMovieUrl = NetworkUtils.buildUrl(strings[0]);

            try {

                movieQueryResponse = NetworkUtils.getResponseFromHttp(theMovieUrl);
                mMovie = JsonUtils.parseMovieJson(movieQueryResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return mMovie;
        }

        @Override
        protected void onPostExecute(Movies[] movies) {
            new MovieFetchTask().cancel(true);

            if (movies != null) {

                mMovie = movies;
                PosterAdopter adapter = new PosterAdopter(movies, MainActivity.this, MainActivity.this);
                mRecyclerView.setAdapter(adapter);

                mRecyclerView.setVisibility(View.VISIBLE);
                hideViews();

            } else {
                Log.e(LOG_TAG, "Problems with the adapter");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        if (selected == -1) {
            return true;
        }

        switch (selected) {

            case R.id.popularity:
                menuItem = menu.findItem(R.id.popularity);
                menuItem.setChecked(true);
                break;

            case R.id.top_rated:
                menuItem = menu.findItem(R.id.top_rated);
                menuItem.setChecked(true);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!isOnline())
            return false;

        if (NetworkUtils.API_KEY.equals(""))
            return false;

        int id = item.getItemId();

        switch (id) {
            case R.id.popularity:

                selected = id;
                item.setChecked(true);

                queryMovie = "popular";
                new MovieFetchTask().execute(queryMovie);

                appTitle = "Popular Movies";
                setTitle(appTitle);

                break;

            case R.id.top_rated:

                selected = id;
                item.setChecked(true);

                queryMovie = "top_rated";
                new MovieFetchTask().execute(queryMovie);

                appTitle = "Top Rated Movies";
                setTitle(appTitle);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        String currQueryMovie = queryMovie;
        String currAppTitle = appTitle;

        savedInstanceState.putString(SAVED_QUERY, currQueryMovie);
        savedInstanceState.putString(SAVED_TITLE, currAppTitle);

        savedInstanceState.putInt(MENU_SELECTED, selected);

    }
}