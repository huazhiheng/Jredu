package com.example.heng.jredu.jredu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.heng.jredu.R;
import com.example.heng.jredu.entity.UserDemo;
import com.example.heng.jredu.util.PreLoginUtil;
import com.example.heng.jredu.util.StringPostRequest;
import com.example.heng.jredu.util.SystemBarTintManager;
import com.example.heng.jredu.util.UrlUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.android.volley.Response.ErrorListener;
import static com.android.volley.Response.Listener;


public class MainActivity_zc extends Activity {
    private TextView userName, userPwd, userNick, usersex;
    private Button by_zc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_zc);

        userName = (TextView) findViewById(R.id.userName);
        userPwd = (TextView) findViewById(R.id.userPwd);
        userNick = (TextView) findViewById(R.id.userNick);
        usersex = (TextView) findViewById(R.id.usersex);
        by_zc = (Button) findViewById(R.id.bt_zc);
        by_zc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "用户名不能为空", Toast.LENGTH_LONG).show();
                } else if (userPwd.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_LONG).show();
                } else {
                    UserDemo userDemo = new UserDemo();
                    String nickname = userNick.getText().toString();
                    String sexid = usersex.getText().toString();
                    String date = new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));//获取当前日期
                    String name = userName.getText().toString();
                    String pwd = userPwd.getText().toString();
                    userDemo.setNickName(nickname);
                    userDemo.setRgdtDate(date);
                    userDemo.setSexId(sexid);
                    userDemo.setUname(name);
                    userDemo.setUpwd(pwd);
                    inster(userDemo);
                }


            }
        });

        setTranslucentStatus();

        findViewById(R.id.left_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void inster(UserDemo userDemo) {
        String url = UrlUtil.UserLogin;
        StringPostRequest str = new StringPostRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    Gson gson = new Gson();
                    UserDemo user = gson.fromJson(s, UserDemo.class);
                    if (user.getUname().equals("")) {
                        Toast.makeText(getApplicationContext(), "账号已存在，请重新输入", Toast.LENGTH_LONG).show();
                    } else {
                        MyApplaction.getMyApplaction().setUser(user);
                        //将账号密码在登陆时存到本地
                        PreLoginUtil.putString(getBaseContext(), "name", user.getUname());
                        PreLoginUtil.putString(getBaseContext(), "pwd", user.getUpwd());
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        str.putParams("uname", userDemo.getUname());
        str.putParams("upwd", userDemo.getUpwd());
        str.putParams("unickname", userDemo.getNickName());
        str.putParams("sexid", userDemo.getSexId());
        str.putParams("rgdtDate", userDemo.getRgdtDate());
        str.putParams("flag", "2");

        MyApplaction.getMyApplaction().getRequestQueue().add(str);
    }


    //沉浸式状态栏
    public void setTranslucentStatus() {
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.tongzhilan);

    }


}
