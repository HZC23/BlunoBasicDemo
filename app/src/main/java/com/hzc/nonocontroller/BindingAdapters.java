package com.hzc.nonocontroller;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

public final class BindingAdapters {
    private BindingAdapters() {}

    // accepte un Drawable (ex: ViewModel expose Drawable)
    @BindingAdapter("srcCompat")
    public static void bindSrcCompat(ImageView view, @Nullable Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    // accepte un resource id (ex: ViewModel expose int)
    @BindingAdapter("srcCompat")
    public static void bindSrcCompatRes(ImageView view, @DrawableRes int resId) {
        if (resId != 0) view.setImageResource(resId);
        else view.setImageDrawable(null);
    }
}
