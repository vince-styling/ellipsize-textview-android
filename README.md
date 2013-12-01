ellipsize-textview-android
==========================
This project provides a Textview-like control which allows you to set max number
of lines to wrap an input string, then ellipsizes the last line if there's not
enough room to handle the entire input string.

As you know, the Android UI TextView also allow we to set MaxLines and EllipsizeMode,
but we can't find out how to know if TextView ellipsized, we want to know it because
we can do something for it such as show an expand/collapse Button by the ellipsize
state, that was why we wrote this widget.

For more Detail, take a look at this [Article](http://vincestyling.com/posts/2013/easily-to-know-ellipsize-mode-of-textview-in-Android.html).

## How to use?

in layout file, define it:

    <LinearLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:vincestyling="http://schemas.android.com/apk/res/com.vincestyling.android">
        // 'com.vincestyling.android' reference to 'package' attribute declare in AndroidManifest.xml

        <com.vincestyling.android.ui.EllipsizeEndTextView
            android:id="@+id/txvEllipsize"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            vincestyling:textSize="15sp"
            vincestyling:textColor="#2b2b2b"
            vincestyling:maxLines="4"
            vincestyling:lineSpacing="10dp"/>

    </LinearLayout>

in Activity, just set Text to the widget:

    mTxvEllipsize.setText(getString(R.string.ellipsize_txt_chn));