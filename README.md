PinnedHeaderExpandableListView
==============================
##前言
Android中，大家都用过ListView，ExpandableListView等，也许你还用过PinnedHeaderListView，但是如果我说PinnedHeaderExpandableListView，你听过吗？还有可下拉的PinnedHeaderExpandableListView呢？没听过也不要紧，本文就是介绍这个东西的，为了让大家有更直观的了解，先上效果图。通过效果图可以看出，首先它是一个ExpandableListView，但是它的头部可以固定，其次，在它的上面还有一个头部可以来回伸缩，恩，这就是本文要介绍的自定义view。为了提高复用性，这个效果我分成来了2个view来实现，第一个是PinnedHeaderExpandableListView来实现头部固定的ExpandableListView，第二个view是StickyLayout，这个view具有一个可以上下滑动的头部，最后将这2个view组合在一起，就达到了如下的效果。

![mahua](http://img.blog.csdn.net/20140511151546843?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2luZ3doYXRpd2FubmE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

##接口
```java
    public interface OnHeaderUpdateListener {
        /**
         * 返回一个view对象即可
         * 注意：view必须要有LayoutParams
         */
        public View getPinnedHeader();

        public void updatePinnedHeader(View headerView, int firstVisibleGroupPos);
    }
    
    public interface OnGiveUpTouchEventListener {
        public boolean giveUpTouchEvent(MotionEvent event);
    }
```
##如何使用
让你的activity实现OnHeaderUpdateListener, OnGiveUpTouchEventListener两个接口，
分别为PinnedHeaderExpandableListView中如何绘制和更新固定的头部以及StickyLayout中content何时放弃事件处理。
```java
    @Override
    public View getPinnedHeader() {
        View headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.group, null);
        headerView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        Group firstVisibleGroup = (Group) adapter.getGroup(firstVisibleGroupPos);
        TextView textView = (TextView) headerView.findViewById(R.id.group);
        textView.setText(firstVisibleGroup.getTitle());
    }

    @Override
    public boolean giveUpTouchEvent(MotionEvent event) {
        if (expandableListView.getFirstVisiblePosition() == 0) {
            View view = expandableListView.getChildAt(0);
            if (view != null && view.getTop() >= 0) {
                return true;
            }
        }
        return false;
    }
```
##许可协议
采用MIT共享协议发布
##更详细的介绍
[可下拉的PinnedHeaderExpandableListView的实现](http://blog.csdn.net/singwhatiwanna/article/details/25546871)
