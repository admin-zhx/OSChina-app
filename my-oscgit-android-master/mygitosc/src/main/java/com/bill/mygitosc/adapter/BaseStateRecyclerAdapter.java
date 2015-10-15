package com.bill.mygitosc.adapter;

import android.content.Context;
import android.view.View;

import com.bill.mygitosc.R;

/**
 * Created by liaobb on 2015/8/2.
 */
public abstract class BaseStateRecyclerAdapter<T> extends BaseRecyclerAdapter<T> {
    public static final int STATE_ERROR = -1;
    public static final int STATE_EMPTY = 0;
    public static final int STATE_MORE = 1;
    public static final int STATE_FULL = 2;

    public static int TYPE_ITEM = 0;
    public static int TYPE_FOOT = 1;

    private int state;

    public BaseStateRecyclerAdapter(Context context) {
        super(context);
        state = STATE_FULL;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOT;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

    protected String getStateDescription() {
        String stateDescription;
        switch (state) {
            case STATE_ERROR:
                stateDescription = context.getString(R.string.foot_state_error);
                break;
            case STATE_EMPTY:
                stateDescription = context.getString(R.string.foot_state_empty);
                break;
            case STATE_MORE:
                stateDescription = context.getString(R.string.foot_state_more);
                break;
            case STATE_FULL:
                stateDescription = context.getString(R.string.foot_state_full);
                break;
            default:
                stateDescription = "unknow error";
                break;
        }
        return stateDescription;
    }

    protected int getProgressBarVisiable() {
        if (state == STATE_MORE) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }
}
