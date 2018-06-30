package me.sanura_njaka.flicks;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.sanura_njaka.flicks.models.Config;
import me.sanura_njaka.flicks.models.Movie;
import me.sanura_njaka.flicks.models.MovieDetailsActivity;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    // list of movies
    ArrayList<Movie> movies;
    // config needed for image urls
    Config config;
    // context for rendering
    Context context;

    // initialize with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    // creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // get the context and create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create the view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        // return a new ViewHolder
        return new ViewHolder(movieView);
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // get the movie data at the specified position
        Movie movie = movies.get(position);
        // populate the view with the movie data
        viewHolder.tvTitle.setText(movie.getTitle());
        viewHolder.tvOverview.setText(movie.getOverview());
        //viewHolder.tvOverview.setMovementMethod(new ScrollingMovementMethod());

        // determine the current orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        // build url for poster image
        String imageUrl;
        // get correct placeholder and imageview for current orientation
        int placeholderId;
        ImageView imageView;

        // if in portrait mode, load the poster image and get poster placeholder and imageview
        if (isPortrait) {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
            placeholderId = R.drawable.flicks_movie_placeholder;
            imageView = viewHolder.ivPosterImage;
        } else {
            // load the backdrop image and get backdrop placeholder and imageview
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
            placeholderId = R.drawable.flicks_backdrop_placeholder;
            imageView = viewHolder.ivBackdropImage;
        }

        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.placeholderOf(placeholderId)
                        .error(placeholderId)
                        .fitCenter())
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(15, 0)))
                .into(imageView);

    }

    // returns the total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // create the viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // track view objects
        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // lookup view objects by id
            ivPosterImage = itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = itemView.findViewById(R.id.ivBackdropImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // get item position
            int position = getAdapterPosition();

            // make sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position
                Movie movie = movies.get(position);
                // create the intent for the new activity
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
