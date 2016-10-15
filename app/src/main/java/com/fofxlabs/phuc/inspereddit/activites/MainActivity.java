package com.fofxlabs.phuc.inspereddit.activites;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fofxlabs.phuc.inspereddit.R;
import com.fofxlabs.phuc.inspereddit.async_tasks.ShowImagesTask;
import com.fofxlabs.phuc.inspereddit.fragments.GridViewFragment;
import com.fofxlabs.phuc.inspereddit.fragments.ListViewFragment;
import com.fofxlabs.phuc.inspereddit.fragments.PostsContainerFragment;
import com.fofxlabs.phuc.inspereddit.models.Post;
import com.fofxlabs.phuc.inspereddit.services.HttpGetRequestIntentService;
import com.fofxlabs.phuc.inspereddit.utils.CommonFunctions;
import com.fofxlabs.phuc.inspereddit.utils.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    private Toast mToast;

    private RelativeLayout mImageHolder;
    private ImageView mIvImage;
    private ProgressBar mProgressBar;

    private ListViewFragment mListViewFragment;
    private GridViewFragment mGridViewFragment;

    private ImageLoader mImageLoader = ImageLoader.getInstance();

    private ResponseReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mListViewFragment = ListViewFragment.newInstance();
        mGridViewFragment = GridViewFragment.newInstance();

        mIvImage = (ImageView) findViewById(R.id.ivImage);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        CommonFunctions.setProgressBarColor(mContext, mProgressBar, R.color.orange);

        mImageHolder = (RelativeLayout) findViewById(R.id.rlImageHolder);
        mImageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideImage();
                cancelToast();
            }
        });

        mReceiver = new ResponseReceiver();

        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(mContext).build();
        mImageLoader.init(imageLoaderConfiguration);

        showListView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerBroadcastReceiver();

        if (Post.sPosts.isEmpty()) {
            getFrontPagePosts();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_selector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list:
                showListView();
                return true;
            case R.id.grid:
                showGridView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshContent() {
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content);
        ((PostsContainerFragment) currentFragment).loadPosts();
        ((PostsContainerFragment) currentFragment).stopRefreshSpinner();
    }

    private void showFetchError() {
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content);
        ((PostsContainerFragment) currentFragment).stopRefreshSpinner();

        showToast(getResources().getString(R.string.fetch_error));
    }

    private void showListView() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, mListViewFragment)
                .commit();
    }

    private void showGridView() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, mGridViewFragment)
                .commit();
    }

    private void showToast(String string) {
        mToast = Toast.makeText(mContext, string, Toast.LENGTH_LONG);
        mToast.show();
    }

    private void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    private void hideImage() {
        mImageHolder.setVisibility(View.GONE);
        mIvImage.setVisibility(View.GONE);
    }

    public void showImage(int index) {
        Post post = Post.sPosts.get(index);
        String postTitle = post.getTitle();

        showToast(postTitle);

        new ShowImagesTask(post.getImageUrl(), mIvImage, mProgressBar).execute();
        mImageHolder.setVisibility(View.VISIBLE);
    }

    public void getFrontPagePosts() {
        HttpGetRequestIntentService.getJsonFromUrl(mContext, Constants.SUB_REDDIT);
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ResponseReceiver.ACTION_REFRESH_CONTENT);
        filter.addAction(ResponseReceiver.ACTION_SHOW_URL_FETCH_ERROR);
        registerReceiver(mReceiver, filter);
    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_REFRESH_CONTENT = "ACTION_REFRESH_CONTENT";
        public static final String ACTION_SHOW_URL_FETCH_ERROR = "ACTION_SHOW_URL_FETCH_ERROR";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();

            switch (action) {
                case ACTION_REFRESH_CONTENT:
                    String serverResponse = extras.getString(Constants.SERVER_RESPONSE);
                    Post.getPostsFromJson(serverResponse);
                    refreshContent();
                    break;
                case ACTION_SHOW_URL_FETCH_ERROR:
                    showFetchError();
            }
        }
    }
}
