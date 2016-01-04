package com.example.heng.jredu.jredu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.heng.jredu.R;
import com.example.heng.jredu.entity.UserDemo;
import com.example.heng.jredu.util.ImageLoaderUtil;
import com.example.heng.jredu.util.PreLoginUtil;
import com.example.heng.jredu.util.SystemBarTintManager;

public class MainActivity_grcentre extends Activity {
    private ImageView face;
    private TextView name, right_btn;
    private UserDemo userDemo;
    private String remember_01, remember_02;
    private RelativeLayout left_icon;

    private ImageView image_big;
    private PopupWindow popupWindow;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_grcentre);

        face = (ImageView) findViewById(R.id.face);
        left_icon = (RelativeLayout) findViewById(R.id.left_icon);
        right_btn = (TextView) findViewById(R.id.right_btn);
        name = (TextView) findViewById(R.id.name);

        left_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remember_02 = "no";
                PreLoginUtil.putString(getBaseContext(), "remember_02", remember_02);
                finish();
            }
        });
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity_grcentre.this, MainActivity_grzl.class);
                startActivity(it);
            }
        });

        setTranslucentStatus();

        view = this.getLayoutInflater().inflate(R.layout.popwindow_big_image, null);
        image_big = (ImageView) view.findViewById(R.id.image_big);

        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow = getPopwindow(view);

            }
        });

    }


    public PopupWindow getPopwindow(View view) {
        popupWindow = new PopupWindow(view,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.2f;
        getWindow().setAttributes(lp);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAtLocation(findViewById(R.id.face), Gravity.CENTER, 0, 0);
        //设置当popupWindow消失的时候背景为透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        return popupWindow;
    }

    @Override
    protected void onStart() {
        super.onStart();
        userDemo = MyApplaction.getMyApplaction().getUser();
        name.setText(userDemo.getNickName());
        ImageLoaderUtil.display(userDemo.getPhotoUri(), face);
        ImageLoaderUtil.display(userDemo.getPhotoUri(), image_big);
    }

    //沉浸式状态栏
    public void setTranslucentStatus() {
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.tongzhilan);

    }

}
