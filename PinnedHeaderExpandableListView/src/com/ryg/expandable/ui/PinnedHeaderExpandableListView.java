/**
The MIT License (MIT)

Copyright (c) 2014 singwhatiwanna
https://github.com/singwhatiwanna
http://blog.csdn.net/singwhatiwanna

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.ryg.expandable.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.AbsListView.OnScrollListener;

public class PinnedHeaderExpandableListView extends ExpandableListView implements OnScrollListener {
    private static final String TAG = "PinnedHeaderExpandableListView";

    public interface OnHeaderUpdateListener {
        /**
         * 采用单例模式返回同一个view对象即可
         * 注意：view必须要有LayoutParams
         */
        public View getPinnedHeader();

        public void updatePinnedHeader(int firstVisibleGroupPos);
    }

    private View mHeaderView;
    private int mHeaderWidth;
    private int mHeaderHeight;

    private OnScrollListener mScrollListener;
    private OnHeaderUpdateListener mHeaderUpdateListener;

    private boolean mActionDownHappened = false;


    public PinnedHeaderExpandableListView(Context context) {
        super(context);
        initView();
    }

    public PinnedHeaderExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PinnedHeaderExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setFadingEdgeLength(0);
        setOnScrollListener(this);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        if (l != this) {
            mScrollListener = l;
        }
        super.setOnScrollListener(this);
    }
    
    public void setOnHeaderUpdateListener(OnHeaderUpdateListener listener) {
        mHeaderUpdateListener = listener;
        if (listener == null) {
            return;
        }
        mHeaderView = listener.getPinnedHeader();
        int firstVisiblePos = getFirstVisiblePosition();
        int firstVisibleGroupPos = getPackedPositionGroup(getExpandableListPosition(firstVisiblePos));
        listener.updatePinnedHeader(firstVisibleGroupPos);
        requestLayout();
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView == null) {
            return;
        }
        measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
        mHeaderWidth = mHeaderView.getMeasuredWidth();
        mHeaderHeight = mHeaderView.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mHeaderView == null) {
            return;
        }
        mHeaderView.layout(0, 0, mHeaderWidth, mHeaderHeight);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderView != null) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        Log.d(TAG, "dispatchTouchEvent");
        int pos = pointToPosition(x, y);
        if (y >= mHeaderView.getTop() && y <= mHeaderView.getBottom()) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                mActionDownHappened = true;
            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                int groupPosition = getPackedPositionGroup(getExpandableListPosition(pos));
                if (groupPosition != INVALID_POSITION && mActionDownHappened) {
                    if (isGroupExpanded(groupPosition)) {
                        collapseGroup(groupPosition);
                    } else {
                        expandGroup(groupPosition);
                    }
                    mActionDownHappened = false;
                }
                
            }
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    protected void refreshHeader() {
        if (mHeaderView == null) {
            return;
        }
        int firstVisiblePos = getFirstVisiblePosition();
        int pos = firstVisiblePos + 1;
        int firstVisibleGroupPos = getPackedPositionGroup(getExpandableListPosition(firstVisiblePos));
        int group = getPackedPositionGroup(getExpandableListPosition(pos));

        if (group == firstVisibleGroupPos + 1) {
            View view = getChildAt(1);
            if (view.getTop() <= mHeaderHeight) {
                int delta = mHeaderHeight - view.getTop();
                mHeaderView.layout(0, -delta, mHeaderWidth, mHeaderHeight - delta);
            }
        } else {
            mHeaderView.layout(0, 0, mHeaderWidth, mHeaderHeight);
        }

        if (mHeaderUpdateListener != null) {
            mHeaderUpdateListener.updatePinnedHeader(firstVisibleGroupPos);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mHeaderView != null && scrollState == SCROLL_STATE_IDLE) {
            int firstVisiblePos = getFirstVisiblePosition();
            if (firstVisiblePos == 0) {
                mHeaderView.layout(0, 0, mHeaderWidth, mHeaderHeight);
            }
        }
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        if (totalItemCount > 0) {
            refreshHeader();
        }
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

}
