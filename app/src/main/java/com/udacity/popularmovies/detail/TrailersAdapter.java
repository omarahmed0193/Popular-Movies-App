package com.udacity.popularmovies.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.beans.Trailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by omar- on 06-Mar-16.
 */
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    private Context mContext;
    private List<Trailer> mTrailerList;

    public TrailersAdapter(Context mContext, List<Trailer> trailerList) {
        this.mContext = mContext;
        this.mTrailerList = trailerList;
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_trailer, parent, false);
        return new TrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder holder, final int position) {
        final Trailer trailer = mTrailerList.get(position);
        holder.mTrailersTitle.setText(trailer.getName());
        //load the thumbnail of the trailer
        Picasso.with(mContext).load("http://img.youtube.com/vi/" +
                trailer.getKey() + "/mqdefault.jpg").
                into(holder.mTrailersThumbnail);
        //launch an intent with the trailer url
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=" +
                                trailer.getKey())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailers_thumbnail)
        ImageView mTrailersThumbnail;
        @BindView(R.id.trailers_play_button)
        ImageView mTrailersPlayButton;
        @BindView(R.id.trailers_title)
        TextView mTrailersTitle;
        View mView;


        public TrailersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
        }
    }
}
