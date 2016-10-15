package com.fofxlabs.phuc.inspereddit.utils;

import android.content.Context;
import android.widget.ProgressBar;

/**
 * Created by phuc on 10/15/2016.
 */

public class CommonFunctions {

    public static void setProgressBarColor(Context context, ProgressBar progressBar, int color) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(context.getResources().getColor(color, null), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        else {
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(context.getResources().getColor(color), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }
}
