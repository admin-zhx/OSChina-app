
package net.oschina.gitapp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.oschina.gitapp.R;

public class EnhanceListView extends ListView implements OnScrollListener {

    private static final String LOG_TAG = "EnhanceListView";
    private static final int STATUS_NORMAL = 0;
    private static final int STATUS_DROP_DOWN_REFRESH = 1;
    private static final int STATUS_RELEASE_REFRESH = 2;
    private static final int STATUS_REFRESHING = 3;
    private static final int STATUS_REFRESH_COMPLETE = 4;
    private static final int STATUS_LOADING = 5;

    // attr of EnhanceListView
    private boolean isRefreshStyle;
    private boolean isLoadMoreStyle;

    // listener
    private OnScrollListener mOnScrollListener;
    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean hasMore = true;
    private boolean isReachedTop;
    private boolean isReachedBottom;
    private int mCurrentStatus;
    private int mCurrentScrollState;
    private Handler mHandler;

    // header view
    private View mHeaderLayout;
    private ImageView mHeaderImage;
    private ProgressBar mHeaderProgressBar;
    private TextView mHeaderTextView;

    // footer view
    private View mFooterLayout;
    private ProgressBar mFooterProgressBar;
    private TextView mFooterTextView;

    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    private float mPaddingTopRate = 1.5f;
    private int mHeaderReleaseMinDistance;
    private int mHeaderOriginalHeight;
    private int mHeaderOriginalTopPadding;
    private int mFooterOriginalHeight;
    private int mFooterOriginalBottomPadding;
    private float mActionDownPoinY;

    private int mHistoryCount;
    private int mPageSize = 20;
    private int mPageNum = 1;

    public EnhanceListView(Context context) {
        this(context, true, true);
    }

    public EnhanceListView(Context context, boolean isRefreshStyle, boolean isLoadMoreStyle) {
        this(context, null);
        this.isRefreshStyle = isRefreshStyle;
        this.isLoadMoreStyle = isLoadMoreStyle;
    }

    public EnhanceListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EnhanceListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public int getPageSize() {
        return mPageSize;
    }

    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    private void init(Context context, AttributeSet attrs) {
        mHandler = new Handler();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EnhanceListView);
        isRefreshStyle = a.getBoolean(R.styleable.EnhanceListView_isRefreshStyle, true);
        isLoadMoreStyle = a.getBoolean(R.styleable.EnhanceListView_isLoadMoreStyle, true);
        a.recycle();
        mCurrentStatus = STATUS_NORMAL;

