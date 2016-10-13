package com.fofxlabs.phuc.inspereddit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fofxlabs.phuc.inspereddit.R;
import com.fofxlabs.phuc.inspereddit.models.Post;
import com.fofxlabs.phuc.inspereddit.utils.Constants;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by phuc on 10/11/2016.
 */

public class PostAdapter extends ArrayAdapter<Post> {

    private Context mContext;
    private String mCollectionType;

    private class ViewHolder {
        RelativeLayout rlItem;
        TextView tvTitle;
        ImageView ivThumbnail;
        ProgressBar progressBar;

        private void showThumbnail(Bitmap bitmap) {
            ivThumbnail.setImageBitmap(bitmap);
            ivThumbnail.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        private void loadThumbnail() {
            progressBar.setVisibility(View.VISIBLE);
            ivThumbnail.setVisibility(View.INVISIBLE);
        }

        private void setRowBackgroundColor(int color) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                rlItem.setBackgroundColor(mContext.getResources().getColor(color, null));
            }
            else {
                rlItem.setBackgroundColor(mContext.getResources().getColor(color));
            }
        }

        private void setTitleColor(int color) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                tvTitle.setTextColor(mContext.getResources().getColor(color, null));
            }
            else {
                tvTitle.setTextColor(mContext.getResources().getColor(color));
            }
        }
    }

    public PostAdapter(Context context, ArrayList<Post> posts, String collectionType) {
        super(context, 0, posts);

        mContext = context;
        mCollectionType = collectionType;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = getItem(position);

        if (post != null) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                switch (mCollectionType) {
                    case Constants.LIST:
                        convertView = inflater.inflate(R.layout.list_view_item, parent, false);
                        break;
                    case Constants.GRID:
                        convertView = inflater.inflate(R.layout.grid_view_item, parent, false);
                        break;
                }

                viewHolder.rlItem = (RelativeLayout) convertView.findViewById(R.id.rlItem);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                viewHolder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
                viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.loadThumbnail();

            viewHolder.ivThumbnail.setTag(position);

            if (viewHolder.tvTitle != null) {
                if (position % 2 == 0) {
                    viewHolder.setRowBackgroundColor(R.color.grey);
                    viewHolder.setTitleColor(R.color.white);
                }
                else {
                    viewHolder.setRowBackgroundColor(R.color.lightGrey);
                    viewHolder.setTitleColor(R.color.grey);
                    viewHolder.setRowBackgroundColor(R.color.grey);
                    viewHolder.setTitleColor(R.color.white);
                }

                viewHolder.tvTitle.setText(post.getTitle());
            }

            if (post.mThumbnail == null || post.mImage == null) {
                new DownloadImagesTask(position, post, viewHolder).execute(post.getThumbnailUrl(), post.getImageUrl());
            }
            else {
                viewHolder.showThumbnail(post.mThumbnail);
            }
        }

        return convertView;
    }

    // Task to download thumbnail and full image of a post
    private class DownloadImagesTask extends AsyncTask<String, Void, Bitmap[]> {
        int mPosition;
        Post mPost;
        ViewHolder mViewHolder;
        int tag;

        DownloadImagesTask(int position, Post post, ViewHolder viewHolder) {
            mPosition = position;
            mPost = post;
            mViewHolder = viewHolder;
            tag = (int) viewHolder.ivThumbnail.getTag();
        }

        @Override
        protected Bitmap[] doInBackground(String... urls) {
            String thumbnailUrl = urls[0];
            String imageUrl = urls[1];
            Bitmap[] bitmaps = new Bitmap[2];
            try {
                InputStream inputStreamThumbnail = new java.net.URL(thumbnailUrl).openStream();
                bitmaps[0] = BitmapFactory.decodeStream(inputStreamThumbnail);

                InputStream inputStreamImage = new java.net.URL(imageUrl).openStream();
                bitmaps[1] = BitmapFactory.decodeStream(inputStreamImage);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return bitmaps;
        }

        @Override
        protected void onPostExecute(Bitmap[] bitmaps) {
            // Store downloaded bitmaps into post
            mPost.mThumbnail = bitmaps[0];
            if (bitmaps[1] != null) {
                mPost.mImage = bitmaps[1];
            }
            else {
                mPost.mImage = bitmaps[0];
            }

            // Update imageView if correct tag
            if ((int)mViewHolder.ivThumbnail.getTag() == tag) {
                mViewHolder.showThumbnail(bitmaps[0]);
            }
        }
    }
}
