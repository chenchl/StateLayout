package cn.chenchl.statelayout;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import cn.chenchl.statelayout.databinding.ActivityMain2Binding;
import cn.chenchl.statelayoutlibrary.StateLayout;

public class Main2Activity extends AppCompatActivity {

    private ActivityMain2Binding activityMain2Binding;
    private StateLayout stateLayout;
    private StateLayout stateLayout2;
    private StateLayout stateLayout3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMain2Binding = DataBindingUtil.setContentView(this, R.layout.activity_main2);
        setSupportActionBar(activityMain2Binding.toolbar);
        //setTitleCenter(activityMain2Binding.toolbar);

        activityMain2Binding.c1.post(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });


       /* activityMain2Binding.toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                activityMain2Binding.toolbar.inflateMenu(R.menu.menu_main);
            }
        },3500);
*/
       /* stateLayout = new StateLayout(this)
                .setOnRetryClickListener(new StateLayout.OnRetryClickListener() {
                    @Override
                    public void toRetry() {
                        Toast.makeText(Main2Activity.this, "HAHAH", Toast.LENGTH_SHORT).show();
                    }
                })
                .setEnableLoadingAlpha(true)
                .setAnimDuration(300)
                .with(this)
                .showLoading("主页加载中");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stateLayout.showContent();
            }
        },2000);*/
        stateLayout2 = new StateLayout(this)
                .setOnRetryClickListener(new StateLayout.OnRetryClickListener() {
                    @Override
                    public void toRetry() {
                        Toast.makeText(Main2Activity.this, "HAHAH1", Toast.LENGTH_SHORT).show();
                    }
                })
                .setAnimDuration(500)
                .with(findViewById(R.id.view_content));
        activityMain2Binding.btnLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout2.showLoading("view加载中");
            }
        });
        activityMain2Binding.btnEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout2.showEmpty("view空的啊");
            }
        });
        activityMain2Binding.btnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout2.showError("view错了");
            }
        });
        activityMain2Binding.btnContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout2.showContent();
            }
        });
        stateLayout3 = new StateLayout(this)
                .setOnRetryClickListener(new StateLayout.OnRetryClickListener() {
                    @Override
                    public void toRetry() {
                        Toast.makeText(Main2Activity.this, "HAHAH1", Toast.LENGTH_SHORT).show();
                    }
                })
                .setLoadingResID(R.layout.custom_loading)
                .setEmptyResID(R.layout.custom_empty)
                .setErrorResID(R.layout.custom_error)
                .setAnimDuration(500)
                .setUseContentBgWhenLoading(true)
                .with(findViewById(R.id.login));
        activityMain2Binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout3.showLoading("");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stateLayout3.showContent();
                    }
                },3000);
            }
        });
        activityMain2Binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Main2Activity.this, "HAHAH123", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(Main2Activity.this, "MenuItem "+getResources().getResourceName(item.getItemId()), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityCompat.finishAfterTransition(this);
                break;
        }
        return true;
    }

    public void setTitleCenter(Toolbar toolbar) {
        String title = "title";
        final CharSequence originalTitle = toolbar.getTitle();
        toolbar.setTitle(title);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (title.equals(textView.getText())) {
                    textView.setGravity(Gravity.CENTER);
                    Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) textView.getLayoutParams();
                    if (layoutParams == null) {
                        layoutParams = new Toolbar.LayoutParams(Gravity.CENTER);
                    } else {
                        layoutParams.gravity = Gravity.CENTER;
                    }
                    textView.setLayoutParams(layoutParams);
                }
            }
            toolbar.setTitle(originalTitle);
        }
    }
}
