package com.vincestyling.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.vincestyling.android.ui.EllipsizeEndTextView;

public class MainActivity extends Activity implements View.OnClickListener {
    private EllipsizeEndTextView mTxvEllipsize;
    private TextView mBtnExpand;
    private View mLotEllipsize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTxvEllipsize = (EllipsizeEndTextView) findViewById(R.id.txvEllipsize);

        mLotEllipsize = findViewById(R.id.lotEllipsize);

        mBtnExpand = (TextView) findViewById(R.id.btnExpand);

        mTxvEllipsize.setOnMeasureDoneListener(new EllipsizeEndTextView.OnMeasureDoneListener() {
            @Override
            public void onMeasureDone(View v) {
                if (mTxvEllipsize.isExpanded()) {
                    mBtnExpand.setVisibility(View.GONE);
                } else {
                    mLotEllipsize.setOnClickListener(MainActivity.this);
                }
                mTxvEllipsize.setOnMeasureDoneListener(null);
            }
        });
        mTxvEllipsize.setText(getString(R.string.ellipsize_txt_chn));
    }

    @Override
    public void onClick(View v) {
        mTxvEllipsize.setOnMeasureDoneListener(new EllipsizeEndTextView.OnMeasureDoneListener() {
            @Override
            public void onMeasureDone(View v) {
                int resId = mTxvEllipsize.isExpanded() ? R.string.book_detail_summary_collapse : R.string.book_detail_summary_expand;
                mBtnExpand.setText(resId);
                resId = mTxvEllipsize.isExpanded() ? R.drawable.book_detail_arrow_up : R.drawable.book_detail_arrow_down;
                mBtnExpand.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
            }
        });
        mTxvEllipsize.elipsizeSwitch();
    }

}