        initRefreshStyle();
        initLoadMoreStyle();
        requestLayout();
        super.setOnScrollListener(this); // 必须调用父类方法，否则header和footer无动画
    }

    private void initRefreshStyle() {
        mHeaderReleaseMinDistance = getResources().getDimensionPixelSize(
                R.dimen.enhance_list_header_release_min_distance);
        mFlipAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        mHeaderLayout = LayoutInflater.from(getContext()).inflate(R.layout.enhance_list_header,
                this, false);
        mHeaderTextView = (TextView) mHeaderLayout.findViewById(R.id.enhance_list_header_text);
        mHeaderImage = (ImageView) mHeaderLayout.findViewById(R.id.enhance_list_header_image);
        mHeaderProgressBar = (ProgressBar) mHeaderLayout
                .findViewById(R.id.enhance_list_header_progress_bar);
        updateHeader(mCurrentStatus);
        addHeaderView(mHeaderLayout);

        measureHeaderLayout(mHeaderLayout);
        mHeaderOriginalHeight = mHeaderLayout.getMeasuredHeight();
        mHeaderOriginalTopPadding = mHeaderLayout.getPaddingTop();
        log("mHeaderOriginalHeight:" + mHeaderOriginalHeight);
        log("mHeaderOriginalTopPadding:" + mHeaderOriginalTopPadding);
    }

    private void initLoadMoreStyle() {
        mFooterLayout = LayoutInflater.from(getContext()).inflate(R.layout.enhance_list_footer,
                this, false);
        mFooterTextView = (TextView) mFooterLayout.findViewById(R.id.enhance_list_footer_button);
        mFooterTextView.setDrawingCacheBackgroundColor(0);
        mFooterTextView.setEnabled(true);

        mFooterProgressBar = (ProgressBar) mFooterLayout
                .findViewById(R.id.enhance_list_footer_progress_bar);
        updateFooter(mCurrentStatus);
        addFooterView(mFooterLayout);

        measureHeaderLayout(mFooterLayout);
        mFooterOriginalHeight = mFooterLayout.getMeasuredHeight();
        mFooterOriginalBottomPadding = mFooterLayout.getPaddingBottom();
        log("mFooterOriginalHeight:" + mFooterOriginalHeight);
        log("mFooterOriginalBottomPadding:" + mFooterOriginalBottomPadding);
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        super.setAdapter(adapter);
        // 在init里默认添加上了header和footer,在此处根据配置值修正header和footer
        // 在setAdapter时若header view且footer view为空，以后再添加header和footer会报错
        if (!isRefreshStyle) {
            removeHeaderView(mHeaderLayout);
        }
        if (!isLoadMoreStyle) {
            removeFooterView(mFooterLayout);
        }
        if (isRefreshStyle) {
            setSelection(1);
        }
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                // 在UI线程更新界面
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        log("data changed.");
                        int count = adapter.getCount();
                        if (count != mHistoryCount && count % mPageSize == 0) {
                            hasMore = true;
                        } else {
                            hasMore = false;
                        }
                        mHistoryCount = count;
                        mPageNum = (int) Math.ceil(count / mPageSize);
                        if (mCurrentStatus == STATUS_REFRESHING) {
                            updateInterfaceStatus(STATUS_REFRESH_COMPLETE);
                            if (mCurrentScrollState == SCROLL_STATE_IDLE) {
                                setSelection(0);
                            }
                        } else {
                            updateInterfaceStatus(STATUS_NORMAL);
                        }
                        // show or hide empty view

                        if (mCurrentStatus == STATUS_REFRESH_COMPLETE) {
                            mHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    if (isReachedTop
                                            && mCurrentScrollState == SCROLL_STATE_IDLE) {
                                        setSelection(1);
                                    }
                                    updateInterfaceStatus(STATUS_NORMAL);
                                }

                            }, 1000);
                        }

                    }
                });
            }
        });
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        OnItemClickListener onItemClickListener = getOnItemClickListener();
        if (onItemClickListener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
            int headerCount = getHeaderViewsCount();
            int footerCount = getFooterViewsCount();
            position -= headerCount;
            ListAdapter adapter = getAdapter();
            int count = 0;
            if (adapter != null) {
                count = adapter.getCount() - headerCount - footerCount;
            }
            if (position >= 0 && position < count) {
                onItemClickListener.onItemClick(this, view, position, id);
            }
            return true;
        }

        return false;
    }

    private void showEmptyView() {

    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        isReachedTop = (firstVisibleItem == 0);
        isReachedBottom = (firstVisibleItem + visibleItemCount == totalItemCount);
        if (isReachedTop) { // scroll to first
            if (isRefreshStyle) {
                // 从 STATUS_NORMAL状态 到 STATUS_DROP_DOWN_REFRESH状态
                if (totalItemCount > 0) {
                    switch (mCurrentStatus) {
                        case STATUS_NORMAL:
                            if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
                                updateInterfaceStatus(STATUS_DROP_DOWN_REFRESH);
                            } else if (isRefreshStyle) {
                                setSelection(1);
                            }
                            break;
                        case STATUS_DROP_DOWN_REFRESH:
                            if (mHeaderLayout.getBottom() >= mHeaderOriginalHeight
                                    + mHeaderReleaseMinDistance) {
                                mHeaderImage.clearAnimation();
                                mHeaderImage.startAnimation(mFlipAnimation);
                                updateInterfaceStatus(STATUS_RELEASE_REFRESH);
                            }
                            break;
                        case STATUS_RELEASE_REFRESH:
                            if (mHeaderLayout.getBottom() < mHeaderOriginalHeight
                                    + mHeaderReleaseMinDistance) {
                                mHeaderImage.clearAnimation();
                                mHeaderImage.startAnimation(mReverseFlipAnimation);
                                updateInterfaceStatus(STATUS_DROP_DOWN_REFRESH);
                            }
                            break;
                        case STATUS_REFRESHING:
                            break;
                        default:
                            break;
                    }
                    if (mCurrentStatus == STATUS_NORMAL) {
                        if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
                            updateInterfaceStatus(STATUS_DROP_DOWN_REFRESH);
                        } else {
                            setSelection(1);
                        }
                    } else if (mCurrentStatus == STATUS_LOADING
                            && mCurrentScrollState != SCROLL_STATE_TOUCH_SCROLL) {
                        setSelection(1);
                    }
                }
            }
        } else if (isReachedBottom) { // scroll to last
            if (isLoadMoreStyle) {
                // 列表拉至底部进入 STATUS_LOADING状态
                if (hasMore && mCurrentStatus == STATUS_NORMAL) {
                    updateInterfaceStatus(STATUS_LOADING);
                    mOnLoadMoreListener.onLoadMore(mPageNum + 1, mPageSize);
                }

            }
        } else {
            // 从 STATUS_DROP_DOWN_REFRESH状态 到 STATUS_NORMAL状态
            if (mCurrentStatus == STATUS_DROP_DOWN_REFRESH) {
                updateInterfaceStatus(STATUS_NORMAL);
                mHeaderLayout.setPadding(mHeaderLayout.getPaddingLeft(), mHeaderOriginalTopPadding,
                        mHeaderLayout.getPaddingRight(), mHeaderLayout.getPaddingBottom());
                mFooterLayout.setPadding(mFooterLayout.getPaddingLeft(),
                        mFooterLayout.getPaddingTop(), mFooterLayout.getPaddingRight(),
                        mFooterOriginalBottomPadding);
            }
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionDownPoinY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isReachedTop) {
                    adjustHeaderPadding(event);
                } else if (isReachedBottom) {
                    adjustFooterPadding(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isReachedTop && isRefreshStyle) {
                    log("reset HeaderPadding:" + mHeaderOriginalTopPadding);
                    mHeaderLayout.setPadding(mHeaderLayout.getPaddingLeft(),
                            mHeaderOriginalTopPadding, mHeaderLayout.getPaddingRight(),
                            mHeaderLayout.getPaddingBottom());
                    switch (mCurrentStatus) {
                        case STATUS_REFRESHING:
                            break;
                        case STATUS_RELEASE_REFRESH:
                            updateInterfaceStatus(STATUS_REFRESHING);
                            int itemCount = getAdapter().getCount() - getHeaderViewsCount()
                                    - getFooterViewsCount();
                            if (itemCount < mPageSize) {
                                itemCount = mPageSize;
                            }
                            mOnRefreshListener.onRefresh(itemCount);
                            break;
                        default:
                            setSelection(1);
                            break;
                    }
                } else if (isReachedBottom && isLoadMoreStyle) {
                    log("reset FooterPadding:" + mFooterOriginalBottomPadding);
                    mFooterLayout.setPadding(mFooterLayout.getPaddingLeft(),
                            mFooterLayout.getPaddingTop(), mFooterLayout.getPaddingRight(),
                            mFooterOriginalBottomPadding);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void adjustHeaderPadding(MotionEvent event) {
        float pointY = event.getY();
        int paddingTop = mHeaderLayout.getPaddingTop();
        int topPadding = 1;
        if (paddingTop != mHeaderOriginalTopPadding) {
            topPadding = (int) ((pointY - mActionDownPoinY - mHeaderOriginalHeight) / mPaddingTopRate);
        } else {
            mActionDownPoinY = pointY;
        }
        if (topPadding > 0) {
            // log("adjustHeaderPadding:" + topPadding);
            mHeaderLayout.setPadding(mHeaderLayout.getPaddingLeft(), topPadding
                            + mHeaderOriginalTopPadding, mHeaderLayout.getPaddingRight(),
                    mHeaderLayout.getPaddingBottom());
        }
    }

    private void adjustFooterPadding(MotionEvent event) {
        float pointY = event.getY();
        int paddingBottom = mFooterLayout.getPaddingBottom();
        int bottomPadding = 1;
        if (paddingBottom != mFooterOriginalBottomPadding) {
            bottomPadding = (int) ((mActionDownPoinY - pointY - mFooterOriginalHeight)
                    / mPaddingTopRate + mFooterOriginalHeight);
        } else {
            mActionDownPoinY = pointY;
        }
        if (bottomPadding > 0) {
            // log("adjustFooterPadding:" + bottomPadding);
            mFooterLayout.setPadding(mFooterLayout.getPaddingLeft(), mFooterLayout.getPaddingTop(),
                    mFooterLayout.getPaddingRight(), bottomPadding + mFooterOriginalHeight);
        }
    }

    private void updateInterfaceStatus(final int status) {
        mCurrentStatus = status;
        log("status=" + status);
        updateHeader(mCurrentStatus);
        updateFooter(mCurrentStatus);
    }

    private void updateHeader(int status) {
        switch (status) {
            case STATUS_NORMAL:
                mHeaderTextView.setText(" ");
                mHeaderImage.setVisibility(View.GONE);
                mHeaderProgressBar.setVisibility(View.GONE);
                break;
            case STATUS_DROP_DOWN_REFRESH:
                mHeaderTextView.setText(R.string.enhance_list_header_pull_text);
                mHeaderImage.setVisibility(View.VISIBLE);
                mHeaderImage.setImageResource(R.drawable.enhance_list_arrow);
                mHeaderProgressBar.setVisibility(View.GONE);
                break;
            case STATUS_RELEASE_REFRESH:
                mHeaderTextView.setText(R.string.enhance_list_header_release_text);
                mHeaderImage.setVisibility(View.VISIBLE);
                mHeaderImage.setImageResource(R.drawable.enhance_list_arrow);
                mHeaderProgressBar.setVisibility(View.GONE);
                break;
            case STATUS_REFRESHING:
                mHeaderTextView.setText(R.string.enhance_list_header_refreshing_text);
                mHeaderImage.setVisibility(View.GONE);
                mHeaderImage.setImageDrawable(null);
                mHeaderProgressBar.setVisibility(View.VISIBLE);
                break;
            case STATUS_REFRESH_COMPLETE:
                mHeaderTextView.setText(R.string.enhance_list_header_refresh_success_text);
                mHeaderImage.setVisibility(View.VISIBLE);
                mHeaderImage.clearAnimation();
                mHeaderImage.setImageResource(R.drawable.refresh_success);
                mHeaderProgressBar.setVisibility(View.GONE);
                break;
            case STATUS_LOADING:
                mHeaderTextView.setText(R.string.enhance_list_footer_loading_text);
                break;
        }
    }

    private void updateFooter(int status) {
        switch (status) {
            case STATUS_NORMAL:
                if (hasMore) {
                    mFooterTextView.setText(R.string.enhance_list_footer_more_text);
                } else {
                    mFooterTextView.setText(R.string.enhance_list_footer_no_more_text);
                }
                mFooterProgressBar.setVisibility(View.GONE);
                break;
            case STATUS_DROP_DOWN_REFRESH:
            case STATUS_RELEASE_REFRESH:
                break;
            case STATUS_REFRESHING:
                mFooterTextView.setText(R.string.enhance_list_header_refreshing_text);
                break;
            case STATUS_REFRESH_COMPLETE:
                break;
            case STATUS_LOADING:
                mFooterTextView.setText(R.string.enhance_list_footer_loading_text);
                mFooterProgressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    // 此方法执行后getMeasuredHeight才能获取到正确的值
    private void measureHeaderLayout(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public interface OnRefreshListener {
        public void onRefresh(int itemCount);
    }

    public interface OnLoadMoreListener {
        public void onLoadMore(int pageNum, int pageSize);
    }

    private void log(String msg) {
        Log.e(LOG_TAG, msg);
    }

}
