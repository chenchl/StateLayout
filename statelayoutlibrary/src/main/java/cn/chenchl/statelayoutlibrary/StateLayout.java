package cn.chenchl.statelayoutlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * created by ccl on 2019/12/31
 * 状态管理布局
 **/
public class StateLayout extends FrameLayout {

    /**
     * 状态注解类
     */
    @IntDef({State.NONE, State.LOADING, State.EMPTY, State.CONTENT, State.ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        int NONE = 0;
        int LOADING = 1;
        int EMPTY = 2;
        int CONTENT = 3;
        int ERROR = 4;
    }

    private int mState = State.NONE;
    private View loadingView;
    private View emptyView;
    private View errorView;
    @Nullable
    private View contentView;
    private LayoutInflater mLayoutInflater;
    private long animDuration = 300l;
    private boolean enableLoadingAlpha = false;
    private boolean useContentBgWhenLoading = false;

    //重试点击监听
    public interface OnRetryClickListener {
        void toRetry();
    }

    private OnRetryClickListener onRetryClickListener;

    public StateLayout setOnRetryClickListener(OnRetryClickListener onRetryClickListener) {
        this.onRetryClickListener = onRetryClickListener;
        return this;
    }

    public StateLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public StateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (contentView == null) {
            throw new IllegalArgumentException("contentView can not be null!");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mState == State.LOADING && loadingView.getVisibility() == VISIBLE) //防止加载时点击穿透造成bug
            return true;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(animRunnable);
    }

    private void init() {
        mLayoutInflater = LayoutInflater.from(getContext());
        loadingView = mLayoutInflater.inflate(R.layout.default_statelayout_loading, this, false);
        emptyView = mLayoutInflater.inflate(R.layout.default_statelayout_empty, this, false);
        errorView = mLayoutInflater.inflate(R.layout.default_statelayout_error, this, false);
    }

    /**
     * 包裹指定activity contentview
     *
     * @param activity
     * @return
     */
    public StateLayout with(Activity activity) {
        View contentView = activity.findViewById(android.R.id.content);
        if (contentView != null && contentView instanceof ViewGroup) {
            return with(((ViewGroup) contentView).getChildAt(0));
        } else {
            throw new ClassCastException("null cannot be cast to android.view.ViewGroup");
        }
    }

    /**
     * 包裹指定fragment
     *
     * @param fragment
     * @return
     */
    public StateLayout with(Fragment fragment) {
        return with(fragment.getView());
    }

