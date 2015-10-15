package com.bill.mygitosc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bill.mygitosc.R;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * Created by liaobb on 2015/08/03.
 */
public class TipInfoLayout extends FrameLayout {
    private ProgressWheel mPbProgressBar;
    private ImageView mTvTipState;
    private TextView mTvTipMsg;

    private Context context;

    public TipInfoLayout(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public TipInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public TipInfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.tip_info_layout, null, false);
        mPbProgressBar = (ProgressWheel) view.findViewById(R.id.tv_tip_loading);
        mTvTipState = (ImageView) view.findViewById(R.id.tv_tip_state);
        mTvTipMsg = (TextView) view.findViewById(R.id.tv_tip_msg);
        setLoading();
        addView(view);
    }

    /*public void setOnClick(OnClickListener onClik) {
        this.setOnClickListener(onClik);
    }*/

    public void setLoading() {
        //this.setVisibility(VISIBLE);
        this.mPbProgressBar.setVisibility(View.VISIBLE);
        this.mTvTipState.setVisibility(View.GONE);
        this.mTvTipMsg.setText(context.getString(R.string.tip_loading));
    }

    public void setNetworkError() {
        this.mPbProgressBar.setVisibility(View.GONE);
        this.mTvTipState.setVisibility(View.VISIBLE);
        this.mTvTipState.setImageResource(R.drawable.page_icon_network);
        this.mTvTipMsg.setText(context.getString(R.string.tip_load_network_error));
    }

    public void setLoadError(String message){
        setLoadError();
        this.mTvTipMsg.setText(message);
    }

    public void setLoadError() {
        this.mPbProgressBar.setVisibility(View.GONE);
        this.mTvTipState.setVisibility(View.VISIBLE);
        this.mTvTipState.setImageResource(R.drawable.page_icon_loaderror);
        this.mTvTipMsg.setText(context.getString(R.string.tip_load_error));
    }

    public void setEmptyData(String message){
        setEmptyData();
        this.mTvTipMsg.setText(message);
    }

    public void setEmptyData() {
        this.setVisibility(VISIBLE);
        this.mPbProgressBar.setVisibility(View.GONE);
        this.mTvTipState.setVisibility(View.VISIBLE);
        this.mTvTipState.setImageResource(R.drawable.page_icon_empty);
        this.mTvTipMsg.setText(context.getString(R.string.tip_load_empty));
    }
}
