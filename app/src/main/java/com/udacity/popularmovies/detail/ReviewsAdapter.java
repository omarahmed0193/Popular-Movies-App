package com.udacity.popularmovies.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.beans.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private Context mContext;
    private List<Review> mReviewList;

    public ReviewsAdapter(Context mContext, List<Review> reviewList) {
        this.mContext = mContext;
        this.mReviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.mReviewAuthor.setText(mReviewList.get(position).getAuthor());
        holder.mReviewContent.setText(mReviewList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_author)
        TextView mReviewAuthor;
        @BindView(R.id.review_content)
        TextView mReviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
