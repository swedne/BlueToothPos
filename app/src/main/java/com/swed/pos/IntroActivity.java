package com.swed.pos;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.swed.pos.model.QueryResult;
import com.swed.pos.model.StringBean;
import com.swed.pos.myapplication.R;
import com.swed.pos.net.BusinessAPI;
import com.swed.pos.net.OkHttpManager;


/**
 * 启动页
 */
public class IntroActivity extends AppCompatActivity {
    ImageView imgIntro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_intro);
        imgIntro = findViewById(R.id.iv_intro);
        isShowIntro();
    }

    private void isShowIntro() {
        BusinessAPI.getIntro(new OkHttpManager.HttpCallback(StringBean.class) {
            @Override
            public void onResponse(QueryResult result) {
                StringBean stringBean = (StringBean) result;
                if (stringBean.getDataMsg().getIs_show()) {
                    startApp(stringBean.getDataMsg().getShow_time());
                } else {
                    toHome();
                }
            }

            @Override
            public void onFailure(String error) {
                toHome();
            }
        });
    }

    private void startApp(int delayMillis) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                toHome();
            }
        }, delayMillis);
    }

    private void toHome() {
        Intent intent = new Intent(this, OtherWebActivity.class);
        startActivity(intent);
        finish();
    }
}
