package com.example.heng.jredu.jredu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
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

import java.util.List;

public class MainActivity_on extends Activity {
    private Button dl_bt;
    private TextView zc_bt;
    private TextView zh, mm;
    private CheckBox dl_jz, dl_zd;
    private List<UserDemo> list;
    private String remember_01 = "no";
    private String remember_02 = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dl_bt = (Button) findViewById(R.id.loginBtn);
        zh = (TextView) findViewById(R.id.userName);
        mm = (TextView) findViewById(R.id.userPwd);
        dl_jz = (CheckBox) findViewById(R.id.cb_remPwd);
        dl_zd = (CheckBox) findViewById(R.id.cb_antoLogin);
        zc_bt = (TextView) findViewById(R.id.registBtn);

        zc_bt.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        zc_bt.getPaint().setAntiAlias(true);//抗锯齿


        findViewById(R.id.left_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //注册
        zc_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity_on.this, MainActivity_zc.class);
                startActivity(it);
            }
        });
        //自动登录
        if (PreLoginUtil.gettString(getBaseContext(), "remember_02").equals("yes")) {
            dl_zd.setChecked(true);//使选中的对勾一直存在
        } else {
            dl_zd.setChecked(false);
            //登录实现
            dl_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (zh.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_LONG).show();
                    } else if (mm.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_LONG).show();
                    } else {
                        String uno = zh.getText() + "";
                        String pwd = mm.getText() + "";
                        login(uno, pwd);
                    }
                }
            });
        }
        setTranslucentStatus();
    }

    private void login(String uno, String pwd) {
        String url_Login = UrlUtil.UserLogin;
        StringPostRequest strR = new StringPostRequest(url_Login, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    if (!jsonObject.has("info")) {
                        Gson gson = new Gson();
                        UserDemo user = gson.fromJson(s, UserDemo.class);
                        MyApplaction.getMyApplaction().setUser(user);
                        //将账号密码在登陆时存到本地
                        PreLoginUtil.putString(getBaseContext(), "name", user.getUname());
                        PreLoginUtil.putString(getBaseContext(), "pwd", user.getUpwd());
                        //记住密码
                        if (dl_jz.isChecked() && remember_01.equals("no")) {
                            remember_01 = "yes";
                            PreLoginUtil.putString(getBaseContext(), "remember_01", remember_01);
                        } else {
                            PreLoginUtil.putString(getBaseContext(), "remember_01", remember_01);
                        }
                        //自动登录
                        if (dl_zd.isChecked() && remember_02.equals("no")) {
                            remember_02 = "yes";
                            PreLoginUtil.putString(getBaseContext(), "remember_02", remember_02);
                        } else {
                            PreLoginUtil.putString(getBaseContext(), "remember_02", remember_02);
                        }

                        if (!user.getUname().equals("")) {
                            MyApplaction.getMyApplaction().setUser(user);
                            finish();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "账号密码错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        strR.putParams("uname", uno);
        strR.putParams("upwd", pwd);
        strR.putParams("flag", "1");

        MyApplaction.getMyApplaction().getRequestQueue().add(strR);

        //登陆按钮点击事件
        dl_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sturl = UrlUtil.UserLogin;
                StringPostRequest stringRequest = new StringPostRequest(sturl
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson();
                        UserDemo user = gson.fromJson(s, UserDemo.class);
                        if (user.getUname().equals("")) {
                            Toast.makeText(getApplication(), "用户名不存在", Toast.LENGTH_LONG).show();
                        } else {
                            finish();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplication(), "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
                stringRequest.putParams("uname", zh.getText().toString());
                stringRequest.putParams("upwd", mm.getText().toString());
                stringRequest.putParams("flag", "1");

                MyApplaction.getMyApplaction().getRequestQueue().add(stringRequest);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PreLoginUtil.gettString(getBaseContext(), "remember_01").equals("yes")) {
            dl_jz.setChecked(true);//使选中的对勾一直存在
            String name = PreLoginUtil.gettString(getBaseContext(), "name");
            zh.setText(name);
            String pwd = PreLoginUtil.gettString(getBaseContext(), "pwd");
            mm.setText(pwd);
        } else if (dl_jz.isChecked()) {
            remember_01 = "no";
            dl_jz.setChecked(false);
        }

    }

    //沉浸式状态栏
    public void setTranslucentStatus() {
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.tongzhilan);

    }

}
