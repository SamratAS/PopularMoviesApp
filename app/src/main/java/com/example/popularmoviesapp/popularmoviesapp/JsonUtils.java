package com.example.popularmoviesapp.popularmoviesapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class JsonUtils {

    private static final String MOVIE_IMAGE = "poster_path";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_PLOT = "overview";
    private static final String MOVIE_RATING = "vote_average";
    private static final String MOVIE_RELEASE_DATE = "release_date";

    private static final String MOVIE_RESULTS = "results";
//parsing the json responce
    public static Movies[] parseMovieJson(String jsonMovieDetails) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonMovieDetails);
        JSONArray jsonArrayResult = jsonObject.getJSONArray(MOVIE_RESULTS);
        Movies[] result = new Movies[jsonArrayResult.length()];
        for (int i = 0; i < jsonArrayResult.length(); i++) {
            Movies movie = new Movies();
            movie.setImage(jsonArrayResult.getJSONObject(i).optString(MOVIE_IMAGE));
            movie.setMovieTitle(jsonArrayResult.getJSONObject(i).optString(MOVIE_TITLE));
            movie.setMoviePLot(jsonArrayResult.getJSONObject(i).optString(MOVIE_PLOT));
            movie.setMovieRating(jsonArrayResult.getJSONObject(i).optString(MOVIE_RATING));
            movie.setReleaseDate(jsonArrayResult.getJSONObject(i).optString(MOVIE_RELEASE_DATE));
            result[i] = movie;
        }
        return result;

    }

}
