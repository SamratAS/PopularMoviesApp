package com.example.popularmoviesapp.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class PosterAdopter extends RecyclerView.Adapter<PosterAdopter.MovieHolder> {

    private static final String IMAGE_URL_PATH = "http://image.tmdb.org/t/p/w185";
    private final Context mContext;
    private Movies[] mMovie = null;
    private final MovieClickListener mMovieClickListener;


    public PosterAdopter(Movies[] movies, Context context, MovieClickListener movieClickListener) {
        mMovie = movies;
        mContext = context;
        mMovieClickListener = movieClickListener;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_poster, parent, false);

        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        holder.textViewHolder.setText(mMovie[position].getMovieTitle());
        Picasso.with(mContext)
                .load(IMAGE_URL_PATH.concat(mMovie[position].getImage()))
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .into(holder.imageViewHolder);

    }

    @Override
    public int getItemCount() {
        return mMovie.length;
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imageViewHolder;
        TextView textViewHolder;

        MovieHolder(View itemView) {
            super(itemView);

            textViewHolder = itemView.findViewById(R.id.Movie_title_id);
            imageViewHolder = itemView.findViewById(R.id.poster_img_id);
            imageViewHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickPosition = getAdapterPosition();
            mMovieClickListener.onClickMovie(clickPosition);
        }
    }

    public interface MovieClickListener {

        void onClickMovie(int position);
    }

}
