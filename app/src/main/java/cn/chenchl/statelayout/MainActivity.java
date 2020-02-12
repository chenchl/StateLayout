package cn.chenchl.statelayout;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import cn.chenchl.statelayoutlibrary.StateLayout;

public class MainActivity extends AppCompatActivity {

    private StateLayout stateLayout;
    private StateLayout stateLayout2;
    private StateLayout stateLayout3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //针对contentview
        stateLayout = new StateLayout(this)
                .setOnRetryClickListener(new StateLayout.OnRetryClickListener() {
                    @Override
                    public void toRetry() {
                        Toast.makeText(MainActivity.this, "HAHAH", Toast.LENGTH_SHORT).show();
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
        },2000);
        //针对整个layout
        stateLayout2 = new StateLayout(this)
                .setOnRetryClickListener(new StateLayout.OnRetryClickListener() {
                    @Override
                    public void toRetry() {
                        Toast.makeText(MainActivity.this, "HAHAH1", Toast.LENGTH_SHORT).show();
                    }
                })
                .setAnimDuration(500)
                .with(findViewById(R.id.view_content));
        findViewById(R.id.btn_loading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout2.showLoading("view加载中");
            }
        });
        findViewById(R.id.btn_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout2.showEmpty("view空的啊");
            }
        });
        findViewById(R.id.btn_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout2.showError("view错了");
            }
        });
        findViewById(R.id.btn_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout2.showContent();
            }
        });
        //针对view
        stateLayout3 = new StateLayout(this)
                .setOnRetryClickListener(new StateLayout.OnRetryClickListener() {
                    @Override
                    public void toRetry() {
                        Toast.makeText(MainActivity.this, "HAHAH1", Toast.LENGTH_SHORT).show();
                    }
                })
                .setLoadingResID(R.layout.custom_loading)
                .setEmptyResID(R.layout.custom_empty)
                .setErrorResID(R.layout.custom_error)
                .setAnimDuration(500)
                .setUseContentBgWhenLoading(true)
                .with(findViewById(R.id.login));
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
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
    }
}