    /**
     * 包裹指定view
     *
     * @param view
     * @return
     */
    public StateLayout with(View view) {
        if (view == null)
            throw new IllegalArgumentException("view can not be null");
        emptyView.setVisibility(GONE);
        emptyView.setAlpha(0f);
        addView(emptyView);

        loadingView.setVisibility(GONE);
        loadingView.setAlpha(0f);
        addView(loadingView);

        errorView.setVisibility(GONE);
        errorView.setAlpha(0f);
        if (errorView.findViewById(R.id.tv_retry) == null) {
            errorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    retryInner();
                }
            });
        } else
            errorView.findViewById(R.id.tv_retry).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    retryInner();
                }
            });
        addView(errorView);

        view.setVisibility(INVISIBLE);
        view.setAlpha(0f);
        if (view.getParent() == null) {
            addView(view, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else {
            //降content从其父viewgroup中先移出
            ViewGroup parent = (ViewGroup) view.getParent();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            int index = parent.indexOfChild(view);
            parent.removeView(view);
            //把content包裹进statelayout
            addView(view, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            //把statelayout放进view的父viewgroup中
            parent.addView(this, index, layoutParams);
        }
        contentView = view;
        showLayout(State.CONTENT);
        return this;
    }

    private void showLayout(int state) {
        if (state == mState)
            return;
        mState = state;
        switch (mState) {
            case State.CONTENT:
                postAnim(contentView);
                if (useContentBgWhenLoading && contentView != null && contentView.getBackground() != null) {
                    setBackground(contentView.getBackground());
                }
                if (enableLoadingAlpha) {
                    loadingView.setBackgroundColor(Color.parseColor("#66000000"));//添加半透明背景
                } else {
                    loadingView.setBackground(null);
                }
                break;
            case State.EMPTY:
                postAnim(emptyView);
                break;
            case State.ERROR:
                postAnim(errorView);
                break;
            case State.LOADING:
                postAnim(loadingView);
                break;
            default:
                break;
        }
    }

    private Runnable animRunnable;

    private void postAnim(final View view) {
        removeCallbacks(animRunnable);
        animRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < getChildCount(); i++) {
                    if (mState == State.LOADING && enableLoadingAlpha && getChildAt(i) == contentView)//当时loading状态 且是透明loading时排除contentView
                        continue;
                    hideAnim(getChildAt(i));
                }
                showAnim(view);
            }
        };
        post(animRunnable);
    }

    /**
     * 渐变展示
     *
     * @param view
     */
    private void showAnim(final View view) {
        if (view == null)
            return;
        view.animate().cancel();
        view.animate().alpha(1f)
                .setDuration(animDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(VISIBLE);
                    }
                })
                .start();
    }

    /**
     * 渐变隐藏
     *
     * @param view
     */
    private void hideAnim(final View view) {
        if (view == null)
            return;
        view.animate().cancel();
        view.animate().alpha(0f)
                .setDuration(animDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(INVISIBLE);
                    }
                })
                .start();
    }

    public StateLayout showContent() {
        showLayout(State.CONTENT);
        return this;
    }

    public StateLayout showLoading(String text) {
        showLayout(State.LOADING);
        TextView tvLoading = loadingView.findViewById(R.id.tv_Loading);
        if (tvLoading != null)
            if (TextUtils.isEmpty(text))
                tvLoading.setVisibility(GONE);
            else {
                tvLoading.setVisibility(VISIBLE);
                tvLoading.setText(text);
            }
        return this;
    }

    public StateLayout showError(String text) {
        showLayout(State.ERROR);
        TextView tvRetry = errorView.findViewById(R.id.tv_retry);
        if (tvRetry != null)
            if (TextUtils.isEmpty(text))
                tvRetry.setVisibility(GONE);
            else {
                tvRetry.setVisibility(VISIBLE);
                tvRetry.setText(text);
            }
        return this;
    }

    public StateLayout showEmpty(String text) {
        showLayout(State.EMPTY);
        TextView tvEmpty = emptyView.findViewById(R.id.tv_empty);
        if (tvEmpty != null)
            if (TextUtils.isEmpty(text))
                tvEmpty.setVisibility(GONE);
            else {
                tvEmpty.setVisibility(VISIBLE);
                tvEmpty.setText(text);
            }
        return this;
    }

    private void retryInner() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onRetryClickListener != null)
                    onRetryClickListener.toRetry();
            }
        }, animDuration);
    }

    //参数设置
    public StateLayout setLoadingResID(int resID) {
        if (resID != 0) {
            loadingView = mLayoutInflater.inflate(resID, this, false);
            ViewGroup.LayoutParams layoutParams = loadingView.getLayoutParams();
            if (layoutParams != null && layoutParams instanceof FrameLayout.LayoutParams) {
                ((LayoutParams) layoutParams).gravity = Gravity.CENTER;
            }
        }
        return this;
    }

    public StateLayout setEmptyResID(int resID) {
        if (resID != 0) {
            emptyView = mLayoutInflater.inflate(resID, this, false);
            ViewGroup.LayoutParams layoutParams = emptyView.getLayoutParams();
            if (layoutParams != null && layoutParams instanceof FrameLayout.LayoutParams) {
                ((LayoutParams) layoutParams).gravity = Gravity.CENTER;
            }
        }
        return this;
    }

    public StateLayout setErrorResID(int resID) {
        if (resID != 0) {
            errorView = mLayoutInflater.inflate(resID, this, false);
            ViewGroup.LayoutParams layoutParams = errorView.getLayoutParams();
            if (layoutParams != null && layoutParams instanceof FrameLayout.LayoutParams) {
                ((LayoutParams) layoutParams).gravity = Gravity.CENTER;
            }
        }
        return this;
    }

    public StateLayout setAnimDuration(long animDuration) {
        this.animDuration = animDuration;
        return this;
    }

    public StateLayout setEnableLoadingAlpha(boolean enableLoadingAlpha) {
        this.enableLoadingAlpha = enableLoadingAlpha;
        return this;
    }

    public StateLayout setUseContentBgWhenLoading(boolean useContentBgWhenLoading) {
        this.useContentBgWhenLoading = useContentBgWhenLoading;
        return this;
    }
}
