package com.hzc.nonocontroller;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import android.widget.TextView;

public class BindingAdapters {

    @BindingAdapter(value = {"android:text"}, requireAll = false)
    public static void setInt(TextView view, int value) {
        if (view.getText() == null || !view.getText().toString().equals(String.valueOf(value))) {
            view.setText(String.valueOf(value));
        }
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    public static int getInt(TextView view) {
        String num = view.getText().toString();
        if (num.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @BindingAdapter("android:textAttrChanged")
    public static void setListeners(TextView view, final InverseBindingListener attrChange) {
        if (attrChange != null) {
            view.addTextChangedListener(new TextWatcherAdapter() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    attrChange.onChange();
                }
            });
        }
    }

    // Helper class for TextWatcher
    private static abstract class TextWatcherAdapter implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(android.text.Editable s) { }
    }
}