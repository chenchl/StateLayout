# StateLayout
布局动态状态管理（loading error empty normal）

## 使用方法

- 

## 核心原理

```java
if (view.getParent() == null) {
            addView(view, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
} else {
            //将content从其父viewgroup中先移出
            ViewGroup parent = (ViewGroup) view.getParent();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            int index = parent.indexOfChild(view);
            parent.removeView(view);
            //把content包裹进statelayout
            addView(view, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            //把statelayout放进view的父viewgroup中
            parent.addView(this, index, layoutParams);
}
```
