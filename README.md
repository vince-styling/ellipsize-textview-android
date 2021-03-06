ellipsize-textview-android
==========================

This project provides a Textview-like control which allows you to set max number
of lines to wrap an input string, then ellipsizes the last line if there's not
enough room to handle the entire input string.

As you know, the Android UI TextView also allow we to set MaxLines and EllipsizeMode,
but we can't find out how to know if TextView ellipsized, we want to know it because
we can do something for it such as show an expand/collapse Button by the ellipsize
state, that was why we wrote this widget.

For more Detail, take a look at this [Article](http://vincestyling.com/posts/2013/easily-to-know-and-switch-the-ellipsize-mode-of-textview-in-android.html).

## How to use?

in layout file, define it:

```xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:vincestyling="http://schemas.android.com/apk/res-auto">

    <com.vincestyling.android.ui.EllipsizeEndTextView
        android:id="@+id/txvEllipsize"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        vincestyling:textSize="15sp"
        vincestyling:textColor="#2b2b2b"
        vincestyling:maxLines="4"
        vincestyling:lineSpacing="10dp"/>

</LinearLayout>
```

in Activity, just set Text to the widget:

```java
mTxvEllipsize.setText(getString(R.string.ellipsize_txt_chn));
```

License
=======

```
Copyright 2013 Vince Styling

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```