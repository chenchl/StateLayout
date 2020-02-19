# StateLayout
方便的布局动态状态管理库，链式调用一行代码搞定布局状态切换（loading error empty normal）

[![](https://jitpack.io/v/chenchl/StateLayout.svg)](https://jitpack.io/#chenchl/StateLayout)

## 使用方法

- **step1：**You need add the jitpack.io repository

  ```java
  allprojects {
   repositories {
      ....
      maven { url "https://jitpack.io" }
   }
  }
  ```

- **step2：**Add the dependencies in your module

  ```java
  dependencies {
  		implementation 'com.github.User:Repo:1.0.0'
  	}
  ```

- **step3：**Now, enjoy it

  ```java
  new StateLayout(this)
      .setOnRetryClickListener(new StateLayout.OnRetryClickListener() {
          @Override
          public void toRetry() {
              ...
          }
      })//重试点击事件回调
      .setEnableLoadingAlpha(true)//加载状态是否需要半透明显示，默认不需要false
      .setUseContentBgWhenLoading(true)//是否使用正文背景作为loading状态的背景 默认false
      .setErrorResID()//设置加载错误状态布局
      .setEmptyResID()//设置空状态布局
      .setLoadingResID()//设置加载中状态布局
      .setAnimDuration(500)//设置状态切换时渐变动画持续时间
      .with(this)//设置需要包裹的对象 可以使activity、fragment以及任意View
      .showLoading("主页加载中")//显示加载中状态
      .showEmpty()//显示空状态
      .showError()//显示加载失败状态
      .showContent();//显示正文
  ```

  

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
